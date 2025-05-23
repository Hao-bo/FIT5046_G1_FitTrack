// ExerciseModels.kt
package com.example.fittrackapp.data.model

import com.google.gson.annotations.SerializedName


data class ExerciseInfo(
    val id: Int,
    val uuid: String,
    val created: String,
    @SerializedName("last_update") val lastUpdate: String,
    @SerializedName("last_update_global") val lastUpdateGlobal: String,
    val category: ExerciseCategory,
    val muscles: List<Muscle>,
    @SerializedName("muscles_secondary") val musclesSecondary: List<Muscle>,
    val equipment: List<Equipment>,
    val license: License,
    @SerializedName("license_author") val licenseAuthor: String?,
    val images: List<ExerciseImage>,
    val translations: List<ExerciseTranslation>,
    val variations: Int,
    val videos: List<ExerciseVideo>,
    @SerializedName("author_history") val authorHistory: List<String>,
    @SerializedName("total_authors_history") val totalAuthorsHistory: List<String>
)

data class ExerciseCategory(
    val id: Int,
    val name: String
)

data class Muscle(
    val id: Int,
    val name: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("is_front") val isFront: Boolean,
    @SerializedName("image_url_main") val imageUrlMain: String,
    @SerializedName("image_url_secondary") val imageUrlSecondary: String
)

data class Equipment(
    val id: Int,
    val name: String
)

data class License(
    val id: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("short_name") val shortName: String,
    val url: String
)

data class ExerciseImage(
    val id: Int,
    val uuid: String,
    val exercise: Int,
    @SerializedName("exercise_uuid") val exerciseUuid: String,
    val image: String,
    @SerializedName("is_main") val isMain: Boolean,
    val style: String,
    val license: Int,
    @SerializedName("license_title") val licenseTitle: String,
    @SerializedName("license_object_url") val licenseObjectUrl: String,
    @SerializedName("license_author") val licenseAuthor: String,
    @SerializedName("license_author_url") val licenseAuthorUrl: String,
    @SerializedName("license_derivative_source_url") val licenseDerivativeSourceUrl: String,
    @SerializedName("author_history") val authorHistory: List<String>
)

data class ExerciseTranslation(
    val id: Int,
    val uuid: String,
    val name: String,
    val exercise: Int,
    val description: String,
    val created: String,
    val language: Int,
    val aliases: List<ExerciseAlias>,
    val notes: List<ExerciseNote>,
    val license: Int,
    @SerializedName("license_title") val licenseTitle: String,
    @SerializedName("license_object_url") val licenseObjectUrl: String,
    @SerializedName("license_author") val licenseAuthor: String,
    @SerializedName("license_author_url") val licenseAuthorUrl: String,
    @SerializedName("license_derivative_source_url") val licenseDerivativeSourceUrl: String,
    @SerializedName("author_history") val authorHistory: List<String>
)

data class ExerciseAlias(
    val id: Int,
    val uuid: String,
    val alias: String
)

data class ExerciseNote(
    val id: Int,
    val uuid: String,
    val translation: Int,
    val comment: String
)

data class ExerciseVideo(
    val id: Int,
    val uuid: String,
    val exercise: Int,
    val video: String,
    @SerializedName("is_main") val isMain: Boolean,
    val size: Int,
    val duration: String,
    val width: Int,
    val height: Int,
    val codec: String,
    @SerializedName("codec_long") val codecLong: String,
    val license: Int,
    @SerializedName("license_title") val licenseTitle: String,
    @SerializedName("license_object_url") val licenseObjectUrl: String,
    @SerializedName("license_author") val licenseAuthor: String,
    @SerializedName("license_author_url") val licenseAuthorUrl: String,
    @SerializedName("license_derivative_source_url") val licenseDerivativeSourceUrl: String,
    @SerializedName("author_history") val authorHistory: List<String>
)