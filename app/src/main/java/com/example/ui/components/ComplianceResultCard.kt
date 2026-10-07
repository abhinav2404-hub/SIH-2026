package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComplianceCheck
import com.example.data.model.ComplianceStatus

enum class ComplianceStatusType {
    PASS,
    WARNING,
    FAIL,
    NOT_VERIFIABLE;

    companion object {
        fun fromString(status: String): ComplianceStatusType {
            return when (status.uppercase().trim()) {
                "PASS", "COMPLIANT", "SUCCESS", "VALID" -> PASS
                "WARNING", "REVIEW", "MINOR_VIOLATIONS", "ADVISORY" -> WARNING
                "FAIL", "NON_COMPLIANT", "CRITICAL", "SEIZURE_RECOMMENDED", "VIOLATION" -> FAIL
                "NOT_VERIFIABLE", "UNVERIFIABLE", "UNCERTAIN", "OBSCURED", "INCONCLUSIVE" -> NOT_VERIFIABLE
                else -> NOT_VERIFIABLE
            }
        }
    }
}

/**
 * Reusable Compliance Result Card taking a [ComplianceCheck] domain model.
 */
@Composable
fun ComplianceResultCard(
    check: ComplianceCheck,
    modifier: Modifier = Modifier,
    onEvidenceClick: (() -> Unit)? = null
) {
    ComplianceResultCard(
        ruleId = check.ruleId.ifBlank { "RULE" },
        ruleName = check.ruleName,
        status = when (check.status) {
            ComplianceStatus.PASS -> ComplianceStatusType.PASS
            ComplianceStatus.WARNING -> ComplianceStatusType.WARNING
            ComplianceStatus.FAIL -> ComplianceStatusType.FAIL
            ComplianceStatus.NOT_VERIFIABLE -> ComplianceStatusType.NOT_VERIFIABLE
        },
        detectedValue = check.detectedValue,
        requiredValue = check.requiredValue,
        evidenceSnippet = check.evidenceSnippet,
        reason = check.reasoning,
        penaltyClause = check.penaltyClause,
        statutoryRef = check.statutoryRef,
        modifier = modifier,
        onEvidenceClick = onEvidenceClick
    )
}

/**
 * Reusable Compliance Result Card displaying Pass / Warning / Fail / Not-Verifiable status
 * with distinct 🟢 🟠 🔴 ⚪ color coding, statutory rule citations, value comparisons, OCR evidence,
 * and expandable penalty provisions.
 */
@Composable
fun ComplianceResultCard(
    ruleId: String,
    ruleName: String,
    status: ComplianceStatusType,
    detectedValue: String,
    requiredValue: String,
    evidenceSnippet: String,
    reason: String,
    penaltyClause: String? = null,
    statutoryRef: String? = null,
    modifier: Modifier = Modifier,
    onEvidenceClick: (() -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    val statusConfig = when (status) {
        ComplianceStatusType.PASS -> StatusVisualConfig(
            emoji = "🟢",
            label = "LEGAL (PASS)",
            accentColor = Color(0xFF2E7D32),
            containerColor = Color(0xFFF1F8E9),
            borderColor = Color(0xFFA5D6A7),
            icon = Icons.Default.CheckCircle,
            contentDesc = "Rule $ruleId passed: $ruleName legal"
        )
        ComplianceStatusType.WARNING -> StatusVisualConfig(
            emoji = "🟠",
            label = "WARNING (REVIEW)",
            accentColor = Color(0xFFE65100),
            containerColor = Color(0xFFFFF3E0),
            borderColor = Color(0xFFFFCC80),
            icon = Icons.Default.Warning,
            contentDesc = "Rule $ruleId advisory warning: $ruleName needs review"
        )
        ComplianceStatusType.FAIL -> StatusVisualConfig(
            emoji = "🔴",
            label = "ILLEGAL (FAIL)",
            accentColor = Color(0xFFC62828),
            containerColor = Color(0xFFFFEBEE),
            borderColor = Color(0xFFEF9A9A),
            icon = Icons.Default.ReportProblem,
            contentDesc = "Rule $ruleId failed: $ruleName illegal violation detected"
        )
        ComplianceStatusType.NOT_VERIFIABLE -> StatusVisualConfig(
            emoji = "⚪",
            label = "UNCLEAR (RETRY)",
            accentColor = Color(0xFF546E7A),
            containerColor = Color(0xFFECEFF1),
            borderColor = Color(0xFFCFD8DC),
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            contentDesc = "Rule $ruleId not verifiable: $ruleName obscured or unreadable"
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("compliance_card_$ruleId")
            .semantics { contentDescription = statusConfig.contentDesc },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, statusConfig.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Rule Tag + Name & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(statusConfig.accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = statusConfig.icon,
                            contentDescription = null,
                            tint = statusConfig.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = ruleId,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (statutoryRef != null) {
                                Text(
                                    text = statutoryRef,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                        Text(
                            text = ruleName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusConfig.containerColor,
                    border = BorderStroke(1.dp, statusConfig.borderColor),
                    modifier = Modifier.testTag("verdict_status_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusConfig.accentColor)
                        )
                        Text(
                            text = "${statusConfig.emoji} ${statusConfig.label}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = statusConfig.accentColor
                        )
                    }
                }
            }

            // Reason / Explanation
            if (reason.isNotBlank()) {
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Comparison Boxes: Detected vs Required
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Detected Value Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = if (status == ComplianceStatusType.FAIL) Color(0xFFFFEBEE).copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(
                        0.8.dp,
                        if (status == ComplianceStatusType.FAIL) Color(0xFFEF9A9A) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "PRINTED ON PACKET",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = detectedValue.ifBlank { "Not Found on Label" },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            ),
                            color = if (status == ComplianceStatusType.FAIL) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Required Standard Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "WHAT LAW REQUIRES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = requiredValue.ifBlank { "Mandatory Declaration" },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Supporting Evidence / OCR Snippet Box
            if (evidenceSnippet.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("evidence_box")
                        .clickable(enabled = onEvidenceClick != null) { onEvidenceClick?.invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "OCR Evidence Snippet",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Extracted Evidence:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "\"$evidenceSnippet\"",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Expandable Penalty & Legal References
            if (!penaltyClause.isNullOrBlank()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isExpanded) "Hide Statutory Penalty" else "View Statutory Penalty Clause",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF8E1),
                        border = BorderStroke(1.dp, Color(0xFFFFE082)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .testTag("penalty_clause")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Statutory Legal Provision:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF57F17)
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = penaltyClause,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class StatusVisualConfig(
    val emoji: String,
    val label: String,
    val accentColor: Color,
    val containerColor: Color,
    val borderColor: Color,
    val icon: ImageVector,
    val contentDesc: String
)
