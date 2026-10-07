package com.example.data.engine

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class DateValidationResult(
    val isValid: Boolean,
    val isExpired: Boolean,
    val remainingDays: Int?,
    val mfgDateFormatted: String?,
    val expiryDateFormatted: String?,
    val parsedExpiryDate: Date?,
    val isAmbiguous: Boolean,
    val explanation: String
)

/**
 * Programmatic Date Validation and Expiry Duration Engine.
 * Never guesses dates—strictly validates formats (DD/MM/YYYY, DD-MM-YYYY, MM/YYYY, YYYY-MM-DD, Best before X months)
 * and calculates remaining shelf life against current system date.
 */
object DateValidationEngine {

    private val DATE_PATTERNS = listOf(
        SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH),
        SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH),
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
        SimpleDateFormat("MM/yyyy", Locale.ENGLISH),
        SimpleDateFormat("MM-yyyy", Locale.ENGLISH),
        SimpleDateFormat("MMM yyyy", Locale.ENGLISH),
        SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    )

    fun validateDates(
        mfgDateText: String,
        expiryDateText: String,
        bestBeforePeriodText: String = "",
        currentDate: Date = Date()
    ): DateValidationResult {
        val trimmedExpiry = expiryDateText.trim()
        val trimmedMfg = mfgDateText.trim()

        if (trimmedExpiry.isBlank() && bestBeforePeriodText.isBlank()) {
            return DateValidationResult(
                isValid = false,
                isExpired = false,
                remainingDays = null,
                mfgDateFormatted = if (trimmedMfg.isNotBlank()) trimmedMfg else null,
                expiryDateFormatted = null,
                parsedExpiryDate = null,
                isAmbiguous = true,
                explanation = "No expiry or best before declaration detected."
            )
        }

        var parsedExpiry: Date? = parseDate(trimmedExpiry)
        var isComputedFromBestBefore = false

        // If direct expiry parse failed, check if best before is declared (e.g., "Best before 9 months from mfg")
        if (parsedExpiry == null && bestBeforePeriodText.isNotBlank()) {
            val parsedMfg = parseDate(trimmedMfg)
            val monthsMatch = Pattern.compile("(\\d+)\\s*(?:months|month|m)", Pattern.CASE_INSENSITIVE).matcher(bestBeforePeriodText)
            val daysMatch = Pattern.compile("(\\d+)\\s*(?:days|day|d)", Pattern.CASE_INSENSITIVE).matcher(bestBeforePeriodText)

            if (parsedMfg != null) {
                val cal = Calendar.getInstance()
                cal.time = parsedMfg
                if (monthsMatch.find()) {
                    val months = monthsMatch.group(1)?.toIntOrNull() ?: 0
                    cal.add(Calendar.MONTH, months)
                    parsedExpiry = cal.time
                    isComputedFromBestBefore = true
                } else if (daysMatch.find()) {
                    val days = daysMatch.group(1)?.toIntOrNull() ?: 0
                    cal.add(Calendar.DAY_OF_YEAR, days)
                    parsedExpiry = cal.time
                    isComputedFromBestBefore = true
                }
            }
        }

        if (parsedExpiry == null) {
            return DateValidationResult(
                isValid = false,
                isExpired = false,
                remainingDays = null,
                mfgDateFormatted = trimmedMfg,
                expiryDateFormatted = trimmedExpiry,
                parsedExpiryDate = null,
                isAmbiguous = true,
                explanation = "Expiry date format could not be verified reliably."
            )
        }

        // Logical check: Expiry before Mfg date
        val parsedMfg = parseDate(trimmedMfg)
        if (parsedMfg != null && parsedExpiry.before(parsedMfg)) {
            return DateValidationResult(
                isValid = false,
                isExpired = true,
                remainingDays = -1,
                mfgDateFormatted = trimmedMfg,
                expiryDateFormatted = trimmedExpiry,
                parsedExpiryDate = parsedExpiry,
                isAmbiguous = false,
                explanation = "Logical contradiction: Expiry date ($trimmedExpiry) precedes Manufacturing date ($trimmedMfg)."
            )
        }

        // Calculate remaining days
        val diffMillis = parsedExpiry.time - currentDate.time
        val remainingDays = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
        val isExpired = diffMillis < 0

        val displayExpiry = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(parsedExpiry)
        val displayMfg = parsedMfg?.let { SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(it) } ?: trimmedMfg

        val explanation = when {
            isExpired -> "Product expired ${Math.abs(remainingDays)} days ago on $displayExpiry."
            remainingDays in 0..15 -> "Urgent: Product expires in $remainingDays days ($displayExpiry)."
            else -> "Valid product. $remainingDays days remaining before expiry ($displayExpiry)."
        }

        return DateValidationResult(
            isValid = true,
            isExpired = isExpired,
            remainingDays = remainingDays,
            mfgDateFormatted = displayMfg,
            expiryDateFormatted = if (isComputedFromBestBefore) "$displayExpiry (Computed from Best Before)" else trimmedExpiry,
            parsedExpiryDate = parsedExpiry,
            isAmbiguous = false,
            explanation = explanation
        )
    }

    private fun parseDate(text: String): Date? {
        if (text.isBlank()) return null
        val clean = text.replace(",", "").replace(".", "/").trim()
        for (format in DATE_PATTERNS) {
            try {
                format.isLenient = false
                val date = format.parse(clean)
                if (date != null) return date
            } catch (_: Exception) {
            }
        }
        return null
    }
}
