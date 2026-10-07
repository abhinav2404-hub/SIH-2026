const mongoose = require('mongoose');
const violationSchema = require('./Violation');

const scanSchema = new mongoose.Schema({
  clientScanId: {
    type: String,
    required: true,
    unique: true,
    index: true,
    trim: true
  },
  deviceId: { type: String, default: 'DEV-UNKNOWN', index: true },
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  rulesetId: {
    type: String,
    required: true,
    default: 'IN-PCR2011-v2011',
    index: true
  },
  productName: { type: String, required: true, trim: true },
  brandName: { type: String, default: '', trim: true },
  manufacturerName: { type: String, default: '', trim: true },
  productCategory: { type: String, default: 'General Packaged Commodity' },
  batchNumber: { type: String, default: '' },
  barcode: { type: String, default: '', index: true },
  qrCode: { type: String, default: '' },
  mrp: { type: Number, default: null },
  netQuantity: { type: String, default: '' },
  manufacturingDate: { type: String, default: '' },
  expiryDate: { type: String, default: '' },
  images: [{
    url: String,
    panel: { type: String, default: 'PDP' },
    hash: String
  }],
  ocrText: { type: String, default: '' },
  aiAnalysis: {
    model: { type: String, default: 'gemini-2.5-flash' },
    extractedFields: mongoose.Schema.Types.Mixed,
    reasoning: String
  },
  violations: [violationSchema],
  overallVerdict: {
    type: String,
    enum: ['PASS', 'FAIL', 'NOT_VERIFIABLE', 'REVIEW'],
    default: 'PASS',
    index: true
  },
  complianceScore: { type: Number, min: 0, max: 100, default: 100 },
  capturedAt: { type: Date, default: Date.now },
  captureMode: {
    type: String,
    enum: ['ONLINE', 'OFFLINE', 'SYNTHETIC', 'GALLERY'],
    default: 'ONLINE'
  },
  syncStatus: {
    type: String,
    enum: ['PENDING', 'SYNCED', 'FAILED'],
    default: 'SYNCED',
    index: true
  },
  locale: { type: String, default: 'en-IN' },
  countryCode: { type: String, default: 'IN' }
}, { timestamps: true });

// Multi-attribute compound indexes for fast filtering & reporting
scanSchema.index({ createdAt: -1 });
scanSchema.index({ deviceId: 1, createdAt: -1 });
scanSchema.index({ overallVerdict: 1, rulesetId: 1 });
scanSchema.index({ 'violations.ruleId': 1 });

module.exports = mongoose.model('Scan', scanSchema);
