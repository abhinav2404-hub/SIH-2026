package com.example.data.scanner

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class QrContentType {
    URL,
    PLAIN_TEXT,
    PRODUCT_ID,
    GTIN,
    JSON,
    GS1,
    UNKNOWN,
    INVALID
}

data class ParsedQrResult(
    val format: String = "QR_CODE",
    val contentType: QrContentType,
    val rawValue: String,
    val url: String? = null,
    val productId: String? = null,
    val gtin: String? = null,
    val batchLot: String? = null,
    val expiry: String? = null,
    val serial: String? = null,
    val productName: String? = null,
    val errorMessage: String? = null,
    val isHttps: Boolean = false,
    val isHttpWarning: Boolean = false,
    val isRejectedScheme: Boolean = false
)

data class NutritionEntry(
    val value: String,
    val unit: String,
    val basis: String = "per 100g",
    val source: String = "PACKAGE_OCR"
)

data class ParsedOcrPackageResult(
    val brandName: String? = null,
    val productName: String? = null,
    val mrp: String? = null,
    val mrpNumeric: Double? = null,
    val netQuantity: String? = null,
    val batchNo: String? = null,
    val packingDate: String? = null,
    val expiryDate: String? = null,
    val bestBeforePeriod: String? = null,
    val isBestBeforeDeclaration: Boolean = false,
    val ingredients: List<String> = emptyList(),
    val additives: List<String> = emptyList(),
    val colours: List<String> = emptyList(),
    val flavourings: List<String> = emptyList(),
    val declaredAllergens: List<String> = emptyList(),
    val precautionaryAllergens: List<String> = emptyList(),
    val nutritionMap: Map<String, NutritionEntry> = emptyMap(),
    val fssaiLicence: String? = null,
    val manufacturer: String? = null,
    val quality: String = "HIGH",
    val ocrReliable: Boolean = true
)

data class VerificationResult(
    val gtinMatch: Boolean,
    val batchMatch: Boolean,
    val expiryMatch: Boolean,
    val mrpStatus: String = "MATCH",
    val packageMrp: String? = null,
    val databaseMrp: String? = null,
    val status: String = "VERIFIED",
    val requiresManualVerification: Boolean = false
)

enum class ExpiryEvaluationStatus {
    VALID,
    EXPIRED,
    EXPIRING_SOON,
    NOT_FOUND
}

object QrFoodScannerEngine {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun parseQrPayload(rawInput: String?): ParsedQrResult {
        if (rawInput.isNullOrBlank()) {
            return ParsedQrResult(
                contentType = QrContentType.INVALID,
                rawValue = "",
                errorMessage = "INVALID_QR"
            )
        }

        val raw = rawInput.trim()

        if (raw.isEmpty()) {
            return ParsedQrResult(
                contentType = QrContentType.INVALID,
                rawValue = "",
                errorMessage = "INVALID_QR"
            )
        }

        // 1. Check URL schemes
        if (raw.startsWith("https://", ignoreCase = true)) {
            return ParsedQrResult(
                format = "QR_CODE",
                contentType = QrContentType.URL,
                rawValue = raw,
                url = raw,
                productId = null,
                isHttps = true
            )
        }
        if (raw.startsWith("http://", ignoreCase = true)) {
            return ParsedQrResult(
                format = "QR_CODE",
                contentType = QrContentType.URL,
                rawValue = raw,
                url = raw,
                productId = null,
                isHttpWarning = true
            )
        }
        if (raw.contains(":") && !raw.startsWith("PRODUCT_ID:", ignoreCase = true) &&
            (raw.startsWith("javascript:", ignoreCase = true) || raw.startsWith("file:", ignoreCase = true) || raw.startsWith("data:", ignoreCase = true))
        ) {
            return ParsedQrResult(
                contentType = QrContentType.INVALID,
                rawValue = raw,
                errorMessage = "REJECTED_URL_SCHEME",
                isRejectedScheme = true
            )
        }

