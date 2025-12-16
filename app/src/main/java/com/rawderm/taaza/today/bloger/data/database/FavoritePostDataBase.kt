package com.rawderm.taaza.today.bloger.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PostEntity::class, ShortEntity::class], version = 3,
    exportSchema = false   // Disable schema export
)
@TypeConverters(StringListConverter::class)
abstract class FavoritePostDataBase : RoomDatabase() {
    abstract val favoritePostDao: FavoritePostDao
    abstract val shortDao: ShortDao

}

object Migration2To3 : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS Shorts (
                  id TEXT NOT NULL PRIMARY KEY,
                  title TEXT NOT NULL,
                  selfUrl TEXT NOT NULL,
                  videoId TEXT NOT NULL,
                  content TEXT NOT NULL DEFAULT '',
                  date TEXT NOT NULL,
                  rowDate TEXT NOT NULL DEFAULT '',
                  description TEXT NOT NULL DEFAULT '',
                  labels TEXT NOT NULL,          -- Room converts List<String> ↔ String
                  updatedAt INTEGER NOT NULL
               )"""
        )
    }
}
