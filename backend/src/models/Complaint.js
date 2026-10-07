const mongoose = require('mongoose');

const complaintSchema = new mongoose.Schema({
  complaintId: {
    type: String,
    required: true,
    unique: true,
    index: true
  },
  scanId: { type: mongoose.Schema.Types.ObjectId, ref: 'Scan', index: true },
  clientScanId: { type: String, default: '' },
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  deviceId: { type: String, default: '' },
  productName: { type: String, required: true },
  brandName: { type: String, default: '' },
  manufacturerName: { type: String, default: '' },
  retailerName: { type: String, default: '' },
  retailerAddress: { type: String, default: '' },
  category: {
    type: String,
    enum: ['OVERCHARGING_MRP', 'MISSING_MANDATORY_DECLARATION', 'EXPIRED_PRODUCT', 'DECEPTIVE_NET_QUANTITY', 'IMPROPER_FONT_SIZE', 'OTHER'],
    default: 'MISSING_MANDATORY_DECLARATION'
  },
  description: { type: String, required: true },
  evidenceImages: [{ type: String }],
  status: {
    type: String,
    enum: ['OPEN', 'UNDER_REVIEW', 'INVESTIGATION_ORDERED', 'RESOLVED', 'REJECTED'],
    default: 'OPEN',
    index: true
  },
  priority: {
    type: String,
    enum: ['HIGH', 'MEDIUM', 'LOW'],
    default: 'MEDIUM'
  },
  assignedOfficer: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  resolutionNotes: { type: String, default: '' }
}, { timestamps: true });

complaintSchema.index({ createdAt: -1 });

module.exports = mongoose.model('Complaint', complaintSchema);
