package com.example.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration strategies for Room database schema version upgrades.
 */
object DatabaseMigrations {

    /**
     * Migration from Version 6 to Version 7 / 8:
     * Adds 'products', 'scans', and 'violations' tables with foreign key constraints and indices.
     */
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create products table
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `products` (
                    `productId` TEXT NOT NULL,
                    `barcode` TEXT NOT NULL,
                    `brand` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `category` TEXT NOT NULL,
                    `manufacturer` TEXT NOT NULL,
                    `countryOfOrigin` TEXT NOT NULL,
                    `consumerCare` TEXT NOT NULL,
                    `netQuantityDeclared` TEXT NOT NULL,
                    `mrpDeclared` REAL NOT NULL,
                    `currency` TEXT NOT NULL,
                    `ingredientsText` TEXT NOT NULL,
                    `allergensText` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    PRIMARY KEY(`productId`)
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_products_barcode` ON `products` (`barcode`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_products_name` ON `products` (`name`)")

            // Create scans table
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `scans` (
                    `scanId` TEXT NOT NULL,
                    `productId` TEXT,
                    `timestamp` INTEGER NOT NULL,
                    `rulesetId` TEXT NOT NULL,
                    `overallStatus` TEXT NOT NULL,
                    `complianceScore` INTEGER NOT NULL,
                    `rawOcrText` TEXT NOT NULL,
                    `imageUri` TEXT NOT NULL,
                    `inspectorBadge` TEXT NOT NULL,
                    `inspectorNotes` TEXT NOT NULL,
                    `syncStatus` TEXT NOT NULL,
                    `latencyMs` INTEGER NOT NULL,
                    `violationsCount` INTEGER NOT NULL,
                    PRIMARY KEY(`scanId`),
                    FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE SET_NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_scans_productId` ON `scans` (`productId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_scans_timestamp` ON `scans` (`timestamp`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_scans_syncStatus` ON `scans` (`syncStatus`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_scans_overallStatus` ON `scans` (`overallStatus`)")

            // Create violations table
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `violations` (
                    `violationId` TEXT NOT NULL,
                    `scanId` TEXT NOT NULL,
                    `ruleId` TEXT NOT NULL,
                    `ruleName` TEXT NOT NULL,
                    `severity` TEXT NOT NULL,
                    `detectedValue` TEXT NOT NULL,
                    `requiredValue` TEXT NOT NULL,
                    `evidenceSnippet` TEXT NOT NULL,
                    `reason` TEXT NOT NULL,
                    `penaltyClause` TEXT NOT NULL,
                    `status` TEXT NOT NULL,
                    PRIMARY KEY(`violationId`),
                    FOREIGN KEY(`scanId`) REFERENCES `scans`(`scanId`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_violations_scanId` ON `violations` (`scanId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_violations_ruleId` ON `violations` (`ruleId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_violations_severity` ON `violations` (`severity`)")
        }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Index optimizations and table checks
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_products_name` ON `products` (`name`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_scans_overallStatus` ON `scans` (`overallStatus`)")
        }
    }

    val ALL_MIGRATIONS = arrayOf(MIGRATION_6_7, MIGRATION_7_8)
}
