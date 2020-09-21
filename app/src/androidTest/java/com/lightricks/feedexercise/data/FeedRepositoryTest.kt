package com.lightricks.feedexercise.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.MockFeedApiService
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FeedRepositoryTest {
    lateinit var instrumentationContext: Context
    lateinit var mockFeedApiService: MockFeedApiService
    lateinit var feedDatabase: FeedDatabase
    lateinit var repository: FeedRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
        mockFeedApiService = MockFeedApiService(instrumentationContext)
        feedDatabase = Room.inMemoryDatabaseBuilder<FeedDatabase>(
            instrumentationContext,
            FeedDatabase::class.java
        ).build()
        repository = FeedRepository(mockFeedApiService, feedDatabase)
    }

    @Test
    fun refresh_saveToDB() {
        repository.refresh().test()
        val savedFeedItemEntities: List<FeedItemEntity>? =
            feedDatabase.feedItemDao().getAll().blockingObserve()

        assertThat(savedFeedItemEntities?.size).isEqualTo(10)
    }

    @Test
    fun feedItems_readFromDB() {
        repository.refresh().test()
        val feedItemsFromDB: List<FeedItem>? =
            Transformations.map(feedDatabase.feedItemDao().getAll()) {
                it.toFeedItems()
            }.blockingObserve()
        val feedItems = repository.feedItems.blockingObserve()

        assertThat(feedItems?.size).isEqualTo(feedItemsFromDB?.size)
        assertThat(feedItems).isEqualTo(feedItemsFromDB)
    }

    private fun List<FeedItemEntity>.toFeedItems(): List<FeedItem> {
        return map {
            FeedItem(it.id, it.uri, it.isPro)
        }
    }
}

private fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            value = t
            latch.countDown()
            removeObserver(this)
        }
    }

    observeForever(observer)
    latch.await(5, TimeUnit.SECONDS)
    return value
}
