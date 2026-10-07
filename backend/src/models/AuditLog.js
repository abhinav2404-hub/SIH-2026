const mongoose = require('mongoose');

const auditLogSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  deviceId: { type: String, default: '' },
  action: { type: String, required: true },
  entityType: {
    type: String,
    enum: ['SCAN', 'RULESET', 'COMPLAINT', 'USER', 'DEVICE', 'AUTH', 'SYSTEM'],
    required: true,
    index: true
  },
  entityId: { type: String, default: '', index: true },
  details: { type: mongoose.Schema.Types.Mixed },
  ipAddress: { type: String, default: '127.0.0.1' },
  userAgent: { type: String, default: '' }
}, { timestamps: true });

auditLogSchema.index({ createdAt: -1 });
auditLogSchema.index({ entityType: 1, entityId: 1 });

module.exports = mongoose.model('AuditLog', auditLogSchema);
