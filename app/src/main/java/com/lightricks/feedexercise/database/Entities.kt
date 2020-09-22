package com.lightricks.feedexercise.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lightricks.feedexercise.data.FeedItem

@Entity(tableName = "feedItems")
data class FeedItemEntity(
    @PrimaryKey val id: String,
    @ColumnInfo val thumbnailUri: String,
    @ColumnInfo val isPremium: Boolean
)

fun entityFrom(feedItem: FeedItem): FeedItemEntity {
    return FeedItemEntity(feedItem.id, feedItem.thumbnailUrl, feedItem.isPremium)
}
