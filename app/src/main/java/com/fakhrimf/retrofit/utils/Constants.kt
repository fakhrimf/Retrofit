package com.fakhrimf.retrofit.utils

import com.fakhrimf.retrofit.BuildConfig

const val LOCALE_ID = "id"
const val LOCALE_EN = "en"
const val MOVIE_LATEST_FAIL = "Failed to fetch Latest Movies"
const val MOVIE_POPULAR_FAIL = "Failed to fetch Popular Movies"
const val SHOW_LATEST_FAIL = "Failed to fetch Latest Shows"
const val SHOW_POPULAR_FAIL = "Failed to fetch Popular Shows"
const val TAG_ERROR = "ERR"
const val DURATION: Long = 250
const val TRANSPARENT_ALPHA = 0.0F
const val OPAQUE_ALPHA = 1.0F
const val VALUE_KEY = "model"
const val TYPE_KEY = "type"
const val API_KEY = BuildConfig.API_KEY
const val PREFERENCE_MOVIE_TYPE_KEY = "type_movie"
const val PREFERENCE_SHOW_TYPE_KEY = "type_show"
const val PREFERENCE_FIRST_RUN_KEY = "first_run"
const val PREFERENCE_KEY = "type_preference"
const val VALUE_LIST = "list"
const val VALUE_CARD = "card"
const val VALUE_GRID = "grid"
const val OPEN_DETAIL_ACTION = "com.fakhrimf.retrofit.OPEN_DETAIL_ACTION"
const val PENDING_INTENT_KEY = 34
const val WIDGET_INTENT_KEY = "com.fakhrimf.retrofit.WIDGET_INTENT"
const val WIDGET_REFRESH = "KEY_WIDGET_REFRESH"
const val CONTENT_SCHEME = "content"
const val AUTHORITY = "com.fakhrimf.retrofit"
const val FAVORITE_PROVIDER = 64
const val FAVORITE_PROVIDER_ID = 73
const val CHANNEL_DESC = "Reminds you to comeback and check movies you love"
const val CHANNEL_DESC_RELEASE = "Tells you when a new movie is released"
const val REMINDER_SHARED_PREFERENCES_KEY = "reminder_shared_preferences"
const val CHANNEL_ID = "primary_notification_channel"
const val CHANNEL_ID_RELEASE = "primary_release_notification_channel"
const val CHANNEL_NAME = "Reminder Notification"
const val CHANNEL_NAME_RELEASE = "Release Notification"
const val NOTIFICATION_REQUEST_CODE = 3
const val NOTIFICATION_RELEASE_REQUEST_CODE = 27
const val RECEIVER_INTENT_KEY = "receive_this"
const val HOUR_SEVEN = "07"
const val HOUR_EIGHT = "08"
const val HOUR_NINE = "09"
const val MINUTE_ZERO = "00"
const val KEY_DAILY = "daily"
const val KEY_RELEASE = "release"

enum class Type {
    LIST, CARD, GRID
}