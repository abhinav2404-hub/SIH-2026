package com.example

import com.example.data.scanner.ExpiryEvaluationStatus
import com.example.data.scanner.QrContentType
import com.example.data.scanner.QrFoodScannerEngine
import com.example.data.scanner.ScanSessionTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class QrFoodScannerTest {

    private fun readResource(path: String): String {
        val stream = javaClass.classLoader?.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Test resource not found at path: $path")
        return stream.bufferedReader().use { it.readText() }
    }

    // --- FIXTURE 1: Basic URL QR ---
    @Test
    fun testFixture01_BasicUrlQr() {
        val payload = readResource("qr/qr_url_basic.txt").trim()
        val result = QrFoodScannerEngine.parseQrPayload(payload)

        assertEquals("QR_CODE", result.format)
        assertEquals(QrContentType.URL, result.contentType)
        assertEquals("https://example.com/product/food-1001", result.url)
        assertNull(result.productId)
        assertTrue(result.isHttps)
    }

    // --- FIXTURE 2: Plain Text QR ---
    @Test
    fun testFixture02_PlainTextQr() {
        val payload = readResource("qr/qr_plain_text.txt").trim()
        val result = QrFoodScannerEngine.parseQrPayload(payload)

        assertEquals(QrContentType.PLAIN_TEXT, result.contentType)
        assertEquals("MUDRA-FOOD-PRODUCT-1002", result.rawValue)
        assertNull(result.url)
    }

    // --- FIXTURE 3: Product ID QR ---
    @Test
    fun testFixture03_ProductIdQr() {
        val payload = readResource("qr/qr_product_id.txt").trim()
        val result = QrFoodScannerEngine.parseQrPayload(payload)

        assertEquals(QrContentType.PRODUCT_ID, result.contentType)
        assertEquals("MUDRA1003", result.productId)
    }

    // --- FIXTURE 4: GTIN QR ---
    @Test
    fun testFixture04_GtinQr() {
        val payload = readResource("qr/qr_gtin.txt").trim()
        val result = QrFoodScannerEngine.parseQrPayload(payload)

        assertEquals(QrContentType.GTIN, result.contentType)
        assertEquals("8901234567890", result.gtin)
    }

    // --- FIXTURE 5: JSON QR ---
    @Test
    fun testFixture05_JsonQr() {
        val payload = readResource("qr/qr_product_json.json")
        val result = QrFoodScannerEngine.parseQrPayload(payload)

        assertEquals(QrContentType.JSON, result.contentType)
        assertEquals("MUDRA1005", result.productId)
        assertEquals("8901234567890", result.gtin)
        assertEquals("BATCH-A100", result.batchLot)
        assertEquals("2026-12-28", result.expiry)
    }

    // --- FIXTURE 6: GS1-Style QR ---
    @Test
    fun testFixture06_Gs1FoodQr() {
        val payload = readResource("qr/qr_gs1_food.txt").trim()
        val result = QrFoodScannerEngine.parseQrPayload(payload)

        assertEquals(QrContentType.GS1, result.contentType)
        assertEquals("08901234567890", result.gtin)
        assertEquals("TEST-BATCH-01", result.batchLot)
        assertEquals("2026-12-28", result.expiry)
        assertEquals("TEST-SERIAL-001", result.serial)
    }

    // --- FIXTURE 7: Unknown QR ---
    @Test
    fun testFixture07_UnknownQr() {
        val payload = readResource("qr/qr_unknown.txt").trim()
        val result = QrFoodScannerEngine.parseQrPayload(payload)

        assertEquals(QrContentType.UNKNOWN, result.contentType)
        assertNull(result.productId)
    }

    // --- FIXTURE 8: Invalid / Malformed QR Payload ---
    @Test
    fun testFixture08_InvalidQrPayload() {
        val payload = readResource("qr/qr_invalid.txt").trim()
        val result = QrFoodScannerEngine.parseQrPayload(payload)

        assertEquals(QrContentType.PLAIN_TEXT, result.contentType)
        assertNotNull(result.rawValue)
    }

    // --- FIXTURE 11: Valid Package OCR Extraction ---
    @Test
    fun testFixture11_PackageOcrValid() {
        val rawOcr = readResource("package/package_ocr_valid.txt")
        val parsed = QrFoodScannerEngine.parsePackageOcr(rawOcr)

        assertEquals("₹20.00", parsed.mrp)
        assertEquals("100 g", parsed.netQuantity)
        assertEquals("BATCH-A100", parsed.batchNo)
        assertEquals("30/08/2026", parsed.packingDate)
        assertEquals("TEST1234567890", parsed.fssaiLicence)
        assertTrue(parsed.ingredients.isNotEmpty())
    }

    // --- FIXTURE 12: Missing Data Package OCR ---
    @Test
    fun testFixture12_PackageOcrMissingFields() {
        val rawOcr = readResource("package/package_ocr_missing_fields.txt")
        val parsed = QrFoodScannerEngine.parsePackageOcr(rawOcr)

        assertEquals("₹50", parsed.mrp)
        assertEquals("80 g", parsed.netQuantity)
        assertNull(parsed.expiryDate)
        assertNull(parsed.fssaiLicence)
        assertTrue(parsed.nutritionMap.isEmpty())
    }

    // --- FIXTURE 13: QR and Package Match ---
    @Test
    fun testFixture13_QrPackageMatch() {
        val qr = QrFoodScannerEngine.parseQrPayload(readResource("qr/qr_product_json.json"))
        val pkg = QrFoodScannerEngine.parsePackageOcr(readResource("package/package_ocr_valid.txt"))

        val verification = QrFoodScannerEngine.verifyQrAndPackage(qr, pkg)

        assertTrue(verification.gtinMatch)
        assertTrue(verification.batchMatch)
        assertEquals("VERIFIED", verification.status)
    }

    // --- FIXTURE 14: QR and Package Mismatch ---
    @Test
    fun testFixture14_QrPackageMismatch() {
        val qr = QrFoodScannerEngine.parseQrPayload(readResource("qr/qr_product_json.json"))
        val pkg = QrFoodScannerEngine.parsePackageOcr("TEST BRAND\nTEST BISCUITS\nBATCH NO: BATCH-B999\nEXP: 30/12/2026")

        val verification = QrFoodScannerEngine.verifyQrAndPackage(qr, pkg)

        assertFalse(verification.batchMatch)
        assertEquals("DATA DISCREPANCY", verification.status)
        assertTrue(verification.requiresManualVerification)
    }

    // --- FIXTURE 15: MRP Conflict ---
    @Test
    fun testFixture15_MrpConflict() {
        val qr = QrFoodScannerEngine.parseQrPayload(readResource("qr/qr_product_json.json"))
        val pkg = QrFoodScannerEngine.parsePackageOcr("TEST BRAND\nMRP ₹20\nNET QUANTITY 100 g")

        val verification = QrFoodScannerEngine.verifyQrAndPackage(qr, pkg, dbMrp = "₹25")

        assertEquals("DISCREPANCY DETECTED", verification.mrpStatus)
        assertEquals("₹20", verification.packageMrp)
        assertEquals("₹25", verification.databaseMrp)
    }

    // --- FIXTURE 16: Expiry Evaluation Test Set ---
    @Test
    fun testFixture16_ExpiryTestSet() {
        assertEquals(ExpiryEvaluationStatus.VALID, QrFoodScannerEngine.evaluateExpiry("2028-12-31"))
        assertEquals(ExpiryEvaluationStatus.EXPIRED, QrFoodScannerEngine.evaluateExpiry("2020-01-01"))
        assertEquals(ExpiryEvaluationStatus.NOT_FOUND, QrFoodScannerEngine.evaluateExpiry(null))

        val bbPkg = QrFoodScannerEngine.parsePackageOcr(readResource("package/package_ocr_valid.txt"))
        assertTrue(bbPkg.isBestBeforeDeclaration)
        assertEquals("4 MONTHS FROM PACKAGING", bbPkg.bestBeforePeriod)
    }

    // --- FIXTURE 18: Allergen Analysis ---
    @Test
    fun testFixture18_AllergenAnalysis() {
        val text = readResource("package/allergen_fixture.txt")
        val pkg = QrFoodScannerEngine.parsePackageOcr(text)

        assertTrue(pkg.declaredAllergens.any { it.equals("WHEAT", ignoreCase = true) })
        assertTrue(pkg.precautionaryAllergens.any { it.equals("PEANUTS", ignoreCase = true) })
    }

    // --- FIXTURE 20: Blurry Package Handling ---
    @Test
    fun testFixture20_BlurryPackage() {
        val pkg = QrFoodScannerEngine.parsePackageOcr("BLURRY TEXT TOO SHORT", isBlurry = true)

        assertEquals("LOW", pkg.quality)
        assertFalse(pkg.ocrReliable)
    }

    // --- FIXTURE 22: Duplicate Scan Session Deduplication ---
    @Test
    fun testFixture22_DuplicateScanDeduplication() {
        val tracker = ScanSessionTracker()
        val payload = "PRODUCT_ID:MUDRA1003"

        val (isNew1, sessionId1) = tracker.registerScan(payload, currentTime = 1000L)
        val (isNew2, sessionId2) = tracker.registerScan(payload, currentTime = 1500L)

        assertTrue(isNew1)
        assertFalse(isNew2)
        assertEquals(sessionId1, sessionId2)
    }

    // --- FIXTURE 23: Product Switch Isolation (Zero Stale Data) ---
    @Test
    fun testFixture23_ProductSwitchIsolation() {
        val tracker = ScanSessionTracker()

        tracker.startNewProductSession("PRODUCT_A")
        assertEquals("PRODUCT_A", tracker.getActiveSessionData()["productId"])

        tracker.startNewProductSession("PRODUCT_B")
        assertEquals("PRODUCT_B", tracker.getActiveSessionData()["productId"])
        assertFalse(tracker.getActiveSessionData().containsValue("PRODUCT_A"))
    }

    // --- FIXTURE 27: URL Safety Scheme Checks ---
    @Test
    fun testFixture27_UrlSafety() {
        val httpsResult = QrFoodScannerEngine.parseQrPayload("https://example.com/product/1")
        val httpResult = QrFoodScannerEngine.parseQrPayload("http://example.com/product/2")
        val jsResult = QrFoodScannerEngine.parseQrPayload("javascript:alert(1)")

        assertTrue(httpsResult.isHttps)
        assertTrue(httpResult.isHttpWarning)
        assertTrue(jsResult.isRejectedScheme)
    }
}
