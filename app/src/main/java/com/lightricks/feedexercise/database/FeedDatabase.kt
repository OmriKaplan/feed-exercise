package com.lightricks.feedexercise.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FeedItemEntity::class), version = 1, exportSchema = false)
abstract class FeedDatabase: RoomDatabase() {
    abstract fun feedItemDao(): FeedItemDao
}