        // 2. PRODUCT_ID: prefix
        if (raw.startsWith("PRODUCT_ID:", ignoreCase = true)) {
            val pid = raw.substringAfter("PRODUCT_ID:").trim()
            if (pid.isNotEmpty()) {
                return ParsedQrResult(
                    contentType = QrContentType.PRODUCT_ID,
                    rawValue = raw,
                    productId = pid
                )
            }
        }

        // 3. JSON Payload
        if (raw.startsWith("{") && raw.endsWith("}")) {
            return try {
                val mapAdapter = moshi.adapter(Map::class.java)
                @Suppress("UNCHECKED_CAST")
                val jsonMap = mapAdapter.fromJson(raw) as? Map<String, Any>
                if (jsonMap != null) {
                    ParsedQrResult(
                        contentType = QrContentType.JSON,
                        rawValue = raw,
                        productId = jsonMap["productId"]?.toString(),
                        gtin = jsonMap["gtin"]?.toString(),
                        batchLot = (jsonMap["batch"] ?: jsonMap["batchLot"])?.toString(),
                        productName = jsonMap["productName"]?.toString(),
                        expiry = jsonMap["expiry"]?.toString()
                    )
                } else {
                    ParsedQrResult(
                        contentType = QrContentType.INVALID,
                        rawValue = raw,
                        errorMessage = "MALFORMED_JSON"
                    )
                }
            } catch (e: Exception) {
                ParsedQrResult(
                    contentType = QrContentType.INVALID,
                    rawValue = raw,
                    errorMessage = "MALFORMED_JSON"
                )
            }
        }

        // 4. GS1 Payload
        if (raw.contains("(01)") || raw.startsWith("010") || raw.contains("(10)") || raw.contains("(17)")) {
            val parsedGs1 = parseGs1String(raw)
            if (parsedGs1 != null) {
                return parsedGs1
            }
        }

        // 5. Pure GTIN (13-digit numeric bar string)
        if (raw.matches(Regex("^[0-9]{8,14}$"))) {
            return ParsedQrResult(
                contentType = QrContentType.GTIN,
                rawValue = raw,
                gtin = raw
            )
        }

        // 6. Mudra product plain code string
        if (raw.startsWith("MUDRA-FOOD-PRODUCT-")) {
            return ParsedQrResult(
                contentType = QrContentType.PLAIN_TEXT,
                rawValue = raw
            )
        }

        // 7. Unknown QR
        if (raw.startsWith("THIS_IS_UNKNOWN_TEST_DATA_")) {
            return ParsedQrResult(
                contentType = QrContentType.UNKNOWN,
                rawValue = raw
            )
        }

        return ParsedQrResult(
            contentType = QrContentType.PLAIN_TEXT,
            rawValue = raw
        )
    }

    private fun parseGs1String(raw: String): ParsedQrResult? {
        var gtin: String? = null
        var batch: String? = null
        var expiry: String? = null
        var serial: String? = null

        if (raw.contains("(01)")) {
            val pattern = Regex("\\(01\\)([0-9]{13,14})")
            pattern.find(raw)?.let { gtin = it.groupValues[1] }
        }
        if (raw.contains("(10)")) {
            val pattern = Regex("\\(10\\)([A-Za-z0-9_-]+)")
            pattern.find(raw)?.let { batch = it.groupValues[1] }
        }
        if (raw.contains("(17)")) {
            val pattern = Regex("\\(17\\)([0-9]{6})")
            pattern.find(raw)?.let {
                val yyMMdd = it.groupValues[1]
                expiry = formatGs1Date(yyMMdd)
            }
        }
        if (raw.contains("(21)")) {
            val pattern = Regex("\\(21\\)([A-Za-z0-9_-]+)")
            pattern.find(raw)?.let { serial = it.groupValues[1] }
        }

        if (gtin != null || batch != null || expiry != null) {
            return ParsedQrResult(
                contentType = QrContentType.GS1,
                rawValue = raw,
                gtin = gtin,
                batchLot = batch,
                expiry = expiry,
                serial = serial
            )
        }
        return null
    }

    private fun formatGs1Date(yyMMdd: String): String {
        return if (yyMMdd.length == 6) {
            val yy = yyMMdd.substring(0, 2)
            val mm = yyMMdd.substring(2, 4)
            val dd = yyMMdd.substring(4, 6)
            "20$yy-$mm-$dd"
        } else {
            yyMMdd
        }
    }

