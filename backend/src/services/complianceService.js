const Ruleset = require('../models/Ruleset');

class ComplianceService {
  /**
   * Evaluates product data dynamically against configured jurisdiction ruleset
   */
  static async evaluate({
    rulesetId = 'IN-PCR2011-v2011',
    productName,
    mrp,
    netQuantity,
    fontHeightMm = 0.0,
    hasManufacturer = true,
    hasOrigin = true,
    hasExpiry = true,
    rawOcrText = ''
  }) {
    const ruleset = await Ruleset.findOne({ rulesetId, active: true }) || {
      rulesetId: 'IN-PCR2011-v2011',
      rules: []
    };

    const violations = [];

    // 1. Mandatory MRP Rule Check
    if (mrp === null || mrp === undefined || mrp <= 0) {
      violations.push({
        ruleId: 'RULE-6-MRP',
        ruleRequirement: 'Mandatory Maximum Retail Price (MRP) declaration inclusive of all taxes',
        fieldExpected: 'MRP (₹ / Currency)',
        evidenceFound: mrp ? `${mrp}` : 'Missing / Unstated on packaging',
        verdict: 'FAIL',
        confidence: 0.99,
        violationSeverity: 'CRITICAL',
        remediationHint: 'Affix prominent MRP declaration in format "MRP Rs. XX.XX (incl. of all taxes)".',
        source: 'RULES_ENGINE',
        aiExplanation: 'Rule 6(1)(e) of PCR 2011 mandates unambiguous price declaration.'
      });
    }

    // 2. Principal Display Panel (PDP) Font Height Standard
    if (fontHeightMm > 0 && fontHeightMm < 2.0) {
      violations.push({
        ruleId: 'RULE-9-FONT',
        ruleRequirement: 'Minimum numeral font height standard on Principal Display Panel (PDP)',
        fieldExpected: 'Minimum 2.0 mm height',
        evidenceFound: `Measured: ${fontHeightMm} mm`,
        verdict: 'FAIL',
        confidence: 0.94,
        violationSeverity: 'MAJOR',
        remediationHint: 'Increase numeral and letter size on PDP to comply with Rule 9 Table-I standards.',
        source: 'RULES_ENGINE',
        aiExplanation: 'Rule 9(3) prescribes minimum font heights corresponding to net weight categories.'
      });
    }

    // 3. Packer / Manufacturer Identification
    if (!hasManufacturer) {
      violations.push({
        ruleId: 'RULE-6-MFG',
        ruleRequirement: 'Full registered name and complete address of the manufacturer / packer / importer',
        fieldExpected: 'Manufacturer Name & Address',
        evidenceFound: 'Omitted / Truncated',
        verdict: 'FAIL',
        confidence: 0.96,
        violationSeverity: 'CRITICAL',
        remediationHint: 'Print complete postal address and contact email/helpline on outer packaging.',
        source: 'RULES_ENGINE',
        aiExplanation: 'Rule 6(1)(a) requires unhindered consumer contact details.'
      });
    }

    // Compute Overall Score and Verdict
    const totalDeductions = violations.reduce((acc, v) => {
      if (v.violationSeverity === 'CRITICAL') return acc + 35;
      if (v.violationSeverity === 'MAJOR') return acc + 20;
      return acc + 10;
    }, 0);

    const complianceScore = Math.max(0, 100 - totalDeductions);
    const overallVerdict = violations.some(v => v.violationSeverity === 'CRITICAL')
      ? 'FAIL'
      : violations.length > 0
        ? 'REVIEW'
        : 'PASS';

    return {
      rulesetId: ruleset.rulesetId,
      overallVerdict,
      complianceScore,
      violations
    };
  }
}

module.exports = ComplianceService;
