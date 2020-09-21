package com.lightricks.feedexercise.network

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors


class MockFeedApiService(private val context: Context) : FeedApiService {

    override fun getFeed(): Single<GetFeedResponse> {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<GetFeedResponse> =
            moshi.adapter<GetFeedResponse>(GetFeedResponse::class.java)

        val jsonString = loadTestFeedJson()
        val getFeedResponse: GetFeedResponse = jsonAdapter.fromJson(jsonString)!!

        return Single.just(getFeedResponse)
    }

    private fun loadTestFeedJson(): String {
        val inputStream = context.assets.open("get_feed_response.json")
        return BufferedReader(InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"))
    }

}
