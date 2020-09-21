package com.lightricks.feedexercise.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable

@Dao
interface FeedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeedItems(feedItemEntities: List<FeedItemEntity>): Completable

    @Query("DELETE FROM feedItems")
    fun clearTable(): Completable

    @Query("SELECT * FROM feedItems")
    fun getAll(): LiveData<List<FeedItemEntity>>

    @Query("SELECT COUNT(*) FROM feedItems")
    fun feedItemsCount(): Int
}
