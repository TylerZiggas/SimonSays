// Author: Tyler Ziggas
// Date: May 2021
// Our database

package edu.umsl.tyler.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [GameEntity::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class GameDatabase: RoomDatabase() {
    abstract fun gameDao(): GameDao
}