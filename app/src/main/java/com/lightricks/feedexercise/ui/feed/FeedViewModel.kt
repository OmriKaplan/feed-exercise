package com.lightricks.feedexercise.ui.feed

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.room.Room
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.database.entityFrom
import com.lightricks.feedexercise.network.FeedApiServiceFactory
import com.lightricks.feedexercise.network.GetFeedResponse
import com.lightricks.feedexercise.network.TemplatesMetadataItem
import com.lightricks.feedexercise.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalArgumentException

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel(val feedRepository: FeedRepository) : ViewModel() {

    private val TAG = "FeedViewModel"

    private val isLoading = MutableLiveData<Boolean>()
    private val isEmpty = MutableLiveData<Boolean>()
    private val feedItems = feedRepository.feedItems
    private val networkErrorEvent = MutableLiveData<Event<String>>()

    private var refreshDisposable: Disposable? = null

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsEmpty(): LiveData<Boolean> = isEmpty
    fun getFeedItems(): LiveData<List<FeedItem>> = feedItems
    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    init {
        refresh()
    }

    fun refresh() {
        Log.d(TAG, "refresh: called")
        isLoading.value = true
        refreshDisposable?.dispose()
        refreshDisposable = feedRepository.refresh().subscribe(
            { isLoading.postValue(false) },
            { error -> handleNetworkError(error) }
        )
    }

    private fun handleNetworkError(error: Throwable?) {
        Log.d(TAG, "handleNetworkError: network error ${error.toString()}")
        isLoading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        refreshDisposable?.dispose()
    }

}

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
class FeedViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }

        val feedDatabase =
            Room.databaseBuilder(context, FeedDatabase::class.java, "feed-database").build()
        val feedApiService = FeedApiServiceFactory().createFeedApiService()
        val feedRepository = FeedRepository(feedApiService, feedDatabase)
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel(feedRepository) as T
    }
}