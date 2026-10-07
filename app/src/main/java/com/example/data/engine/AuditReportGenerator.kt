package com.example.data.engine

import com.example.data.model.GlobalComplianceResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generates Auditable Statutory Compliance Reports in JSON and formatted printable document styles.
 */
object AuditReportGenerator {

    fun generateJsonReport(result: GlobalComplianceResult): String {
        val violationsJson = result.violations.joinToString(separator = ",\n      ") { v ->
            """{
        "rule_id": "${v.ruleId}",
        "rule_name": "${v.ruleName}",
        "detected_value": "${v.detectedValue}",
        "required_value": "${v.requiredValue}",
        "evidence": "${v.evidence}",
        "reason": "${v.reason}",
        "severity": "${v.severity}",
        "penalty_clause": "${v.penaltyClause}"
      }"""
        }

        val warningsJson = result.warnings.joinToString(separator = "\", \"", prefix = "[\"", postfix = "\"]")
        val notVerifiableJson = result.notVerifiableFields.joinToString(separator = "\", \"", prefix = "[\"", postfix = "\"]")

        return """{
  "audit_report": {
    "report_version": "5.0",
    "scan_id": "${result.scanId}",
    "timestamp": "${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH).format(Date(result.timestamp))}",
    "ruleset_id": "${result.rulesetId}",
    "compliance_status": "${result.overallStatus}",
    "confidence_score": ${result.confidenceScore},
    "capture_mode": "${result.captureMode}",
    "sync_status": "${result.syncStatus}",
    "product": {
      "name": "${result.productName}",
      "brand": "${result.brand}",
      "manufacturer": "${result.manufacturer}",
      "importer": "${result.importer}",
      "mrp": {
        "value": ${result.mrp.value},
        "currency": "${result.mrp.currency}",
        "inclusive_of_taxes": ${result.mrp.inclusiveOfTaxes}
      },
      "net_quantity": {
        "value": ${result.netQuantity.value},
        "unit": "${result.netQuantity.unit}"
      },
      "country_of_origin": "${result.countryOfOrigin}",
      "mfg_date": "${result.manufacturingDate}",
      "expiry_date": "${result.expiryDate}",
      "remaining_days": ${result.remainingDays ?: "null"},
      "is_expired": ${result.isExpired},
      "batch_number": "${result.batchNumber}",
      "barcode": "${result.barcode}",
      "font_height_mm": ${result.fontHeightMm},
      "font_height_required_mm": ${result.fontHeightRequiredMm}
    },
    "violations": [
      $violationsJson
    ],
    "warnings": $warningsJson,
    "not_verifiable_fields": $notVerifiableJson
  }
}"""
    }

    fun generateTextSummaryReport(
        result: GlobalComplianceResult,
        inspectorName: String = "Legal Metrology Officer",
        inspectorBadge: String = "LMO-DL-2026-0842",
        location: String = "Central Verification Unit"
    ): String {
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date(result.timestamp))
        val sb = StringBuilder()
        sb.append("========================================================\n")
        sb.append("        OFFICIAL PACKAGING COMPLIANCE AUDIT REPORT      \n")
        sb.append("                 MUDRA CHECK GLOBAL AI                  \n")
        sb.append("========================================================\n\n")
        sb.append("REPORT METADATA:\n")
        sb.append("• Scan ID: ${result.scanId}\n")
        sb.append("• Date & Time: $dateStr\n")
        sb.append("• Regulatory Ruleset: ${result.rulesetId}\n")
        sb.append("• Officer: $inspectorName ($inspectorBadge)\n")
        sb.append("• Jurisdiction: $location\n")
        sb.append("• Capture Mode: ${result.captureMode} | Sync Status: ${result.syncStatus}\n\n")

        sb.append("VERDICT SUMMARY:\n")
        sb.append("• Statutory Status: ${result.overallStatus}\n")
        sb.append("• Confidence Score: ${result.confidenceScore} / 100\n")
        sb.append("• Barcode / QR: ${if (result.barcode.isNotBlank()) result.barcode else "N/A"}\n\n")

        sb.append("EXTRACTED PRODUCT DECLARATIONS:\n")
        sb.append("• Product Name: ${result.productName}\n")
        sb.append("• Brand: ${result.brand}\n")
        sb.append("• Declared MRP: ${result.mrp.currency} ${result.mrp.value} (${if (result.mrp.inclusiveOfTaxes) "Incl. of all taxes" else "Taxes not verified"})\n")
        sb.append("• Net Quantity: ${result.netQuantity.value} ${result.netQuantity.unit}\n")
        sb.append("• Country of Origin: ${result.countryOfOrigin}\n")
        sb.append("• Mfg / Packing Date: ${result.manufacturingDate}\n")
        sb.append("• Expiry / Durability: ${result.expiryDate} (${if (result.isExpired) "EXPIRED" else "${result.remainingDays ?: "N/A"} days remaining"})\n")
        sb.append("• Font Height: Detected ${result.fontHeightMm}mm vs Required ${result.fontHeightRequiredMm}mm\n")
        sb.append("• Manufacturer: ${result.manufacturer}\n\n")

        if (result.violations.isNotEmpty()) {
            sb.append("CONTRAVENTIONS & VIOLATIONS FOUND (${result.violations.size}):\n")
            result.violations.forEachIndexed { idx, v ->
                sb.append("${idx + 1}. [${v.ruleId}] ${v.ruleName} (${v.severity})\n")
                sb.append("   - Detected: ${v.detectedValue}\n")
                sb.append("   - Statutory Threshold: ${v.requiredValue}\n")
                sb.append("   - Evidence: ${v.evidence}\n")
                sb.append("   - Penalty: ${v.penaltyClause}\n\n")
            }
        } else {
            sb.append("CONTRAVENTIONS: None detected. All verified declarations comply with ${result.rulesetId}.\n\n")
        }

        if (result.warnings.isNotEmpty()) {
            sb.append("WARNINGS & DISCREPANCIES:\n")
            result.warnings.forEach { sb.append("• $it\n") }
            sb.append("\n")
        }

        sb.append("========================================================\n")
        sb.append("End of Official Audit Report • Generated via Mudra Check\n")
        sb.append("========================================================\n")

        return sb.toString()
    }
}
