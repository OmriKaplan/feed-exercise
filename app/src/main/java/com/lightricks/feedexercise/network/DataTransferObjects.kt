package com.lightricks.feedexercise.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TemplatesMetadataItem(
    val configuration: String,
    val id: String,
    val isNew: Boolean,
    val isPremium: Boolean,
    val templateCategories: List<String>,
    val templateName: String,
    val templateThumbnailURI: String
)

@JsonClass(generateAdapter = true)
data class GetFeedResponse(val templatesMetadata: List<TemplatesMetadataItem>)
