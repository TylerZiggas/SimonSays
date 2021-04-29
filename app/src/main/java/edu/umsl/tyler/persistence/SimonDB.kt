package edu.umsl.tyler.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlayerEntity::class],
    version = 1
)
abstract class SimonDB: RoomDatabase() {
    companion object {

        @Volatile private var instance : SimonDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, SimonDB::class.java, "simon_db"
        ).build()
    }

    abstract fun getPlayerDataDao() : PlayerDao
}