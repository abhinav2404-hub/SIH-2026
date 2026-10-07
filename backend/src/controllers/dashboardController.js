const Scan = require('../models/Scan');
const Complaint = require('../models/Complaint');
const Device = require('../models/Device');
const Ruleset = require('../models/Ruleset');

exports.getSummary = async (req, res, next) => {
  try {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const [totalScans, scansToday, compliantCount, nonCompliantCount, openComplaints, activeDevices, activeRulesets] = await Promise.all([
      Scan.countDocuments(),
      Scan.countDocuments({ createdAt: { $gte: today } }),
      Scan.countDocuments({ overallVerdict: 'PASS' }),
      Scan.countDocuments({ overallVerdict: { $in: ['FAIL', 'REVIEW'] } }),
      Complaint.countDocuments({ status: 'OPEN' }),
      Device.countDocuments({ status: 'ACTIVE' }),
      Ruleset.countDocuments({ active: true })
    ]);

    const complianceRate = totalScans > 0 ? ((compliantCount / totalScans) * 100).toFixed(1) : '100.0';

    res.json({
      success: true,
      data: {
        totalScans,
        scansToday,
        compliantCount,
        nonCompliantCount,
        complianceRate: parseFloat(complianceRate),
        openComplaints,
        activeDevices,
        activeRulesets
      }
    });
  } catch (error) {
    next(error);
  }
};

exports.getViolationsBreakdown = async (req, res, next) => {
  try {
    const breakdown = await Scan.aggregate([
      { $unwind: '$violations' },
      { $group: { _id: '$violations.ruleId', count: { $sum: 1 }, name: { $first: '$violations.ruleRequirement' } } },
      { $sort: { count: -1 } },
      { $limit: 10 }
    ]);

    res.json({
      success: true,
      data: breakdown
    });
  } catch (error) {
    next(error);
  }
};

exports.getRecentScans = async (req, res, next) => {
  try {
    const recent = await Scan.find()
      .sort({ createdAt: -1 })
      .limit(10)
      .select('clientScanId productName brandName mrp netQuantity overallVerdict complianceScore createdAt syncStatus');

    res.json({
      success: true,
      data: recent
    });
  } catch (error) {
    next(error);
  }
};