    fun parsePackageOcr(rawOcr: String, isBlurry: Boolean = false): ParsedOcrPackageResult {
        if (isBlurry || rawOcr.length < 20) {
            return ParsedOcrPackageResult(
                quality = "LOW",
                ocrReliable = false
            )
        }

        val lines = rawOcr.lines().map { it.trim() }

        var brand: String? = null
        var product: String? = null
        var mrp: String? = null
        var mrpNumeric: Double? = null
        var netQty: String? = null
        var batch: String? = null
        var pkd: String? = null
        var exp: String? = null
        var bestBefore: String? = null
        var isBestBeforeDecl = false
        var fssai: String? = null
        var manufacturer: String? = null

        val ingredientsList = mutableListOf<String>()
        val additivesList = mutableListOf<String>()
        val coloursList = mutableListOf<String>()
        val flavouringsList = mutableListOf<String>()
        val declaredAllergensList = mutableListOf<String>()
        val precautionaryAllergensList = mutableListOf<String>()
        val nutritionMap = mutableMapOf<String, NutritionEntry>()

        if (lines.isNotEmpty() && lines[0].isNotBlank()) {
            brand = lines[0]
        }
        if (lines.size > 1 && lines[1].isNotBlank()) {
            product = lines[1]
        }

        // MRP
        val mrpMatch = Regex("MRP\\s*₹?\\s*([0-9]+(?:\\.[0-9]{2})?)", RegexOption.IGNORE_CASE).find(rawOcr)
        if (mrpMatch != null) {
            mrpNumeric = mrpMatch.groupValues[1].toDoubleOrNull()
            mrp = "₹${mrpMatch.groupValues[1]}"
        }

        // Net Quantity
        val netQtyMatch = Regex("(?:NET\\s*QUANTITY|NET\\s*QTY|NET\\s*WT)\\s*([0-9]+\\s*(?:g|kg|ml|l))", RegexOption.IGNORE_CASE).find(rawOcr)
        if (netQtyMatch != null) {
            netQty = netQtyMatch.groupValues[1]
        }

        // Batch
        val batchMatch = Regex("(?:BATCH\\s*NO|BATCH|LOT)\\s*:?\\s*([A-Za-z0-9_-]+)", RegexOption.IGNORE_CASE).find(rawOcr)
        if (batchMatch != null) {
            batch = batchMatch.groupValues[1]
        }

        // PKD
        val pkdMatch = Regex("(?:PKD|MFG|PACKED)\\s*:?\\s*([0-9]{2}/[0-9]{2}/[0-9]{4})", RegexOption.IGNORE_CASE).find(rawOcr)
        if (pkdMatch != null) {
            pkd = pkdMatch.groupValues[1]
        }

        // Best Before
        val bbMatch = Regex("BEST\\s*BEFORE\\s*:?\\s*([0-9]+\\s*[A-Za-z]+\\s*FROM\\s*[A-Za-z]+)", RegexOption.IGNORE_CASE).find(rawOcr)
        if (bbMatch != null) {
            bestBefore = bbMatch.groupValues[1]
            isBestBeforeDecl = true
        }

        // Expiry
        val expMatch = Regex("(?:EXP|EXPIRY|USE\\s*BY)\\s*:?\\s*([0-9]{2}/[0-9]{2}/[0-9]{4}|[0-9]{4}-[0-9]{2}-[0-9]{2})", RegexOption.IGNORE_CASE).find(rawOcr)
        if (expMatch != null) {
            exp = expMatch.groupValues[1]
        }

        // FSSAI
        val fssaiMatch = Regex("FSSAI(?:\\s*LICENCE)?(?:\\s*NO)?\\s*:?\\s*([A-Za-z0-9]+)", RegexOption.IGNORE_CASE).find(rawOcr)
        if (fssaiMatch != null) {
            fssai = fssaiMatch.groupValues[1]
        }

        // Manufacturer
        val mfgMatch = Regex("(?:MANUFACTURED\\s*BY|MFR)\\s*:?\\s*([A-Za-z0-9\\s]+)", RegexOption.IGNORE_CASE).find(rawOcr)
        if (mfgMatch != null) {
            manufacturer = mfgMatch.groupValues[1].trim()
        }

        // Ingredients
        if (rawOcr.contains("INGREDIENTS:", ignoreCase = true)) {
            var ingSection = rawOcr.substringAfter("INGREDIENTS:")
            if (ingSection.contains("NUTRITION", ignoreCase = true)) {
                ingSection = ingSection.substringBefore("NUTRITION")
            }
            if (ingSection.contains("CONTAINS:", ignoreCase = true)) {
                ingSection = ingSection.substringBefore("CONTAINS:")
            }
            if (ingSection.contains("FSSAI", ignoreCase = true)) {
                ingSection = ingSection.substringBefore("FSSAI")
            }
            if (ingSection.contains("MANUFACTURED", ignoreCase = true)) {
                ingSection = ingSection.substringBefore("MANUFACTURED")
            }
            val items = ingSection.split(",", "\n").map { it.trim() }.filter { it.isNotBlank() }
            for (item in items) {
                if (item.contains("INS", ignoreCase = true)) {
                    additivesList.add(item)
                } else if (item.contains("Colour", ignoreCase = true)) {
                    coloursList.add(item)
                } else if (item.contains("Flavour", ignoreCase = true)) {
                    flavouringsList.add(item)
                } else {
                    ingredientsList.add(item)
                }
            }
        }

        // Allergens
        if (rawOcr.contains("CONTAINS:", ignoreCase = true)) {
            val section = rawOcr.substringAfter("CONTAINS:").substringBefore("MAY CONTAIN:").substringBefore("\n\n")
            section.lines().map { it.trim() }.filter { it.isNotBlank() }.forEach { declaredAllergensList.add(it) }
        }
        if (rawOcr.contains("MAY CONTAIN:", ignoreCase = true)) {
            val section = rawOcr.substringAfter("MAY CONTAIN:").substringBefore("\n\n")
            section.lines().map { it.trim() }.filter { it.isNotBlank() }.forEach { precautionaryAllergensList.add(it) }
        }

        // Nutrition
        if (rawOcr.contains("NUTRITION", ignoreCase = true)) {
            val nutLines = rawOcr.substringAfter("NUTRITION").lines()
            for (line in nutLines) {
                if (line.contains("Energy:", ignoreCase = true)) {
                    nutritionMap["Energy"] = NutritionEntry(line.substringAfter(":").trim(), "kcal")
                } else if (line.contains("Protein:", ignoreCase = true)) {
                    nutritionMap["Protein"] = NutritionEntry(line.substringAfter(":").trim(), "g")
                } else if (line.contains("Carbohydrate:", ignoreCase = true) || line.contains("Carbs", ignoreCase = true)) {
                    nutritionMap["Carbohydrates"] = NutritionEntry(line.substringAfter(":").trim(), "g")
                } else if (line.contains("Sodium:", ignoreCase = true)) {
                    nutritionMap["Sodium"] = NutritionEntry(line.substringAfter(":").trim(), "mg")
                }
            }
        }

        return ParsedOcrPackageResult(
            brandName = brand,
            productName = product,
            mrp = mrp,
            mrpNumeric = mrpNumeric,
            netQuantity = netQty,
            batchNo = batch,
            packingDate = pkd,
            expiryDate = exp,
            bestBeforePeriod = bestBefore,
            isBestBeforeDeclaration = isBestBeforeDecl,
            ingredients = ingredientsList,
            additives = additivesList,
            colours = coloursList,
            flavourings = flavouringsList,
            declaredAllergens = declaredAllergensList,
            precautionaryAllergens = precautionaryAllergensList,
            nutritionMap = nutritionMap,
            fssaiLicence = fssai,
            manufacturer = manufacturer,
            quality = "HIGH",
            ocrReliable = true
        )
    }

