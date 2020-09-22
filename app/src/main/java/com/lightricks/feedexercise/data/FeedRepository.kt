package com.lightricks.feedexercise.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.GetFeedResponse
import com.lightricks.feedexercise.network.TemplatesMetadataItem
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepository(
    private val feedApiService: FeedApiService,
    private val feedDatabase: FeedDatabase
) {

    private val TAG = "FeedRepository"

    val feedItems: LiveData<List<FeedItem>> =
        Transformations.map(feedDatabase.feedItemDao().getAll()) {
            it.toFeedItems()
        }

    private fun List<FeedItemEntity>.toFeedItems(): List<FeedItem> {
        return map {
            FeedItem(it.id, it.thumbnailUri, it.isPremium)
        }
    }

    fun refresh(): Completable {
        Log.d(TAG, "refresh: called")
        return feedApiService.getFeed()
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { feedResponse: GetFeedResponse ->
                handleResponse(feedResponse.templatesMetadata)
            }
    }

    private fun handleResponse(feedResponse: List<TemplatesMetadataItem>?): Completable {
        Log.d(TAG, "handleResponse: handling response")
        val dbFeedItems = ArrayList<FeedItemEntity>()
        for (item: TemplatesMetadataItem in feedResponse!!) {
            dbFeedItems.add(item.toFeedItemEntity())
        }

        return feedDatabase.feedItemDao().insertFeedItems(dbFeedItems)
    }

    private fun TemplatesMetadataItem.toFeedItemEntity(): FeedItemEntity {
        val thumbnailUriPrefix = "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"

        return FeedItemEntity(
            this.id,
            "${thumbnailUriPrefix}${this.templateThumbnailURI}",
            this.isPremium
        )
    }

}