package com.example.data.repository

import com.example.data.db.InspectionDao
import com.example.data.model.InspectionRecord
import kotlinx.coroutines.flow.Flow

class InspectionRepository(private val inspectionDao: InspectionDao) {

    val allInspections: Flow<List<InspectionRecord>> = inspectionDao.getAllInspections()

    fun getInspectionById(id: Long): Flow<InspectionRecord?> = inspectionDao.getInspectionById(id)

    fun getInspectionsByStatus(status: String): Flow<List<InspectionRecord>> =
        inspectionDao.getInspectionsByStatus(status)

    fun searchInspections(query: String): Flow<List<InspectionRecord>> =
        inspectionDao.searchInspections(query)

    suspend fun insertInspection(record: InspectionRecord): Long =
        inspectionDao.insertInspection(record)

    suspend fun updateInspection(record: InspectionRecord) =
        inspectionDao.updateInspection(record)

    suspend fun deleteInspection(id: Long) =
        inspectionDao.deleteInspection(id)

    suspend fun seedSampleRecordsIfNeeded() {
        if (inspectionDao.getCount() == 0) {
            val samples = listOf(
                InspectionRecord(
                    productName = "KrishiVeda Organic NPK Bio-Fertilizer (50kg)",
                    brandName = "KrishiVeda Bio-Agronomics",
                    category = "Agri Seeds & Fertilizers",
                    barcode = "8908001234567",
                    netQuantity = "50 kg",
                    declaredMrp = "₹ 1,350.00 (incl. of all taxes)",
                    declaredUsp = "₹ 27.00 / kg",
                    countryOfOrigin = "India",
                    manufacturerAddress = "KrishiVeda Bio-Plant, Agro-Zone, Karnal, Haryana - 132001",
                    consumerCareContact = "kisan@krishiveda.in / 1800-180-1551",
                    mfgPackingDate = "02/2026",
                    pdpFontCompliance = true,
                    complianceScore = 100,
                    overallStatus = "COMPLIANT",
                    violationsCount = 0,
                    ruleViolationsSummary = "Fully compliant with Rule 6, Rule 8, and Schedule II font height (8.2mm > 6.0mm requirement). Fertilizer FCO composition specified.",
                    inspectorName = "A. K. Sharma, Senior LMO",
                    inspectorBadge = "LMO-HR-2026-0842",
                    inspectionLocation = "Kisan Seva Kendra, Karnal Mandi",
                    officerNotes = "Verified certified bio-fertilizer lot. All statutory metrology marks verified.",
                    timestamp = System.currentTimeMillis() - 3600000 * 2,
                    expiryDate = "Best before 24 months from mfg (Feb 2028)",
                    ingredientsList = "Azotobacter, Phosphate Solubilizing Bacteria (PSB), Potash Mobilizing Bacteria (KMB), Carrier-based Peat Material (FCO Standard)",
                    nutritionalInfo = "Viable count 1x10^7 CFU/g, Organic Carbon 16%, Moisture 35% max",
                    allergens = "Non-toxic, bio-organic agricultural input. Keep away from direct sunlight.",
                    batchLotNumber = "KV-NPK-2026-B12",
                    licenseNumbers = "FCO Lic. No. AGRI/HR/FERT/2024/0088",
                    rawFullOcrText = "KRISHIVEDA ORGANIC BIO-FERTILIZER. Net Qty: 50kg. MRP: Rs. 1350.00 (incl. of all taxes). USP: Rs. 27/kg. Mfg: 02/2026. Mfd by KrishiVeda Karnal. FCO Lic No: AGRI/HR/FERT/2024/0088. Batch: KV-NPK-2026-B12."
                ),
                InspectionRecord(
                    productName = "Desi Gold Pure Mustard Oil (1L)",
                    brandName = "Desi Gold Agrotech",
                    category = "Edible Oils & Ghee",
                    barcode = "8901234567890",
                    netQuantity = "1 L (910 g)",
                    declaredMrp = "₹ 185.00 (incl. of all taxes)",
                    declaredUsp = "₹ 0.185 / ml",
                    countryOfOrigin = "India",
                    manufacturerAddress = "Plot 42, Food Industrial Area, Kota, Rajasthan - 324005",
                    consumerCareContact = "care@desigold.in / 1800-180-2244",
                    mfgPackingDate = "02/2026",
                    pdpFontCompliance = true,
                    complianceScore = 100,
                    overallStatus = "COMPLIANT",
                    violationsCount = 0,
                    ruleViolationsSummary = "All Rule 6 statutory declarations satisfied. Font height complies with Schedule II.",
                    inspectorName = "A. K. Sharma, Senior LMO",
                    inspectorBadge = "LMO-DL-2026-0842",
                    inspectionLocation = "APMC Mandi Yard, Azadpur, Delhi",
                    officerNotes = "Verified standard pack size under Second Schedule. All clear.",
                    timestamp = System.currentTimeMillis() - 3600000 * 5,
                    expiryDate = "Best before 9 months from packaging (Nov 2026)",
                    ingredientsList = "100% Pure Cold-Pressed Mustard Oil, Vitamin A & Vitamin D",
                    nutritionalInfo = "Per 100g: Energy 900 kcal, Fat 100g (Saturated 7g, MUFA 68g, PUFA 25g), Trans Fat 0g",
                    allergens = "Contains Mustard seeds. Natural pungent aroma.",
                    batchLotNumber = "DGM-2026-B44",
                    licenseNumbers = "FSSAI Lic. No. 10018013000842",
                    rawFullOcrText = "DESI GOLD PURE MUSTARD OIL 1L. MRP: Rs. 185.00 (incl. of all taxes). USP: Rs. 0.185/ml. Pkd: 02/2026. Made in India. Mfd by Desi Gold Agrotech Kota. Care: 1800-180-2244. FSSAI: 10018013000842."
                ),
                InspectionRecord(
                    productName = "Hybrid Bt Cotton Seeds (450g Pack)",
                    brandName = "AgriTech GeneCrop Ltd",
                    category = "Agri Seeds & Fertilizers",
                    barcode = "8904567890123",
                    netQuantity = "450 g",
                    declaredMrp = "₹ 864.00",
                    declaredUsp = "Missing",
                    countryOfOrigin = "India",
                    manufacturerAddress = "AgriTech Seed Farm, Guntur, Andhra Pradesh",
                    consumerCareContact = "seeds@agritech.co.in",
                    mfgPackingDate = "01/2026",
                    pdpFontCompliance = false,
                    complianceScore = 60,
                    overallStatus = "NON_COMPLIANT",
                    violationsCount = 3,
                    ruleViolationsSummary = "1. Missing Unit Sale Price (USP) under Rule 6(1)(g)\n2. MRP missing mandatory '(incl. of all taxes)' suffix under Rule 6(1)(e)\n3. Consumer care telephone helpline missing (only email provided)\n4. Font height for net weight is 1.4mm (< required 2.0mm)",
                    inspectorName = "R. K. Verma, District Metrologist",
                    inspectorBadge = "LMO-AP-2026-0312",
                    inspectionLocation = "APMC Cotton Market, Guntur",
                    officerNotes = "Statutory notice issued to packager under Section 36(1).",
                    timestamp = System.currentTimeMillis() - 3600000 * 18,
                    noticeGenerated = true,
                    expiryDate = "Valid for sowing up to 9 months from test date (Oct 2026)",
                    ingredientsList = "Certified Hybrid Cotton Seeds (Cry1Ac & Cry2Ab traits), Thiram/Imidacloprid treated",
                    nutritionalInfo = "Germination Min 75%, Genetic Purity Min 95%, Physical Purity Min 98%",
                    allergens = "Poison treated seed. Strictly not for food, feed, or oil purposes.",
                    batchLotNumber = "ATC-BT2-882",
                    licenseNumbers = "Seeds Act Reg: AP/GNT/SEED/2023/1029",
                    rawFullOcrText = "AGRITECH HYBRID BT COTTON SEEDS. Net 450g. MRP Rs. 864.00. Packed 01/2026. Mfd by AgriTech Guntur AP. Contact: seeds@agritech.co.in. Treated with poison. Batch: ATC-BT2-882."
                ),
                InspectionRecord(
                    productName = "EcoGreen Bio-Degradable Agro Mulching Film (400m)",
                    brandName = "EcoGreen Plastix",
                    category = "Environmental & Farm Supplies",
                    barcode = "8906009876543",
                    netQuantity = "400 m (12.5 kg)",
                    declaredMrp = "₹ 2,400.00 (incl. of all taxes)",
                    declaredUsp = "₹ 6.00 / m",
                    countryOfOrigin = "India",
                    manufacturerAddress = "EcoGreen Industrial Estate, Sanand, Gujarat - 382110",
                    consumerCareContact = "support@ecogreenplastic.com / 079-26804411",
                    mfgPackingDate = "02/2026",
                    pdpFontCompliance = true,
                    complianceScore = 95,
                    overallStatus = "COMPLIANT",
                    violationsCount = 0,
                    ruleViolationsSummary = "Compliant with Legal Metrology and Plastic Waste Management Rules 2021 declarations.",
                    inspectorName = "S. N. Patel, Inspector",
                    inspectorBadge = "LMO-GJ-2026-0955",
                    inspectionLocation = "Agro Distribution Depot, Ahmedabad",
                    officerNotes = "Green certified packaging. Thickness and metric length verified accurately.",
                    timestamp = System.currentTimeMillis() - 3600000 * 28,
                    expiryDate = "Degradation lifespan 180 days after field application",
                    ingredientsList = "Corn Starch Polymer, PBAT (Polybutyrate Adipate Terephthalate), UV Stabilizer Masterbatch",
                    nutritionalInfo = "Film Thickness 25 Microns (Complies with PWM Rules 2021 & ISO 17088)",
                    allergens = "Non-food agricultural item.",
                    batchLotNumber = "EGP-SAN-400M-19",
                    licenseNumbers = "CPCB Reg No: B-29016/(SC)/CPCB/PWM/2022",
                    rawFullOcrText = "ECOGREEN AGRO MULCH FILM. Net: 400m (12.5kg). MRP: Rs. 2400.00 (incl. of all taxes). USP: Rs. 6/m. Mfd: 02/2026. Sanand Gujarat. CPCB Reg: PWM/2022. Batch: EGP-SAN-400M-19."
                ),
                InspectionRecord(
                    productName = "Crunchy Masala Chips (95g)",
                    brandName = "TasteBite Snacks Ltd",
                    category = "Packaged Food & Beverages",
                    barcode = "8909876543210",
                    netQuantity = "95 g",
                    declaredMrp = "₹ 30.00",
                    declaredUsp = "Missing",
                    countryOfOrigin = "India",
                    manufacturerAddress = "TasteBite Foods, Phase II, Noida (UP)",
                    consumerCareContact = "consumer@tastebite.com (No phone)",
                    mfgPackingDate = "01/2026",
                    pdpFontCompliance = false,
                    complianceScore = 55,
                    overallStatus = "NON_COMPLIANT",
                    violationsCount = 3,
                    ruleViolationsSummary = "1. Missing Unit Sale Price (USP) under Rule 6(1)(g)\n2. MRP missing mandatory '(incl. of all taxes)' suffix under Rule 6(1)(e)\n3. Consumer care phone number missing under Rule 6(1)(f)\n4. PDP font height 1.2mm (< required 2.0mm)",
                    inspectorName = "A. K. Sharma, Senior LMO",
                    inspectorBadge = "LMO-DL-2026-0842",
                    inspectionLocation = "Metro Cash & Carry, Mayapuri",
                    officerNotes = "Compound notice issued under Section 36 of Legal Metrology Act, 2009.",
                    timestamp = System.currentTimeMillis() - 3600000 * 36,
                    noticeGenerated = true,
                    expiryDate = "Best before 6 months from packaging (Jul 2026)",
                    ingredientsList = "Potatoes (62%), Edible Palmolein Oil (32%), Spices & Condiments (Chilli, Onion, Mango powder), Iodized Salt, INS 627, INS 631",
                    nutritionalInfo = "Per 100g: Energy 542 kcal, Carbs 52.4g, Protein 6.8g, Fat 34.2g (Saturated 15.8g, Trans Fat 0.1g), Sodium 780mg",
                    allergens = "May contain traces of Gluten, Milk Solids, and Soy.",
                    batchLotNumber = "TB-CHI-081",
                    licenseNumbers = "FSSAI Lic. No. 10020051000318",
                    rawFullOcrText = "CRUNCHY MASALA CHIPS. Net Weight 95g. MRP Rs. 30.00. Mfd 01/2026. Packed by TasteBite Foods Noida UP. For feedback email consumer@tastebite.com. Ingredients: Fresh Potatoes (62%), Edible Palmolein Oil, Spices. Batch: TB-CHI-081."
                ),
                InspectionRecord(
                    productName = "Himalayan Herbal Cold-Pressed Neem Oil (500ml)",
                    brandName = "Prakriti Bio-Extracts",
                    category = "Agri Seeds & Fertilizers",
                    barcode = "8907654321098",
                    netQuantity = "500 ml",
                    declaredMrp = "₹ 240.00 (incl. of all taxes)",
                    declaredUsp = "₹ 0.48 / ml",
                    countryOfOrigin = "India",
                    manufacturerAddress = "Village Kotla, Solan, Himachal Pradesh - 173212",
                    consumerCareContact = "care@prakritibio.in / 01792-234567",
                    mfgPackingDate = "02/2026",
                    pdpFontCompliance = true,
                    complianceScore = 100,
                    overallStatus = "COMPLIANT",
                    violationsCount = 0,
                    ruleViolationsSummary = "Bio-pesticide labeling conforms to CIBRC & Legal Metrology Rule 6.",
                    inspectorName = "V. K. Thakur, LMO",
                    inspectorBadge = "LMO-HP-2026-0120",
                    inspectionLocation = "Solan Farmer Market",
                    officerNotes = "Natural organic pesticide pack. Full compliance.",
                    timestamp = System.currentTimeMillis() - 3600000 * 44,
                    expiryDate = "Best before 24 months from manufacture (Feb 2028)",
                    ingredientsList = "100% Pure Cold Pressed Azadirachta Indica (Neem) Kernel Oil, Azadirachtin content 3000 PPM",
                    nutritionalInfo = "Natural Fatty Acids (Oleic 52%, Stearic 18%, Palmitic 15%, Linoleic 10%)",
                    allergens = "Non-edible oil. Keep out of reach of children. Botanical biopesticide.",
                    batchLotNumber = "PKT-NM-2026-44",
                    licenseNumbers = "CIBRC Reg: CIR-64210/2021-Neem(EC)-412",
                    rawFullOcrText = "PRAKRITI BIO HIMALAYAN NEEM OIL 500ml. MRP: Rs. 240.00 (incl. of all taxes). USP: Rs. 0.48/ml. Pkd: 02/2026. Made in India. Mfd by Prakriti Bio-Extracts Solan HP. CIBRC Reg: CIR-64210. Batch: PKT-NM-2026-44."
                ),
                InspectionRecord(
                    productName = "Swiss Artisan Hazelnut Cocoa Spread (350g)",
                    brandName = "Alps Treats AG",
                    category = "Imported Confectionery",
                    barcode = "7612345678901",
                    netQuantity = "350 g",
                    declaredMrp = "₹ 450",
                    declaredUsp = "Not printed",
                    countryOfOrigin = "Missing",
                    manufacturerAddress = "Alps Treats AG, Zurich, Switzerland (Importer missing)",
                    consumerCareContact = "contact@alps.ch",
                    mfgPackingDate = "11/2025",
                    pdpFontCompliance = false,
                    complianceScore = 30,
                    overallStatus = "SEIZURE_RECOMMENDED",
                    violationsCount = 5,
                    ruleViolationsSummary = "1. Country of Origin not declared (Rule 6(1)(h))\n2. Name & Address of Indian Importer not declared (Rule 6(1)(a))\n3. Missing Unit Sale Price (Rule 6(1)(g))\n4. Non-standard MRP format without tax inclusion phrase (Rule 6(1)(e))\n5. No Indian consumer helpline number.",
                    inspectorName = "R. K. Meena, Field Inspector",
                    inspectorBadge = "LMO-DL-2026-1109",
                    inspectionLocation = "Supermarket Mall, Saket",
                    officerNotes = "Seizure memo prepared under Rule 32. 48 packs impounded.",
                    timestamp = System.currentTimeMillis() - 3600000 * 56,
                    noticeGenerated = true,
                    expiryDate = "EXP 11/2026 (Best before 12 months)",
                    ingredientsList = "Sugar, Vegetable Fat (Palm, Shea), Hazelnuts (13%), Skimmed Milk Powder (8.7%), Cocoa Powder (7.4%), Emulsifier: Soy Lecithin, Vanillin",
                    nutritionalInfo = "Per 100g: Energy 539 kcal, Carbohydrates 57.5g (Sugars 56.3g), Protein 6.3g, Fat 30.9g (Saturated 10.6g), Sodium 42mg",
                    allergens = "Contains Tree Nuts (Hazelnuts), Milk, Soy.",
                    batchLotNumber = "LOT: CH-ZR-904",
                    licenseNumbers = "UNREGISTERED IMPORTER (No FSSAI Import License printed)",
                    rawFullOcrText = "ALPS ARTISAN HAZELNUT SPREAD 350g. Price: 450 Rs. Mfd by Alps Treats AG Zurich Switzerland. Contact: contact@alps.ch. Lot: CH-ZR-904. Ingredients: Sugar, Hazelnuts 13%, Cocoa 7.4%."
                )
            )

            for (sample in samples) {
                inspectionDao.insertInspection(sample)
            }
        }
    }
}
