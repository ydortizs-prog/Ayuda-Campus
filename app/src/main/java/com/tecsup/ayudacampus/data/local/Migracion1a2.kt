
package com.tecsup.ayudacampus.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `notificaciones` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `usuarioId` INTEGER NOT NULL,
                `ticketId` INTEGER NOT NULL,
                `titulo` TEXT NOT NULL,
                `mensaje` TEXT NOT NULL,
                `leida` INTEGER NOT NULL,
                `fecha` INTEGER NOT NULL,
                FOREIGN KEY(`usuarioId`)
                    REFERENCES `usuarios`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`ticketId`)
                    REFERENCES `tickets`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_notificaciones_usuarioId`
            ON `notificaciones` (`usuarioId`)
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_notificaciones_ticketId`
            ON `notificaciones` (`ticketId`)
        """.trimIndent())
    }
}
