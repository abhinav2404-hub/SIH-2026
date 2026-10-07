const Complaint = require('../models/Complaint');

exports.createComplaint = async (req, res, next) => {
  try {
    const { productName, description, category, retailerName, clientScanId } = req.body;
    if (!productName || !description) {
      return res.status(400).json({
        success: false,
        error: { code: 'MISSING_FIELDS', message: 'Product name and complaint description are required.' }
      });
    }

    const complaintId = `CMP-${Date.now().toString().slice(-6)}-${Math.floor(Math.random() * 900 + 100)}`;
    const complaint = await Complaint.create({
      complaintId,
      userId: req.user ? req.user._id : null,
      deviceId: req.body.deviceId || '',
      clientScanId: clientScanId || '',
      productName,
      brandName: req.body.brandName || '',
      category: category || 'MISSING_MANDATORY_DECLARATION',
      retailerName: retailerName || '',
      retailerAddress: req.body.retailerAddress || '',
      description,
      priority: req.body.priority || 'MEDIUM'
    });

    res.status(201).json({
      success: true,
      data: complaint,
      message: `Grievance registered successfully. Tracking reference: ${complaintId}`
    });
  } catch (error) {
    next(error);
  }
};

exports.getComplaints = async (req, res, next) => {
  try {
    const status = req.query.status;
    const filter = {};
    if (status) filter.status = status;

    const complaints = await Complaint.find(filter)
      .sort({ createdAt: -1 })
      .populate('userId', 'name email')
      .populate('assignedOfficer', 'name badgeNumber');

    res.json({
      success: true,
      data: complaints
    });
  } catch (error) {
    next(error);
  }
};

exports.updateComplaintStatus = async (req, res, next) => {
  try {
    const { status, resolutionNotes, assignedOfficer } = req.body;
    const complaint = await Complaint.findOneAndUpdate(
      { $or: [{ _id: req.params.id }, { complaintId: req.params.id }] },
      {
        ...(status && { status }),
        ...(resolutionNotes && { resolutionNotes }),
        ...(assignedOfficer && { assignedOfficer })
      },
      { new: true }
    );

    if (!complaint) {
      return res.status(404).json({
        success: false,
        error: { code: 'COMPLAINT_NOT_FOUND', message: 'Complaint not found.' }
      });
    }

    res.json({
      success: true,
      data: complaint,
      message: 'Complaint updated successfully.'
    });
  } catch (error) {
    next(error);
  }
};
