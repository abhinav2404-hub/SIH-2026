package com.example.data.repository

import com.example.data.db.MetrologyRuleDao
import com.example.data.model.MetrologyRuleEntity
import kotlinx.coroutines.flow.Flow

class MetrologyRuleRepository(private val metrologyRuleDao: MetrologyRuleDao) {

    val allRules: Flow<List<MetrologyRuleEntity>> = metrologyRuleDao.getAllRules()

    fun getRulesByCategory(category: String): Flow<List<MetrologyRuleEntity>> =
        metrologyRuleDao.getRulesByCategory(category)

    fun getAgriRules(): Flow<List<MetrologyRuleEntity>> =
        metrologyRuleDao.getAgriSpecificRules()

    fun searchRules(query: String): Flow<List<MetrologyRuleEntity>> =
        metrologyRuleDao.searchRules(query)

    suspend fun getRuleById(ruleId: String): MetrologyRuleEntity? =
        metrologyRuleDao.getRuleById(ruleId)

    suspend fun seedDefaultRulesIfNeeded() {
        if (metrologyRuleDao.getRulesCount() == 0) {
            val rules = listOf(
                MetrologyRuleEntity(
                    ruleId = "RULE_6_1_A",
                    ruleNumber = "Rule 6(1)(a)",
                    title = "Manufacturer / Packer / Importer Name & Address",
                    description = "Every package shall bear the name and complete address of the manufacturer, or where the manufacturer is not the packer, the name and address of the manufacturer and packer, and for imported commodities, the name and address of the importer.",
                    category = "MANDATORY_DECLARATIONS",
                    statutorySection = "Section 18 & Section 36(1), Legal Metrology Act 2009",
                    mandatoryCheck = "Complete postal address with State and PIN code or registered office address.",
                    standardThreshold = "Full address required. Web link or QR code alone is not an exemption.",
                    penaltyClause = "Fine up to ₹25,000 for first offence; up to ₹50,000 for second; and up to ₹1,00,000 or imprisonment for subsequent offences.",
                    isAgriSpecific = false,
                    guidanceNotes = "For agricultural produce and bio-fertilizers, packer FPO/cooperative registration details must also be visible."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_6_1_B",
                    ruleNumber = "Rule 6(1)(b)",
                    title = "Generic or Common Name of Commodity",
                    description = "The common or generic name of the commodity contained in the package and where the package contains more than one product, the name and number or quantity of each product.",
                    category = "MANDATORY_DECLARATIONS",
                    statutorySection = "Section 18, Legal Metrology Act 2009",
                    mandatoryCheck = "Prominent naming of the core agricultural or consumer item (e.g. 'Mustard Oil', 'Certified Paddy Seeds').",
                    standardThreshold = "Exact generic classification under FSSAI / Seed Act / Fertilizer Control Order.",
                    penaltyClause = "Misleading declaration attracts penalties under Section 36(1) and Section 53.",
                    isAgriSpecific = true,
                    guidanceNotes = "For seeds & fertilizers, crop variety and chemical composition ratios must be clearly specified."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_6_1_C",
                    ruleNumber = "Rule 6(1)(c)",
                    title = "Net Quantity in Standard Metric Units",
                    description = "The net quantity in terms of standard unit of weight or measure or number. Units must be g, kg, ml, l, m, or N. Non-standard symbols like 'gms', 'kilo', 'ltr' are non-compliant.",
                    category = "MANDATORY_DECLARATIONS",
                    statutorySection = "Section 18 & Section 36(2), Legal Metrology Act 2009",
                    mandatoryCheck = "Expressed in standard SI metric units (kg, g, L, ml, cm, m, N).",
                    standardThreshold = "Within maximum permissible error (MPE) tolerances specified in First Schedule.",
                    penaltyClause = "Deficiency in quantity attracts fine of ₹10,000 to ₹50,000 and seizure under Section 36(2).",
                    isAgriSpecific = false,
                    guidanceNotes = "Bulk agricultural grain bags (50kg) must maintain nett weight at standard moisture content."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_6_1_D",
                    ruleNumber = "Rule 6(1)(d)",
                    title = "Month & Year of Manufacture / Packaging",
                    description = "The month and year in which the commodity is manufactured or pre-packed or imported shall be mentioned in letters and numerals. For agricultural products & seeds, germination validity period is also mandatory.",
                    category = "MANDATORY_DECLARATIONS",
                    statutorySection = "Section 18, Legal Metrology Act 2009",
                    mandatoryCheck = "Format MM/YYYY or Month Year (e.g., '02/2026' or 'Feb 2026').",
                    standardThreshold = "Clearly legible and indelibly marked on the package.",
                    penaltyClause = "Penalty up to ₹25,000 under Section 36(1).",
                    isAgriSpecific = true,
                    guidanceNotes = "Agricultural seeds must state Month of Test and Expiry/Germination Validity period."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_6_1_E",
                    ruleNumber = "Rule 6(1)(e)",
                    title = "Maximum Retail Price (MRP) inclusive of All Taxes",
                    description = "The retail sale price of the package shall be clearly indicated as 'Maximum Retail Price ₹ ... / Rs. ... inclusive of all taxes' or 'MRP Rs. ... incl. of all taxes'. Selling above MRP is strictly punishable.",
                    category = "UNIT_SALE_PRICE",
                    statutorySection = "Rule 18 & Section 36(1), Legal Metrology Act 2009",
                    mandatoryCheck = "Mandatory inclusion of '(incl. of all taxes)' phrase and standard rupee symbol ₹.",
                    standardThreshold = "Single definitive MRP without alteration, sticker tampering, or dual-pricing.",
                    penaltyClause = "First violation ₹2,000 to ₹25,000; overcharging above MRP attracts prosecution and license suspension.",
                    isAgriSpecific = false,
                    guidanceNotes = "Subsidized agricultural inputs (Urea, DAP) must reflect notified ceiling prices."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_6_1_G",
                    ruleNumber = "Rule 6(1)(g)",
                    title = "Unit Sale Price (USP) Mandate (Dec 2022 Amendment)",
                    description = "Unit sale price shall be declared on every pre-packaged commodity where net quantity is more than 1 kg / 1 L, expressed as '₹ per g/kg' or '₹ per ml/L' rounded off to two decimal places.",
                    category = "UNIT_SALE_PRICE",
                    statutorySection = "Rule 6(1)(g) & Rule 6(11), Legal Metrology Rules 2011",
                    mandatoryCheck = "USP must be declared alongside MRP in font size not less than required under Schedule II.",
                    standardThreshold = "Calculated as MRP divided by Net Quantity (e.g. ₹ 200 for 500g = ₹ 0.40 / g or ₹ 400.00 / kg).",
                    penaltyClause = "Notice and compound fine up to ₹25,000 for non-display of USP.",
                    isAgriSpecific = false,
                    guidanceNotes = "Crucial for agricultural farm inputs and consumer staples to allow fair price comparison."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_6_1_F",
                    ruleNumber = "Rule 6(1)(f)",
                    title = "Consumer Care & Grievance Redressal Contact",
                    description = "Name, address, telephone number, and email address of the person or office who can be contacted in case of consumer complaints.",
                    category = "MANDATORY_DECLARATIONS",
                    statutorySection = "Section 18, Legal Metrology Act 2009",
                    mandatoryCheck = "Must provide functional telephone number AND email address.",
                    standardThreshold = "Toll-free helpline or working telephone number active during business hours.",
                    penaltyClause = "Violation attracts compounding fine up to ₹25,000.",
                    isAgriSpecific = false,
                    guidanceNotes = "For Farmer Producer Organizations (FPOs), Kisan Call Centre or local nodal officer contact is recommended."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_6_1_H",
                    ruleNumber = "Rule 6(1)(h)",
                    title = "Country of Origin for Imported Goods",
                    description = "For imported products, the name of the country of origin or manufacturer country must be prominently mentioned on the Principal Display Panel.",
                    category = "MANDATORY_DECLARATIONS",
                    statutorySection = "Section 18, Legal Metrology Act 2009",
                    mandatoryCheck = "Clear declaration: 'Country of Origin: [Country Name]' or 'Made in [Country]'.",
                    standardThreshold = "Bold, unambiguous country nomenclature.",
                    penaltyClause = "Customs and LM compounding penalty; seizure under Rule 32.",
                    isAgriSpecific = false,
                    guidanceNotes = "Imported pesticides, seeds, and specialty fertilizers require strict origin traceability."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_8",
                    ruleNumber = "Rule 8",
                    title = "Principal Display Panel (PDP) Dimensions & Placement",
                    description = "Every declaration on package shall be on the Principal Display Panel (PDP). The area of PDP shall not be less than 40% of total surface area for rectangular packages, and 40% of the product of height and circumference for cylindrical containers.",
                    category = "PDP_SPECIFICATIONS",
                    statutorySection = "Rule 8, Legal Metrology (Packaged Commodities) Rules 2011",
                    mandatoryCheck = "Declarations grouped in a conspicuous, unobstructed zone on the front/principal face.",
                    standardThreshold = ">= 40% of package face area; clearly visible under normal lighting.",
                    penaltyClause = "Non-compliant panel layout invites rectification order or compound notice.",
                    isAgriSpecific = false,
                    guidanceNotes = "Agri chemical containers must keep statutory metrology separate from hazard pictograms."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_9_SCH_II",
                    ruleNumber = "Rule 9 (Schedule II)",
                    title = "Minimum Font Height & Letter Size Specifications",
                    description = "Mandatory declaration numerals and letters must conform to minimum height standards prescribed in Schedule II based on net quantity and area of the Principal Display Panel.",
                    category = "FONT_HEIGHT_SCHEDULE_II",
                    statutorySection = "Rule 9 & Second Schedule, Legal Metrology Rules 2011",
                    mandatoryCheck = "Up to 50g/ml: >= 1.0mm (blown/moulded: >= 2.0mm)\n50g to 200g: >= 1.5mm (>= 3.0mm)\n200g to 1kg: >= 2.0mm (>= 4.0mm)\nAbove 1kg: >= 4.0mm (>= 6.0mm)\nArea > 3600 cm²: >= 6.0mm (>= 10.0mm)",
                    standardThreshold = "Strict compliance with Schedule II minimum millimeter heights.",
                    penaltyClause = "Compounding fine under Section 36(1) for undersized font height.",
                    isAgriSpecific = false,
                    guidanceNotes = "Large fertilizer sacks (25kg/50kg) must have font height of at least 6.0mm for net weight and MRP."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_18_1",
                    ruleNumber = "Rule 18(1)",
                    title = "Prohibition of Sale Above Maximum Retail Price",
                    description = "No dealer or other person including wholesale dealer or retail dealer shall sell any commodity in packed form at a price exceeding the retail sale price thereof.",
                    category = "PENAL_PROVISIONS",
                    statutorySection = "Section 36(1), Legal Metrology Act 2009",
                    mandatoryCheck = "Actual billed price <= Declared MRP.",
                    standardThreshold = "Zero tolerance for dual-pricing or markup beyond declared MRP.",
                    penaltyClause = "First offence fine up to ₹25,000; second offence fine up to ₹50,000; subsequent offences fine up to ₹1,00,000 or imprisonment up to 1 year.",
                    isAgriSpecific = true,
                    guidanceNotes = "Crucial enforcement during agricultural sowing seasons against black-marketing of certified seeds and fertilizers."
                ),
                MetrologyRuleEntity(
                    ruleId = "RULE_36",
                    ruleNumber = "Rule 36 / Section 36",
                    title = "Offences, Compounding & Seizure Powers",
                    description = "Legal Metrology Inspectors are empowered under Section 15 & Section 36 to enter, inspect, search, seize non-compliant commodities, and issue statutory notices for compounding offences.",
                    category = "PENAL_PROVISIONS",
                    statutorySection = "Section 15, 36 & 48, Legal Metrology Act 2009",
                    mandatoryCheck = "Form VI Seizure Notice & compounding documentation.",
                    standardThreshold = "Compounding within 30 days or prosecution in Magistrate Court.",
                    penaltyClause = "Seizure of entire consignment and compounding fee as determined by Controller.",
                    isAgriSpecific = false,
                    guidanceNotes = "Standard digital inspection report generated by Mudra Check serves as admissible prima facie documentation."
                )
            )

            metrologyRuleDao.insertRules(rules)
        }
    }
}
