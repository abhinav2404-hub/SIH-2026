const mongoose = require('mongoose');

const ruleSchema = new mongoose.Schema({
  ruleId: { type: String, required: true },
  ruleRequirement: { type: String, required: true },
  fieldExpected: { type: String, required: true },
  thresholds: {
    minFontHeightMm: Number,
    allowedUnits: [String],
    standardSizes: [Number]
  },
  violationSeverity: {
    type: String,
    enum: ['CRITICAL', 'MAJOR', 'MINOR', 'WARNING'],
    default: 'MAJOR'
  },
  remediationHintTemplate: { type: String, default: '' },
  references: {
    act: { type: String, default: 'Legal Metrology Act, 2009' },
    section: { type: String, default: 'Section 36' },
    rule: { type: String, default: 'Rule 6' }
  },
  active: { type: Boolean, default: true }
}, { _id: true });

const rulesetSchema = new mongoose.Schema({
  rulesetId: { type: String, required: true, unique: true, index: true },
  countryCode: { type: String, required: true, uppercase: true, index: true },
  countryName: { type: String, required: true },
  name: { type: String, required: true },
  version: { type: String, default: '2026.1' },
  effectiveFrom: { type: Date, default: Date.now },
  effectiveTo: { type: Date },
  currency: { type: String, default: 'INR' },
  units: { type: String, default: 'Metric (SI)' },
  consumerHelpline: { type: String, default: '1915' },
  rules: [ruleSchema],
  active: { type: Boolean, default: true, index: true }
}, { timestamps: true });

rulesetSchema.index({ countryCode: 1, active: 1 });

module.exports = mongoose.model('Ruleset', rulesetSchema);
