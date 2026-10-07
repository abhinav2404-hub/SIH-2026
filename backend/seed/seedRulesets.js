require('dotenv').config();
const mongoose = require('mongoose');
const User = require('../src/models/User');
const Ruleset = require('../src/models/Ruleset');
const Scan = require('../src/models/Scan');
const Device = require('../src/models/Device');
const Complaint = require('../src/models/Complaint');

async function seed() {
  try {
    const mongoUri = process.env.MONGODB_URI || 'mongodb://127.0.0.1:27017/mudra_check';
    await mongoose.connect(mongoUri);
    console.log(`[Seed Engine] Connected to: ${mongoUri}`);

    // Clear existing demo records
    await User.deleteMany({});
    await Ruleset.deleteMany({});
    await Scan.deleteMany({});
    await Device.deleteMany({});
    await Complaint.deleteMany({});

    // 1. Create Default Users (Admin & Field Inspector)
    const admin = await User.create({
      name: 'Director General (Legal Metrology)',
      email: 'admin@mudracheck.gov.in',
      password: 'AdminPassword@2026',
      role: 'ADMIN',
      badgeNumber: 'NAT-HQ-001'
    });

    const inspector = await User.create({
      name: 'Inspector Rajesh Sharma',
      email: 'inspector.delhi@mudracheck.gov.in',
      password: 'InspectorPass@2026',
      role: 'INSPECTOR',
      badgeNumber: 'LMO-DL-2026-0842'
    });

    console.log('✅ Users seeded successfully.');

    // 2. Create Active Jurisdictional Rulesets
    // A. India Legal Metrology (Packaged Commodities) Rules, 2011
    const indiaRuleset = await Ruleset.create({
      rulesetId: 'IN-PCR2011-v2011',
      countryCode: 'IN',
      countryName: 'India',
      name: 'Legal Metrology (Packaged Commodities) Rules, 2011',
      version: '2026.1',
      currency: 'INR',
      units: 'Metric (SI)',
      consumerHelpline: '1915 (National Consumer Helpline)',
      active: true,
      rules: [
        {
          ruleId: 'RULE-6-MRP',
          ruleRequirement: 'Mandatory Maximum Retail Price (MRP) declaration inclusive of all taxes',
          fieldExpected: 'MRP (₹ / Currency)',
          violationSeverity: 'CRITICAL',
          remediationHintTemplate: 'Affix prominent MRP declaration in format "MRP Rs. XX.XX (incl. of all taxes)".',
          references: { act: 'Legal Metrology Act, 2009', section: 'Section 36', rule: 'Rule 6(1)(e)' }
        },
        {
          ruleId: 'RULE-6-USP',
          ruleRequirement: 'Mandatory Unit Sale Price (USP) for commodities above 100g / 100ml',
          fieldExpected: 'USP (e.g. ₹0.20 per g / ₹200.00 per kg)',
          violationSeverity: 'MAJOR',
          remediationHintTemplate: 'Declare Unit Sale Price rounded off to nearest two decimal places.',
          references: { act: 'Legal Metrology Act, 2009', section: 'Section 36', rule: 'Rule 6(11)' }
        },
        {
          ruleId: 'RULE-9-FONT',
          ruleRequirement: 'Statutory PDP Minimum Numeral Font Height Standard',
          fieldExpected: 'Minimum 2.0 mm height',
          thresholds: { minFontHeightMm: 2.0 },
          violationSeverity: 'MAJOR',
          remediationHintTemplate: 'Increase numeral and alphabet height on PDP to comply with Rule 9 Table-I standards.',
          references: { act: 'Legal Metrology Act, 2009', section: 'Section 36', rule: 'Rule 9 Table-I' }
        },
        {
          ruleId: 'RULE-6-MFG',
          ruleRequirement: 'Complete name and registered postal address of manufacturer / packer',
          fieldExpected: 'Manufacturer Name & Address',
          violationSeverity: 'CRITICAL',
          remediationHintTemplate: 'State registered address, factory address, and consumer helpline contact.',
          references: { act: 'Legal Metrology Act, 2009', section: 'Section 36', rule: 'Rule 6(1)(a)' }
        }
      ]
    });

    // B. Multi-Country Global Demo Ruleset
    const globalRuleset = await Ruleset.create({
      rulesetId: 'DEMO-GLOBAL-v1',
      countryCode: 'GLOBAL',
      countryName: 'Codex / International Packaging Standard',
      name: 'Universal Packaging Transparency & Net Quantity Standard',
      version: '2026.Global',
      currency: 'USD',
      units: 'Metric / Imperial SI',
      consumerHelpline: '+1-800-CONSUMER-CARE',
      active: true,
      rules: [
        {
          ruleId: 'CODEX-NET-QTY',
          ruleRequirement: 'Explicit net quantity declaration in standard metric units',
          fieldExpected: 'Net Quantity',
          violationSeverity: 'CRITICAL'
        }
      ]
    });

    console.log('✅ Regulatory rulesets seeded successfully.');

    // 3. Register Demo Hardware Device
    await Device.create({
      deviceId: 'DEV-ANDR-88421',
      userId: inspector._id,
      deviceName: 'Samsung Galaxy Tab Active 4 (Inspector Unit)',
      model: 'SM-T636B',
      platform: 'Android',
      appVersion: '5.0.0',
      role: 'INSPECTOR',
      status: 'ACTIVE'
    });

    // 4. Create Sample Scans
    await Scan.create({
      clientScanId: 'SCAN-SAMPLE-001',
      deviceId: 'DEV-ANDR-88421',
      userId: inspector._id,
      rulesetId: 'IN-PCR2011-v2011',
      productName: 'Kurkure Masala Munch (82g)',
      brandName: 'Kurkure',
      manufacturerName: 'PepsiCo India Holdings Pvt. Ltd.',
      barcode: '8901491101844',
      mrp: 20.0,
      netQuantity: '82 g',
      overallVerdict: 'PASS',
      complianceScore: 100,
      syncStatus: 'SYNCED'
    });

    await Scan.create({
      clientScanId: 'SCAN-SAMPLE-002',
      deviceId: 'DEV-ANDR-88421',
      userId: inspector._id,
      rulesetId: 'IN-PCR2011-v2011',
      productName: 'Groundnut Oil Refined (1L)',
      brandName: 'Generic Farms',
      manufacturerName: 'Farm Produce Packagers Ltd.',
      mrp: null,
      netQuantity: '1 L',
      overallVerdict: 'FAIL',
      complianceScore: 65,
      violations: [
        {
          ruleId: 'RULE-6-MRP',
          ruleRequirement: 'Mandatory Maximum Retail Price (MRP) declaration inclusive of all taxes',
          fieldExpected: 'MRP (₹)',
          evidenceFound: 'Missing / Not extracted on PDP',
          verdict: 'FAIL',
          violationSeverity: 'CRITICAL',
          remediationHint: 'Affix prominent MRP declaration.'
        }
      ],
      syncStatus: 'SYNCED'
    });

    // 5. Seed Consumer Grievance
    await Complaint.create({
      complaintId: 'CMP-2026-9901',
      clientScanId: 'SCAN-SAMPLE-002',
      userId: inspector._id,
      productName: 'Groundnut Oil Refined (1L)',
      brandName: 'Generic Farms',
      retailerName: 'Metro Supermarket, Connaught Place',
      category: 'MISSING_MANDATORY_DECLARATION',
      description: 'The edible oil container does not display Maximum Retail Price (MRP) or date of packaging.',
      status: 'OPEN',
      priority: 'HIGH'
    });

    console.log('✅ Demo scans and complaints seeded successfully.');
    console.log(`=======================================================`);
    console.log(`🌱 DATABASE SEEDING COMPLETED WITH ZERO ERRORS`);
    console.log(`Admin User: admin@mudracheck.gov.in / AdminPassword@2026`);
    console.log(`Inspector:  inspector.delhi@mudracheck.gov.in / InspectorPass@2026`);
    console.log(`=======================================================`);
    process.exit(0);
  } catch (err) {
    console.error('❌ Seeding Error:', err);
    process.exit(1);
  }
}

seed();
