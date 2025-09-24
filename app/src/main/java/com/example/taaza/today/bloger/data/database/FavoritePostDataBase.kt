package com.example.taaza.today.bloger.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PostEntity::class], version = 2)
@TypeConverters(StringListConverter::class)
abstract class FavoritePostDataBase : RoomDatabase() {
    abstract val favoritePostDao: FavoritePostDao

}