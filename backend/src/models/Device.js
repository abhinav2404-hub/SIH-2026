const mongoose = require('mongoose');

const deviceSchema = new mongoose.Schema({
  deviceId: {
    type: String,
    required: true,
    unique: true,
    index: true,
    trim: true
  },
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  deviceName: { type: String, default: 'Android Inspector Terminal' },
  model: { type: String, default: 'Android Device' },
  platform: { type: String, default: 'Android' },
  appVersion: { type: String, default: '5.0.0' },
  osVersion: { type: String, default: '14' },
  role: {
    type: String,
    enum: ['INSPECTOR', 'CONSUMER', 'DEMO_DEVICE'],
    default: 'INSPECTOR'
  },
  locale: { type: String, default: 'en-IN' },
  country: { type: String, default: 'IN' },
  status: {
    type: String,
    enum: ['ACTIVE', 'SUSPENDED', 'DECOMMISSIONED'],
    default: 'ACTIVE'
  },
  lastSeenAt: { type: Date, default: Date.now, index: true },
  totalScansCount: { type: Number, default: 0 }
}, { timestamps: true });

module.exports = mongoose.model('Device', deviceSchema);
