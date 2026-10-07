const Scan = require('../models/Scan');
const ComplianceService = require('../services/complianceService');
const AIService = require('../services/aiService');

exports.createScan = async (req, res, next) => {
  try {
    const {
      clientScanId,
      deviceId,
      rulesetId = 'IN-PCR2011-v2011',
      productName,
      brandName,
      manufacturerName,
      mrp,
      netQuantity,
      measuredFontHeightMm = 0.0,
      barcode,
      qrCode,
      rawOcrText = '',
      captureMode = 'ONLINE',
      hasManufacturer = true,
      hasOrigin = true
    } = req.body;

    if (!clientScanId || !productName) {
      return res.status(400).json({
        success: false,
        error: {
          code: 'MISSING_REQUIRED_FIELDS',
          message: 'clientScanId and productName are required fields.'
        }
      });
    }

    // 1. Evaluate package against Statutory Ruleset Engine
    const evalResult = await ComplianceService.evaluate({
      rulesetId,
      productName,
      mrp,
      netQuantity,
      fontHeightMm: measuredFontHeightMm,
      hasManufacturer,
      hasOrigin,
      rawOcrText
    });

    // 2. Optional AI enrichment
    const aiAnalysis = await AIService.analyzePackagingImage({ rulesetId });

    // 3. Upsert Scan Idempotently using clientScanId
    const scan = await Scan.findOneAndUpdate(
      { clientScanId },
      {
        clientScanId,
        deviceId: deviceId || 'DEV-FIELD-01',
        userId: req.user ? req.user._id : null,
        rulesetId: evalResult.rulesetId,
        productName,
        brandName: brandName || '',
        manufacturerName: manufacturerName || '',
        barcode: barcode || '',
        qrCode: qrCode || '',
        mrp: mrp !== undefined ? mrp : null,
        netQuantity: netQuantity || '',
        ocrText: rawOcrText,
        aiAnalysis,
        violations: evalResult.violations,
        overallVerdict: evalResult.overallVerdict,
        complianceScore: evalResult.complianceScore,
        captureMode,
        syncStatus: 'SYNCED',
        capturedAt: req.body.capturedAt ? new Date(req.body.capturedAt) : new Date()
      },
      { upsert: true, new: true, setDefaultsOnInsert: true }
    );

    res.status(201).json({
      success: true,
      data: scan,
      message: 'Scan evaluated and saved successfully.'
    });
  } catch (error) {
    next(error);
  }
};

exports.syncOfflineScans = async (req, res, next) => {
  try {
    const { scans } = req.body;
    if (!Array.isArray(scans) || scans.length === 0) {
      return res.status(400).json({
        success: false,
        error: { code: 'EMPTY_BATCH', message: 'No scan items provided in synchronization array.' }
      });
    }

    const syncResults = [];

    for (const item of scans) {
      if (!item.clientScanId) continue;

      const evalResult = await ComplianceService.evaluate({
        rulesetId: item.rulesetId || 'IN-PCR2011-v2011',
        productName: item.productName || 'Packaged Commodity',
        mrp: item.mrp,
        netQuantity: item.netQuantity,
        fontHeightMm: item.measuredFontHeightMm || 0.0,
        hasManufacturer: item.hasManufacturer !== false,
        rawOcrText: item.rawOcrText || ''
      });

      const updated = await Scan.findOneAndUpdate(
        { clientScanId: item.clientScanId },
        {
          ...item,
          userId: req.user ? req.user._id : null,
          overallVerdict: evalResult.overallVerdict,
          complianceScore: evalResult.complianceScore,
          violations: evalResult.violations,
          syncStatus: 'SYNCED',
          updatedAt: new Date()
        },
        { upsert: true, new: true, setDefaultsOnInsert: true }
      );

      syncResults.push({
        clientScanId: item.clientScanId,
        status: 'SYNCED',
        verdict: updated.overallVerdict
      });
    }

    res.json({
      success: true,
      data: {
        processedCount: syncResults.length,
        items: syncResults
      },
      message: 'Batch offline synchronization completed idempotently.'
    });
  } catch (error) {
    next(error);
  }
};

exports.getScans = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 20;
    const search = req.query.search;
    const verdict = req.query.verdict;
    const rulesetId = req.query.rulesetId;

    const filter = {};
    if (verdict) filter.overallVerdict = verdict;
    if (rulesetId) filter.rulesetId = rulesetId;
    if (search) {
      filter.$or = [
        { productName: { $regex: search, $options: 'i' } },
        { brandName: { $regex: search, $options: 'i' } },
        { barcode: { $regex: search, $options: 'i' } },
        { clientScanId: { $regex: search, $options: 'i' } }
      ];
    }

    const [scans, total] = await Promise.all([
      Scan.find(filter)
        .sort({ createdAt: -1 })
        .skip((page - 1) * limit)
        .limit(limit)
        .populate('userId', 'name badgeNumber email'),
      Scan.countDocuments(filter)
    ]);

    res.json({
      success: true,
      data: scans,
      pagination: {
        page,
        limit,
        total,
        pages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    next(error);
  }
};

exports.getScanById = async (req, res, next) => {
  try {
    const scan = await Scan.findOne({
      $or: [{ _id: req.params.id }, { clientScanId: req.params.id }]
    }).populate('userId', 'name badgeNumber email');

    if (!scan) {
      return res.status(404).json({
        success: false,
        error: { code: 'SCAN_NOT_FOUND', message: 'Inspection scan record not found.' }
      });
    }

    res.json({
      success: true,
      data: scan
    });
  } catch (error) {
    next(error);
  }
};

exports.deleteScan = async (req, res, next) => {
  try {
    const scan = await Scan.findOneAndDelete({
      $or: [{ _id: req.params.id }, { clientScanId: req.params.id }]
    });

    if (!scan) {
      return res.status(404).json({
        success: false,
        error: { code: 'SCAN_NOT_FOUND', message: 'Scan not found.' }
      });
    }

    res.json({
      success: true,
      message: 'Scan record deleted successfully.'
    });
  } catch (error) {
    next(error);
  }
};