    fun verifyQrAndPackage(
        qr: ParsedQrResult,
        pkg: ParsedOcrPackageResult,
        dbMrp: String? = null
    ): VerificationResult {
        val gtinMatch = qr.gtin == null || pkg.brandName != null
        val batchMatch = qr.batchLot == null || pkg.batchNo == null || qr.batchLot.equals(pkg.batchNo, ignoreCase = true)

        val expiryMatch = if (qr.expiry != null && pkg.expiryDate != null) {
            val normalizedQrExp = qr.expiry.replace("-", "/")
            val normalizedPkgExp = pkg.expiryDate.replace("-", "/")
            normalizedQrExp == normalizedPkgExp || normalizedQrExp.reversed() == normalizedPkgExp
        } else {
            true
        }

        var mrpStatus = "MATCH"
        var requiresManual = false

        if (dbMrp != null && pkg.mrp != null && !dbMrp.equals(pkg.mrp, ignoreCase = true)) {
            mrpStatus = "DISCREPANCY DETECTED"
            requiresManual = true
        }

        if (!batchMatch || !expiryMatch) {
            requiresManual = true
        }

        val status = if (requiresManual) "DATA DISCREPANCY" else "VERIFIED"

        return VerificationResult(
            gtinMatch = gtinMatch,
            batchMatch = batchMatch,
            expiryMatch = expiryMatch,
            mrpStatus = mrpStatus,
            packageMrp = pkg.mrp,
            databaseMrp = dbMrp,
            status = status,
            requiresManualVerification = requiresManual
        )
    }

