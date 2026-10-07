package com.example.data.scanner

import com.example.data.model.InspectionStatus
import com.example.data.model.SamplePackage

object SamplePackagesRepository {

    val samplePackages: List<SamplePackage> = listOf(
        SamplePackage(
            id = "SAMPLE_KURKURE_MAIN",
            title = "Kurkure",
            brand = "PepsiCo India Holdings Pvt. Ltd.",
            category = "Packaged Food & Namkeen (Proprietary Food 15.1)",
            netQuantity = "82 g [(68 g + 14 g EXTRA)]",
            declaredMrp = "₹ 20.00 (inclusive of all taxes)",
            declaredUsp = "₹ 0.244 / g (₹ 24.39 / 100g calculated)",
            countryOfOrigin = "India",
            manufacturer = "PepsiCo India Holdings Pvt. Ltd., Village Channo, Sangrur, Punjab - 148026 / DLF Qutab Enclave, Phase-I, Gurugram, Haryana - 122002",
            consumerCare = "Toll Free: 1800-22-4020, Email: consumer.feedback@pepsico.com, Address: Consumer Services Manager, PepsiCo India, PO Box 27, DLF Qutab Enclave, Gurugram, Haryana - 122002",
            mfgDate = "30/08/2026",
            expiryDate = "Best Before 4 Months from Manufacture (EXP: 28/12/2026)",
            fontHeightMm = 3.2,
            pdpAreaSqCm = 115.0,
            expectedComplianceScore = 100,
            expectedStatus = InspectionStatus.COMPLIANT,
            rawLabelText = """
                KURKURE - NAMKEEN (PROPRIETARY FOOD 15.1)
                Net Quantity: 82 g [(68 g + 14 g EXTRA)]
                MRP Rs. 20/- (INCL. OF ALL TAXES)
                UNIT SALE PRICE: Rs. 0.244/- PER g (Rs. 24.39 / 100g)
                MFD & USE BY: 30/08/26 & 28/12/26 | BATCH NO: 1.0/RP 300826 #
                Country of Origin: India (Marketed by PepsiCo India Holdings Pvt. Ltd.)
                Barcode: 8901491367219 (GS1 EAN-13 Barcode)
                FSSAI Mkt. Lic. No: 10014064000435
                Consumer Services Manager: P.O. BOX-27, DLF QUTAB ENCLAVE, PHASE-I, GURUGRAM - 122002, HARYANA | 1800 22 4020 | CONSUMER.FEEDBACK@PEPSICO.COM
                Ingredients: Cereal Products (67%) (Rice Meal (44%), Corn Meal (23%)), Edible Vegetable Oil (Palmolein @ or Rice Bran #), Seasoning (Spices and Condiments, Iodised Salt, Sugar, Flavour (Natural and Nature Identical Flavouring Substances), Black Salt, Tomato Powder, Acidity Regulators (330, 296, 508), Colour (160c), Maltodextrin), Gram Meal (0.5%).
                Nutritional Information (per 100g): Energy 554 kcal, Protein 6.0g, Carbohydrates 57.5g (Total Sugars 1.4g, Added Sugars 0.8g), Total Fat 33.4g (Saturated Fat 15.1g @ / 7.7g #, Trans Fat 0.1g), Sodium 694mg.
                Per Serving (20g): Energy 111 kcal, Protein 1.2g, Carbohydrates 11.5g (Total Sugars 0.3g, Added Sugars 0.2g), Total Fat 6.7g (Saturated Fat 3.0g, Trans Fat 0.02g), Sodium 139mg.
                Hidden / Undeclared Elements: Edible Vegetable Oil percentage (undisclosed on packaging), Seasoning blend ratio (undisclosed on packaging), Maltodextrin carrier starch quantity (undisclosed).
                Dietary Precautions: High Sodium (694mg/100g) - limit daily intake for hypertension; Saturated Fat (15.1g/100g) - portion control advised; Calorie dense (554 kcal/100g); Allergen: May contain Wheat, Soy, Peanut traces.
                QR Payload: 8901491367219 | FSSAI:10014064000435 | BATCH:1.0/RP 300826 # | MRP:20.00 | NET:82g
            """.trimIndent(),
            description = "Kurkure (PepsiCo India) 82g Pack (MRP ₹20) scanned via Barcode 8901491367219. Complete ingredient transparency, undisclosed percentages breakdown, INS additives (330, 296, 508, 160c), unit pricing (₹24.39/100g), and health precautions.",
            violationSummary = "Statutory Verdict: 100% PASS. Valid EAN-13 barcode (8901491367219), declared MRP ₹20.00, dynamic USP ₹24.39/100g, FSSAI Lic. 10014064000435, QUID cereal disclosures, and vegetarian green logo.",
            ingredients = "Cereal Products (67%) (Rice Meal (44%), Corn Meal (23%)), Edible Vegetable Oil (Palmolein @ or Rice Bran #) (percentage not disclosed on package), Seasoning (percentage not disclosed on package) (Spices & Condiments, Iodised Salt, Sugar, Flavour [Natural & Nature Identical Flavouring Substances], Black Salt, Tomato Powder, Acidity Regulators [INS 330, INS 296, INS 508], Colour [INS 160c], Maltodextrin), Gram Meal (0.5%)",
            nutritionalInfo = "Per 100g: Energy 554 kcal, Protein 6.0g, Carbohydrates 57.5g (Total Sugars 1.4g, Added Sugars 0.8g), Total Fat 33.4g (Saturated Fat 15.1g @ / 7.7g #, Trans Fat 0.1g), Sodium 694mg | Per Serving (20g): Energy 111 kcal, Protein 1.2g, Carbs 11.5g, Total Fat 6.7g, Sodium 139mg",
            allergens = "Contains Wheat and Soy ingredients. May contain traces of Milk and Peanuts.",
            batchNo = "1.0/RP 300826 #",
            licenseNo = "FSSAI Mkt. Lic. No. 10014064000435 • GS1 EAN-13: 8901491367219",
            qrCodeData = "GS1 EAN-13: 8901491367219 | FSSAI: 10014064000435 | Brand: Kurkure (PepsiCo) | Batch: 1.0/RP 300826 # | MRP: ₹20.00 | Net: 82g",
            chemicalSpecs = "Moisture: 1.5% (Limit <= 3.0%) • Peroxide Value: 2.6 meq/kg • Free Fatty Acids: 0.24% • Preservatives within FSSAI Schedule limits [PASS]",
            quidDetails = "Cereal Products: 67% (Rice Meal 44%, Corn Meal 23%) • Gram Meal: 0.5% (Explicitly Disclosed) • Edible Vegetable Oil: Percentage not disclosed on package • Seasoning Mix: Percentage not disclosed on package • Maltodextrin: Quantity not disclosed on package",
            foplWarning = "🟡 High Saturated Fat (15.1g/100g @ Palmolein) • 🟡 High Sodium Concern (694mg/100g) • 🟢 Added Sugar: Low (0.8g/100g) • 🟢 Trans Fat: Low (0.1g/100g)"
        ),
        SamplePackage(
            id = "SAMPLE_KURKURE_MASALA_MUNCH",
            title = "Kurkure Masala Munch Namkeen (19.7g / ₹5 Pack)",
            brand = "PepsiCo India Holdings Pvt. Ltd.",
            category = "Packaged Food & Namkeen (Proprietary Food 15.1)",
            netQuantity = "19.7 g [(12 g + 6 g EXTRA) + 1.7 g]",
            declaredMrp = "₹ 5.00 (inclusive of all taxes)",
            declaredUsp = "₹ 0.25 / g (₹ 25.38 / 100g calculated)",
            countryOfOrigin = "India",
            manufacturer = "PepsiCo India Holdings Pvt. Ltd., Village Channo, Patiala-Sangrur Road, P.O. Bhawanigarh, Distt. Sangrur, Punjab - 148026 / DLF Qutab Enclave, Gurugram, Haryana - 122002",
            consumerCare = "Toll Free: 1800-22-4020, Email: consumer.feedback@pepsico.com, Address: Consumer Services Manager, PepsiCo India, PO Box 27, DLF Qutab Enclave, Gurugram, Haryana - 122002",
            mfgDate = "30/08/2026",
            expiryDate = "Best Before 4 Months from Manufacture (EXP: 28/12/2026)",
            fontHeightMm = 2.4,
            pdpAreaSqCm = 48.0,
            expectedComplianceScore = 100,
            expectedStatus = InspectionStatus.COMPLIANT,
            rawLabelText = """
                KURKURE MASALA MUNCH - NAMKEEN (PROPRIETARY FOOD 15.1)
                Net Quantity: 19.7 g [(12 g + 6 g EXTRA) + 1.7 g]
                MRP Rs. 5/- (INCL. OF ALL TAXES)
                UNIT SALE PRICE: Rs. 0.25/- PER g (Rs. 25.38 / 100g)
                MFD & USE BY: 30/08/26 & 28/12/26 | BATCH NO: 1.0/RP 300826 #
                Country of Origin: India (Marketed by PepsiCo India Holdings Pvt. Ltd.)
                Barcode: 8901491367219 (GS1 EAN-13 Barcode)
                FSSAI Mkt. Lic. No: 10014064000435
                Consumer Services Manager: P.O. BOX-27, DLF QUTAB ENCLAVE, PHASE-I, GURUGRAM - 122002, HARYANA | 1800 22 4020 | CONSUMER.FEEDBACK@PEPSICO.COM
                Ingredients: Cereal Products (67%) (Rice Meal (44%), Corn Meal (23%)), Edible Vegetable Oil (Palmolein @ or Rice Bran #), Seasoning (Spices and Condiments, Iodised Salt, Sugar, Flavour (Natural and Nature Identical Flavouring Substances), Black Salt, Tomato Powder, Acidity Regulators (330, 296, 508), Colour (160c), Maltodextrin), Gram Meal (0.5%).
                Nutritional Information (per 100g): Energy 554 kcal, Protein 6.0g, Carbohydrates 57.5g (Total Sugars 1.4g, Added Sugars 0.8g), Total Fat 33.4g (Saturated Fat 15.1g @ / 7.7g #, Trans Fat 0.1g), Sodium 694mg.
                Per Serving (20g): Energy 111 kcal, Protein 1.2g, Carbohydrates 11.5g (Total Sugars 0.3g, Added Sugars 0.2g), Total Fat 6.7g (Saturated Fat 3.0g, Trans Fat 0.02g), Sodium 139mg.
                Hidden / Undeclared Elements: Edible Vegetable Oil percentage (undisclosed on packaging), Seasoning blend percentage (undisclosed on packaging), Maltodextrin carrier starch quantity (undisclosed).
                Dietary Precautions: High Sodium (694mg/100g) - limit daily intake for hypertension; Saturated Fat (15.1g/100g) - portion control advised; Calorie dense (554 kcal/100g); Allergen: May contain Wheat, Soy, Peanut traces.
                QR Payload: 8901491367219 | FSSAI:10014064000435 | BATCH:1.0/RP 300826 # | MRP:5.00 | NET:19.7g
            """.trimIndent(),
            description = "Kurkure Masala Munch (PepsiCo India) 19.7g Pack (₹5) scanned via Barcode 8901491367219. Complete ingredient transparency, undisclosed percentages breakdown, INS additives (330, 296, 508, 160c), exact nutrition, and health precautions.",
            violationSummary = "Statutory Verdict: 100% PASS. Valid EAN-13 barcode (8901491367219), declared MRP ₹5.00, dynamic USP ₹25.38/100g, FSSAI Lic. 10014064000435, QUID cereal disclosures, and vegetarian green logo.",
            ingredients = "Cereal Products (67%) (Rice Meal (44%), Corn Meal (23%)), Edible Vegetable Oil (Palmolein @ or Rice Bran #) (percentage not disclosed on package), Seasoning (percentage not disclosed on package) (Spices & Condiments, Iodised Salt, Sugar, Flavour [Natural & Nature Identical Flavouring Substances], Black Salt, Tomato Powder, Acidity Regulators [INS 330, INS 296, INS 508], Colour [INS 160c], Maltodextrin), Gram Meal (0.5%)",
            nutritionalInfo = "Per 100g: Energy 554 kcal, Protein 6.0g, Carbohydrates 57.5g (Total Sugars 1.4g, Added Sugars 0.8g), Total Fat 33.4g (Saturated Fat 15.1g @ / 7.7g #, Trans Fat 0.1g), Sodium 694mg | Per Serving (20g): Energy 111 kcal, Protein 1.2g, Carbs 11.5g, Total Fat 6.7g, Sodium 139mg",
            allergens = "Contains Wheat and Soy ingredients. May contain traces of Milk and Peanuts.",
            batchNo = "1.0/RP 300826 #",
            licenseNo = "FSSAI Mkt. Lic. No. 10014064000435 • GS1 EAN-13: 8901491367219",
            qrCodeData = "GS1 EAN-13: 8901491367219 | FSSAI: 10014064000435 | Brand: Kurkure (PepsiCo) | Batch: 1.0/RP 300826 # | MRP: ₹5.00 | Net: 19.7g",
            chemicalSpecs = "Moisture: 1.5% (Limit <= 3.0%) • Peroxide Value: 2.6 meq/kg • Free Fatty Acids: 0.24% • Preservatives within FSSAI Schedule limits [PASS]",
            quidDetails = "Cereal Products: 67% (Rice Meal 44%, Corn Meal 23%) • Gram Meal: 0.5% (Explicitly Disclosed) • Edible Vegetable Oil: Percentage not disclosed on package • Seasoning Mix: Percentage not disclosed on package • Maltodextrin: Quantity not disclosed on package",
            foplWarning = "🟡 High Saturated Fat (15.1g/100g @ Palmolein) • 🟡 High Sodium Concern (694mg/100g) • 🟢 Added Sugar: Low (0.8g/100g) • 🟢 Trans Fat: Low (0.1g/100g)"
        ),
        SamplePackage(
            id = "SAMPLE_KURKURE_MRP20",
            title = "Kurkure Masala Munch Family Pack (78g / ₹20)",
            brand = "PepsiCo India Holdings Pvt. Ltd.",
            category = "Packaged Food & Namkeen (Proprietary Food 15.1)",
            netQuantity = "78 g",
            declaredMrp = "₹ 20.00 (inclusive of all taxes)",
            declaredUsp = "₹ 0.256 / g (₹ 25.64 / 100g calculated)",
            countryOfOrigin = "India",
            manufacturer = "PepsiCo India Holdings Pvt. Ltd., Village Channo, Sangrur, Punjab - 148026 / DLF Qutab Enclave, Gurugram, Haryana - 122002",
            consumerCare = "Toll Free: 1800-22-4020, Email: consumer.feedback@pepsico.com, Address: Consumer Services Manager, PepsiCo India, PO Box 27, DLF Qutab Enclave, Gurugram, Haryana - 122002",
            mfgDate = "30/08/2026",
            expiryDate = "Best Before 4 Months from Manufacture (EXP: 28/12/2026)",
            fontHeightMm = 3.0,
            pdpAreaSqCm = 110.0,
            expectedComplianceScore = 100,
            expectedStatus = InspectionStatus.COMPLIANT,
            rawLabelText = """
                KURKURE MASALA MUNCH - FAMILY PACK (MRP ₹20)
                Net Quantity: 78 g
                MRP Rs. 20/- (INCL. OF ALL TAXES)
                UNIT SALE PRICE: Rs. 0.256/- PER g (Rs. 25.64 / 100g)
                MFD & USE BY: 30/08/26 & 28/12/26 | BATCH NO: 2.0/RP 300826 #
                Country of Origin: India | Barcode: 8901491367219
                FSSAI Mkt. Lic. No: 10014064000435 | Consumer Helpline: 1800 22 4020
                Ingredients: Cereal Products (67%) (Rice Meal (44%), Corn Meal (23%)), Edible Vegetable Oil (Palmolein @ or Rice Bran #), Seasoning (Spices and Condiments, Iodised Salt, Sugar, Flavour (Natural and Nature Identical Flavouring Substances), Black Salt, Tomato Powder, Acidity Regulators [330, 296, 508], Colour [160c], Maltodextrin), Gram Meal (0.5%).
                Nutritional Information (per 100g): Energy 554 kcal, Protein 6.0g, Carbohydrates 57.5g (Total Sugars 1.4g, Added Sugars 0.8g), Total Fat 33.4g (Saturated Fat 15.1g @ / 7.7g #, Trans Fat 0.1g), Sodium 694mg.
                Health Precautions: High Sodium (694mg/100g) & Saturated Fat (15.1g/100g) - portion control advised.
            """.trimIndent(),
            description = "Kurkure Masala Munch ₹20 Family Pack (78g) with Barcode 8901491367219. Detailed hidden ingredient analysis, INS additives, unit pricing ₹25.64/100g, and dietary precautions.",
            violationSummary = "Statutory Verdict: 100% PASS. Valid EAN-13 barcode (8901491367219), declared MRP ₹20.00, dynamic USP ₹25.64/100g, FSSAI Lic. 10014064000435.",
            ingredients = "Cereal Products (67%) (Rice Meal (44%), Corn Meal (23%)), Edible Vegetable Oil (Palmolein @ or Rice Bran #) (percentage not disclosed on package), Seasoning (percentage not disclosed on package) (Spices and Condiments, Iodised Salt, Sugar, Flavour (Natural and Nature Identical Flavouring Substances), Black Salt, Tomato Powder, Acidity Regulators [INS 330, INS 296, INS 508], Colour [INS 160c], Maltodextrin), Gram Meal (0.5%)",
            nutritionalInfo = "Per 100g: Energy 554 kcal, Protein 6.0g, Carbohydrates 57.5g (Total Sugars 1.4g, Added Sugars 0.8g), Total Fat 33.4g (Saturated Fat 15.1g @ / 7.7g #, Trans Fat 0.1g), Sodium 694mg | Per Serving (20g): Energy 111 kcal, Protein 1.2g, Carbs 11.5g, Total Fat 6.7g, Sodium 139mg",
            allergens = "Contains Wheat and Soy ingredients. May contain traces of Milk and Peanuts.",
            batchNo = "2.0/RP 300826 #",
            licenseNo = "FSSAI Mkt. Lic. No. 10014064000435 • GS1 EAN-13: 8901491367219",
            qrCodeData = "GS1 EAN-13: 8901491367219 | FSSAI: 10014064000435 | Brand: Kurkure (PepsiCo) | MRP: ₹20.00 | Net: 78g",
            chemicalSpecs = "Moisture: 1.5% (Limit <= 3.0%) • Peroxide Value: 2.6 meq/kg • Free Fatty Acids: 0.24% [PASS]",
            quidDetails = "Cereal Products: 67% (Rice Meal 44%, Corn Meal 23%) • Gram Meal: 0.5% (Explicitly Disclosed) • Edible Vegetable Oil: Percentage not disclosed on package • Seasoning Mix: Percentage not disclosed on package",
            foplWarning = "🟡 High Saturated Fat (15.1g/100g @ Palmolein) • 🟡 High Sodium Concern (694mg/100g) • 🟢 Added Sugar: Low (0.8g/100g) • 🟢 Trans Fat: Low (0.1g/100g)"
        ),
        SamplePackage(
            id = "SAMPLE_MUSTARD_OIL",
            title = "Shri Krishna Pure Kachi Ghani Mustard Oil (1 Litre / 910g)",
            brand = "KrishiVeda Agro Industries Pvt. Ltd.",
            category = "Agriculture & Edible Oils (FSSAI 2.2.1 & AGMARK Grade-1)",
            netQuantity = "1 L (910 g)",
            declaredMrp = "₹ 185.00 (inclusive of all taxes)",
            declaredUsp = "₹ 0.185 / ml (₹ 185.00 / L)",
            countryOfOrigin = "India",
            manufacturer = "KrishiVeda Agro Mills Ltd., Plot 42, G.T. Road, Karnal, Haryana - 132001 (Govt Reg: HR-IND-4421)",
            consumerCare = "Toll Free Helpline: 1800-180-1551, Email: care@krishiveda.in, Address: KrishiVeda Grievance Cell, Plot 42, Karnal, Haryana - 132001",
            mfgDate = "02/2026",
            expiryDate = "Best before 9 months from packaging (Nov 2026)",
            fontHeightMm = 6.2,
            pdpAreaSqCm = 140.0,
            expectedComplianceScore = 100,
            expectedStatus = InspectionStatus.COMPLIANT,
            rawLabelText = """
                SHRI KRISHNA PURE KACHI GHANI MUSTARD OIL (COLD PRESSED)
                Net Quantity: 1 Litre (910 g)
                MRP: ₹ 185.00 (inclusive of all taxes)
                Unit Sale Price (USP): ₹ 0.185 / ml (₹ 185.00 / L)
                Month & Year of Packing: 02/2026 | Batch No: LOT-DGM-2026-B44
                Best Before: 9 months from packaging date (Use by 11/2026)
                Country of Origin: India (Made in India)
                Manufactured & Packed by: KrishiVeda Agro Mills Ltd, Plot 42, G.T. Road, Karnal, Haryana - 132001
                Consumer Grievance Cell: 1800-180-1551 | care@krishiveda.in
                FSSAI Central License No: 10018013000842
                AGMARK Certificate of Grading: CA-8492 Grade 1 (Special Mustard Oil)
                Ingredients: 100% Pure Cold-Pressed Raw Mustard Seed Extract (Brassica juncea) (99.85%), Fortified with Vitamin A (Retinyl Palmitate, 25 IU/g) and Vitamin D2 (Cholecalciferol, 4.5 IU/g).
                Nutritional Facts (per 100g): Energy 900 kcal, Protein 0g, Carbohydrate 0g (Total Sugars 0g), Total Fat 100g (Saturated Fatty Acids 6.8g, MUFA 67.4g, PUFA 25.8g [Omega-3 ALA 12.1g, Omega-6 13.7g], Trans Fat 0.0g), Cholesterol 0mg, Vitamin A 750 mcg RE, Vitamin D 11.25 mcg.
                Allergens: Contains Mustard Seeds. Free from Argemone Oil, Mineral Oil, Castor Oil, Adulterants or Artificial Colors.
                Chemical Authenticity Parameters: Acid Value 1.15 mg KOH/g (Limit <= 1.50), Iodine Value 104.2 (Limit 98-110), Refractive Index at 40°C 1.4655 (Standard 1.4646-1.4662), Saponification Value 174.5, Natural Allyl Isothiocyanate 0.34% (Standard >= 0.20%), Argemone / Mineral Oil: NEGATIVE.
                QR Payload: 010890123456789010DGM2026B44172611302118500
            """.trimIndent(),
            description = "Official SIH 2026 Golden Benchmark: Certified 100% Cold Pressed Mustard Oil with full Legal Metrology, FSSAI 2020 QUID & AGMARK Grade-1 audit trail.",
            violationSummary = "Statutory Verdict: 100% PASS. All Rule 6(1)(a)-(g) declarations, Schedule II font height, USP calculation, +F Fortification logo, and AGMARK traceability fully verified.",
            ingredients = "100% Pure Cold-Pressed Raw Mustard Seed Extract (Brassica juncea) (99.85%), Fortified with Vitamin A (Retinyl Palmitate, 25 IU/g) and Vitamin D2 (Cholecalciferol, 4.5 IU/g)",
            nutritionalInfo = "Per 100g: Energy 900 kcal, Protein 0g, Total Carbohydrates 0g (Added Sugars 0g), Total Fat 100g (Saturated Fat 6.8g, MUFA 67.4g, PUFA 25.8g [Omega-3 Alpha Linolenic Acid 12.1g, Omega-6 13.7g], Trans Fat 0.0g), Cholesterol 0mg, Sodium 0mg, Vitamin A 750 mcg, Vitamin D 11.25 mcg",
            allergens = "Contains Mustard Seeds. Naturally Gluten-Free. Free from Argemone Oil, Mineral Oil, Castor Oil, and Synthetic Colors.",
            batchNo = "LOT-DGM-2026-B44",
            licenseNo = "FSSAI Central Lic. No. 10018013000842 • AGMARK Grade-1 CA-8492 • BIS IS:546",
            qrCodeData = "GS1-128: (01)08901234567890(10)DGM2026B44(17)261130(21)18500 | FSSAI: 10018013000842 | AGMARK: CA-8492/Grade-1 | SHA256: 8f4b62d3a91c78e5f29a0b12e4d6c7b981",
            chemicalSpecs = "Acid Value: 1.15 mg KOH/g (Statutory Limit <= 1.50) [PASS] • Iodine Value: 104.2 (Standard 98-110) [PASS] • Refractive Index (40°C): 1.4655 (Standard 1.4646-1.4662) [PASS] • Saponification Value: 174.5 [PASS] • Natural Pungency (AITC): 0.34% (Standard >= 0.20%) [PASS] • Argemone & Mineral Oil: NEGATIVE / ABSENT [PASS]",
            quidDetails = "Pure Cold-Pressed Mustard Extract: 99.85% (QUID Declared) • Fortified Micro-nutrients: 0.15% (Vit A & D2 as per FSSAI Food Fortification Regulations)",
            foplWarning = "🟢 FSSAI Trans-Fat Free (0.0g) • 🟢 High in Cardio-Protective MUFA/PUFA • 🟢 Naturally Pungent Kachi Ghani • 🟢 +F Logo Verified"
        ),
        SamplePackage(
            id = "SAMPLE_ADULTERATED_BLENDED_OIL",
            title = "PurityPro Gold Super Cook Oil (1 Litre)",
            brand = "PurityPro Agro Oils",
            category = "Edible Oils (Disguised Blending Violation)",
            netQuantity = "1 Litre",
            declaredMrp = "MRP Rs. 135.00",
            declaredUsp = "MISSING",
            countryOfOrigin = "India",
            manufacturer = "PurityPro Agro Oils, Phase-1, RIICO Industrial Area, Alwar, Rajasthan",
            consumerCare = "care@puritypro.com (No helpline phone or nodal officer)",
            mfgDate = "01/2026",
            expiryDate = "Best before 6 months",
            fontHeightMm = 1.6,
            pdpAreaSqCm = 135.0,
            expectedComplianceScore = 35,
            expectedStatus = InspectionStatus.SEIZURE_RECOMMENDED,
            rawLabelText = """
                PURITYPRO GOLD SUPER COOK OIL
                Net Volume: 1 Litre. Price: Rs. 135.00.
                Mfd: 01/2026. Packed by PurityPro Agro Oils Alwar Rajasthan.
                Contact: care@puritypro.com.
                Ingredients: Refined Vegetable Oil (Blend of Refined Palmolein 65% and Refined Mustard Oil 35%), Synthetic Antioxidant (INS 319).
                Batch: PPO-OIL-091. FSSAI Lic: 10019014000219.
            """.trimIndent(),
            description = "Misbranded edible oil sold with misleading 'Pure Gold' claims, masking 65% imported Palmolein without front-of-pack Blended Edible Oil declaration and missing USP.",
            violationSummary = "Statutory Contraventions Flagged:\n• Rule 6(1)(e): Unit Sale Price (USP) completely omitted\n• Rule 6(1)(da): MRP lacks '(inclusive of all taxes)' phrase\n• FSSAI Blended Oil Regulation: Front-of-pack lacks mandatory box declaring 'BLENDED EDIBLE VEGETABLE OIL' in bold capital letters with exact % ratio\n• Rule 6(1)(g): Incomplete grievance redressal (no phone/toll-free number)\n• Rule 9: Font height 1.6mm is below the statutory 4.0mm requirement for 1L packs",
            ingredients = "Refined Palmolein Oil (65%), Refined Mustard Oil (35%), Synthetic Antioxidant (TBHQ - INS 319) (180 ppm)",
            nutritionalInfo = "Per 100g: Energy 900 kcal, Protein 0g, Total Fat 100g (Saturated Fatty Acids 38.4g, MUFA 42.1g, PUFA 19.5g, Trans Fat 0.3g), Cholesterol 0mg, Sodium 0mg",
            allergens = "Contains Mustard. High in Saturated Palmitic Fatty Acids.",
            batchNo = "LOT-PPO-OIL-091",
            licenseNo = "FSSAI Lic. No. 10019014000219 (Under Investigation)",
            qrCodeData = "INVALID_QR_MISSING_GTIN | Data: PurityPro-Batch-091",
            chemicalSpecs = "Acid Value: 2.45 mg KOH/g (Limit <= 1.50) [FAIL] • Iodine Value: 68.4 (Mustard standard 98-110, Palmolein dilution confirmed) [FAIL] • Refractive Index (40°C): 1.4582 [FAIL] • High Saturated Fat Content: 38.4% [FAIL]",
            quidDetails = "Masked Blend: Refined Palmolein 65%, Refined Mustard 35% (Violates Front-of-Pack Blended Oil Display Mandate)",
            foplWarning = "🔴 WARNING: High Saturated Fat (38.4g/100g) • ⚠️ Missing Mandatory 'BLENDED EDIBLE VEGETABLE OIL' Front Box • 🔴 Missing USP"
        ),
        SamplePackage(
            id = "SAMPLE_CHIPS_VIOLATION",
            title = "Crunchy Masala Potato Chips (95g)",
            brand = "TasteBite Foods Pvt. Ltd.",
            category = "Packaged Food & Snacks",
            netQuantity = "95 g",
            declaredMrp = "MRP Rs. 30.00",
            declaredUsp = "MISSING",
            countryOfOrigin = "India",
            manufacturer = "TasteBite Foods Pvt. Ltd., Sector 62, Noida, UP - 201301",
            consumerCare = "Email: contact@tastebite.com (No phone / address provided)",
            mfgDate = "01/2026",
            expiryDate = "Use within 6 months (Expiry: Jul 2026)",
            fontHeightMm = 1.2,
            pdpAreaSqCm = 65.0,
            expectedComplianceScore = 50,
            expectedStatus = InspectionStatus.NON_COMPLIANT,
            rawLabelText = "CRUNCHY MASALA POTATO CHIPS. Net Weight 95g. MRP Rs. 30.00. Mfd 01/2026. Packed by TasteBite Foods Noida UP. For feedback email contact@tastebite.com. Ingredients: Fresh Potatoes (62%), Edible Palmolein Oil (32%), Spices & Condiments (Chilli powder, Onion powder, Amchur, Cumin), Iodized Salt, Flavour Enhancers (INS 627, INS 631). Batch: TB-CHI-081.",
            description = "Snack packet missing Unit Sale Price (USP), missing '(incl. of all taxes)' on MRP, and has undersized font height.",
            violationSummary = "Violations:\n• Rule 6(1)(e): Unit Sale Price (USP) missing\n• Rule 6(1)(da): MRP missing '(inclusive of all taxes)' phrase\n• Rule 6(1)(g): Incomplete consumer care details (missing telephone number)\n• Rule 9: Font height 1.2mm (< 2.0mm required for 95g package)",
            ingredients = "Fresh Potatoes (62%), Edible Palmolein Oil (32%), Spices & Condiments (Red Chilli, Dry Mango, Cumin, Black Pepper), Iodized Salt, Flavour Enhancer (INS 627, INS 631), Antioxidant (INS 319)",
            nutritionalInfo = "Per 100g: Energy 542 kcal, Carbohydrates 52.4g (Total Sugars 2.1g, Added Sugar 0g), Protein 6.8g, Total Fat 34.2g (Saturated Fat 15.8g, Trans Fat 0.1g), Sodium 780mg",
            allergens = "May contain traces of Gluten, Milk Solids, and Soy.",
            batchNo = "TB-CHI-081",
            licenseNo = "FSSAI Lic. No. 10020051000318",
            qrCodeData = "GS1: (01)08902005100031(10)TBCHI081(17)260715(21)03000",
            chemicalSpecs = "Moisture: 1.8% • Total Peroxide Value: 4.2 meq/kg • Free Fatty Acids: 0.42%",
            quidDetails = "Fresh Potatoes: 62% • Edible Palmolein Oil: 32% • Spices & Seasoning: 6%",
            foplWarning = "🔴 High in Sodium (780mg/100g) • 🟡 High in Saturated Fat (15.8g/100g)"
        ),
        SamplePackage(
            id = "SAMPLE_IMPORTED_SPREAD",
            title = "Alpine Swiss Hazelnut Cocoa Cream (350g)",
            brand = "Alps Treats AG",
            category = "Imported Confectionery",
            netQuantity = "350 g (12.3 oz)",
            declaredMrp = "₹ 480",
            declaredUsp = "MISSING",
            countryOfOrigin = "MISSING / NOT DECLARED",
            manufacturer = "Alps Treats AG, Zurich, Switzerland (No Indian Importer declared)",
            consumerCare = "feedback@alpstreats.ch (No Indian contact)",
            mfgDate = "11/2025",
            expiryDate = "EXP 11/2026 (Best before 12 months)",
            fontHeightMm = 1.8,
            pdpAreaSqCm = 85.0,
            expectedComplianceScore = 25,
            expectedStatus = InspectionStatus.SEIZURE_RECOMMENDED,
            rawLabelText = "ALPINE HAZELNUT COCOA SPREAD. Weight: 350g. Price: 480 Rs. Mfd by Alps Treats AG, Zurich. Contact: feedback@alpstreats.ch. Ingredients: Sugar, Vegetable Fat (Palm, Shea), Hazelnuts (13%), Skimmed Milk Powder (8.7%), Fat-Reduced Cocoa (7.4%), Emulsifier (Soy Lecithin), Vanillin. Lot: CH-ZR-904.",
            description = "Imported product sold without Indian Importer declaration, missing Country of Origin, no USP, and no Indian consumer grievance details.",
            violationSummary = "Severe Offenses under Section 36 & Rule 32:\n• Rule 6(1)(f): Country of Origin missing\n• Rule 6(1)(a): Indian Importer name & registered address missing\n• Rule 6(1)(e): Unit Sale Price missing\n• Rule 6(1)(da): Non-standard MRP declaration without tax inclusion statement\n• Rule 6(1)(g): No Indian grievance redressal contact",
            ingredients = "Sugar (50.1%), Vegetable Fat (Palm, Shea) (30.9%), Hazelnuts (13%), Skimmed Milk Powder (8.7%), Fat-Reduced Cocoa Powder (7.4%), Emulsifier: Soy Lecithin (INS 322), Nature Identical Flavouring Substance (Vanillin)",
            nutritionalInfo = "Per 100g: Energy 539 kcal, Carbohydrates 57.5g (Total Sugars 56.3g, Added Sugars 50.1g), Protein 6.3g, Total Fat 30.9g (Saturated Fat 10.6g, Trans Fat 0g), Sodium 42mg",
            allergens = "Contains Tree Nuts (Hazelnuts), Milk, and Soy. May contain Almonds and Pistachios.",
            batchNo = "LOT: CH-ZR-904",
            licenseNo = "UNREGISTERED IMPORTER (No FSSAI Import License printed)",
            qrCodeData = "EAN-13: 7610123456789 (Non-Indian GS1 Prefix 761 Switzerland - No Indian FSSAI Link)",
            chemicalSpecs = "Sugar: 56.3% • Cocoa Solids: 7.4% • Moisture: 1.2%",
            quidDetails = "Hazelnuts: 13% • Skimmed Milk: 8.7% • Fat-Reduced Cocoa: 7.4%",
            foplWarning = "🔴 High in Added Sugar (50.1g/100g) • 🔴 Unregistered Importer Violation"
        ),
        SamplePackage(
            id = "SAMPLE_BASMATI_RICE",
            title = "Royal Heritage Aged Basmati Rice (5 kg)",
            brand = "Royal Grains Ltd",
            category = "Agriculture & Staples",
            netQuantity = "5 kg",
            declaredMrp = "₹ 720.00 (incl. of all taxes)",
            declaredUsp = "₹ 144.00 / kg",
            countryOfOrigin = "India",
            manufacturer = "Royal Grains Millers, Karnal, Haryana - 132001",
            consumerCare = "Grievance Officer: 0184-2255889, care@royalgrains.com",
            mfgDate = "MISSING MONTH/YEAR",
            expiryDate = "Best before 24 months from packing",
            fontHeightMm = 6.5,
            pdpAreaSqCm = 320.0,
            expectedComplianceScore = 78,
            expectedStatus = InspectionStatus.MINOR_VIOLATIONS,
            rawLabelText = "ROYAL HERITAGE BASMATI RICE 5kg. MRP Rs. 720 (incl. of all taxes). USP: Rs. 144/kg. Country of Origin: India. Mfd by Royal Grains Millers Karnal Haryana 132001. Consumer support: care@royalgrains.com, 0184-2255889. Ingredients: 100% Traditional Aged Basmati Rice (Raw Milled). Crop Year: 2025. Batch: RGM-BAS-772. FSSAI: 10817005000192.",
            description = "Premium rice bag compliant on USP, MRP, and origin, but month/year of packing was omitted on the batch stamp.",
            violationSummary = "Minor Defect:\n• Rule 6(1)(d): Month and year of packing / manufacture missing on label panel",
            ingredients = "100% Traditional Aged Long Grain Basmati Rice (Raw Milled)",
            nutritionalInfo = "Per 100g uncooked: Energy 356 kcal, Protein 8.2g, Carbohydrates 78.4g (Dietary Fibre 1.6g, Sugars 0g), Total Fat 0.6g, Iron 1.2mg",
            allergens = "Naturally Gluten-Free. Packed in a facility handling whole grains.",
            batchNo = "RGM-BAS-772",
            licenseNo = "FSSAI Lic. No. 10817005000192 • AGMARK Grade Special (Basmati)",
            qrCodeData = "GS1: (01)08901081700500(10)RGMBAS772(17)270228(21)72000",
            chemicalSpecs = "Moisture: 11.4% (Limit <= 14.0%) • Average Grain Length: 8.35 mm (Standard >= 6.61 mm) • Elongation Ratio: 2.1x • Admixture of other grains: < 1.0%",
            quidDetails = "Pure Aged Basmati Rice: 100%",
            foplWarning = "🟢 Whole Grain Staple • 🟢 Zero Trans Fat • 🟢 Naturally Low in Sodium"
        ),
        SamplePackage(
            id = "SAMPLE_BIO_FERTILIZER_FCO",
            title = "KrishiShakti Bio-NPK Organic Fertilizer Sack (50 kg)",
            brand = "KrishiShakti Bio Fertilizers",
            category = "Agriculture & Fertilizer Control Order (FCO 1985)",
            netQuantity = "50 kg (when packed)",
            declaredMrp = "₹ 1,350.00 (inclusive of all taxes & Govt. Subsidy)",
            declaredUsp = "₹ 27.00 / kg",
            countryOfOrigin = "India",
            manufacturer = "KrishiShakti Agro Chemicals, GIDC Estate, Panoli, Gujarat - 394116",
            consumerCare = "Toll Free: 1800-233-4455, Email: agronomy@krishishakti.com",
            mfgDate = "02/2026",
            expiryDate = "Best used within 12 months (Feb 2027)",
            fontHeightMm = 8.5,
            pdpAreaSqCm = 650.0,
            expectedComplianceScore = 100,
            expectedStatus = InspectionStatus.COMPLIANT,
            rawLabelText = """
                KRISHISHAKTI BIO-NPK ORGANIC FERTILIZER SACK
                Net Weight: 50 kg (when packed)
                MRP: Rs. 1350.00 (inclusive of all taxes) | USP: Rs. 27.00 / kg
                Date of Mfg: 02/2026 | Batch: KS-NPK-2026-09
                FCO License No: GUJ/FCO/NPK/2021/8842
                Manufactured by: KrishiShakti Agro Chemicals, Panoli, Gujarat - 394116
                Customer Care: 1800-233-4455, agronomy@krishishakti.com
                Nutrient Composition (as per FCO Schedule I): Total Nitrogen (N) 12.0% min, Available Phosphate (P2O5) 8.0% min, Water Soluble Potash (K2O) 10.0% min, Organic Carbon 16.0% min, Moisture 18.0% max, C:N ratio < 20:1. Heavy Metals (Lead, Cadmium, Arsenic) within statutory threshold limits.
            """.trimIndent(),
            description = "Statutory 50kg agricultural fertilizer sack certified under Fertilizer (Control) Order 1985 and Legal Metrology Rules.",
            violationSummary = "Statutory Verdict: 100% PASS under FCO 1985 & Rule 6 of PCR 2011. Guaranteed nutrient analysis, moisture limit, and subsidized MRP accurately printed.",
            ingredients = "Bio-Enriched Compost, Rock Phosphate, Potash derived from molasses, Azotobacter & PSB bio-inoculants",
            nutritionalInfo = "Guaranteed Analysis: Nitrogen (N): 12.0%, Phosphate (P2O5): 8.0%, Potash (K2O): 10.0%, Organic Carbon: 16.0%, Moisture: 18.0% max",
            allergens = "Agricultural product. Not for human or animal consumption. Wear protective gloves when handling.",
            batchNo = "KS-NPK-2026-09",
            licenseNo = "FCO Mfg. Lic. GUJ/FCO/NPK/2021/8842 • CIBRC Certified",
            qrCodeData = "DBT-Fertilizer-QR: (01)08903344556677(10)KSNPK202609(17)270228(21)135000 | FCO: GUJ/FCO/8842",
            chemicalSpecs = "Moisture: 16.8% (Limit <= 18.0%) • Total Nitrogen: 12.4% • Available P2O5: 8.2% • Water Soluble K2O: 10.1% • Heavy Metals Lead < 50 ppm, Cadmium < 5 ppm",
            quidDetails = "NPK Ratio: 12:8:10 with 16% Organic Humic Carbon",
            foplWarning = "🟢 Govt. FCO Schedule-I Certified • 🟢 Zero Harmful Heavy Metal Contamination"
        )
    )
}
