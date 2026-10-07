const mongoose = require('mongoose');

const violationSchema = new mongoose.Schema({
  ruleId: { type: String, required: true },
  ruleRequirement: { type: String, required: true },
  fieldExpected: { type: String, default: '' },
  evidenceFound: { type: String, default: '' },
  verdict: {
    type: String,
    enum: ['PASS', 'FAIL', 'NOT_VERIFIABLE'],
    default: 'FAIL'
  },
  confidence: { type: Number, default: 0.95 },
  violationSeverity: {
    type: String,
    enum: ['CRITICAL', 'MAJOR', 'MINOR', 'WARNING'],
    default: 'MAJOR'
  },
  remediationHint: { type: String, default: '' },
  boundingBox: {
    x: Number,
    y: Number,
    width: Number,
    height: Number
  },
  source: { type: String, default: 'OCR_ENGINE' },
  aiExplanation: { type: String, default: '' }
}, { _id: true, timestamps: true });

module.exports = violationSchema;