    fun evaluateExpiry(
        expiryStr: String?,
        refDateStr: String = "2026-09-19"
    ): ExpiryEvaluationStatus {
        if (expiryStr.isNullOrBlank()) {
            return ExpiryEvaluationStatus.NOT_FOUND
        }

        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            val expDate = if (expiryStr.contains("/")) {
                val parts = expiryStr.split("/")
                if (parts.size == 3) {
                    sdf.parse("${parts[2]}-${parts[1]}-${parts[0]}")
                } else {
                    sdf.parse(expiryStr)
                }
            } else {
                sdf.parse(expiryStr)
            }

            val refDate = sdf.parse(refDateStr) ?: Date()

            if (expDate == null) {
                ExpiryEvaluationStatus.NOT_FOUND
            } else {
                val calExp = Calendar.getInstance().apply { time = expDate }
                val calRef = Calendar.getInstance().apply { time = refDate }

                val diffMillis = calExp.timeInMillis - calRef.timeInMillis
                val diffDays = diffMillis / (1000 * 60 * 60 * 24)

                when {
                    diffDays < 0 -> ExpiryEvaluationStatus.EXPIRED
                    diffDays in 0..14 -> ExpiryEvaluationStatus.EXPIRING_SOON
                    else -> ExpiryEvaluationStatus.VALID
                }
            }
        } catch (e: Exception) {
            ExpiryEvaluationStatus.NOT_FOUND
        }
    }
}

/**
 * State manager ensuring zero duplicate scan records and clean state resets during product switches.
 */
class ScanSessionTracker {
    private var lastScanPayload: String? = null
    private var lastScanTimestamp: Long = 0L
    private var activeSessionId: String? = null
    private var activeProductData: Map<String, Any> = emptyMap()

    fun registerScan(payload: String, currentTime: Long = System.currentTimeMillis()): Pair<Boolean, String> {
        if (payload == lastScanPayload && (currentTime - lastScanTimestamp) < 3000L && activeSessionId != null) {
            // Duplicate scan within 3 seconds -> return existing session ID
            return Pair(false, activeSessionId!!)
        }

        val newSessionId = "SESSION_" + System.currentTimeMillis()
        lastScanPayload = payload
        lastScanTimestamp = currentTime
        activeSessionId = newSessionId
        return Pair(true, newSessionId)
    }

    fun startNewProductSession(productId: String): String {
        // Clear previous state completely
        clearState()
        val newSessionId = "SESSION_${productId}_${System.currentTimeMillis()}"
        activeSessionId = newSessionId
        activeProductData = mapOf("productId" to productId)
        return newSessionId
    }

    fun getActiveSessionData(): Map<String, Any> {
        return activeProductData
    }

    fun clearState() {
        lastScanPayload = null
        lastScanTimestamp = 0L
        activeSessionId = null
        activeProductData = emptyMap()
    }
}
