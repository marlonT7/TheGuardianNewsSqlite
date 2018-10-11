package com.example.marlon.theguardiannewssqlite

import android.net.Uri
import android.provider.BaseColumns
import android.content.ContentResolver


class NewEntry : BaseColumns {
    companion object {
        const val TABLE_NAME = "news"
        const val COLUMN_ID = "id"
        const val COLUMN_SECTION_NAME = "section_name"
        const val COLUMN_HEADLINE = "headline"
        const val COLUMN_URL = "url"
        const val COLUMN_THUMBNAIL = "thumbnail"
        const val COLUMN_BODY_TEXT = "body_text"
        const val COLUMN_SEE_LATER = "see_later"

        const val CONTENT_AUTHORITY = "com.example.marlon.theguardiannewssqlite"
        private val BASE_CONTENT_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")
        val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME)

        /**
         * The MIME type of the [.CONTENT_URI] for a list of pets.
         */
        const val CONTENT_LIST_TYPE ="${ContentResolver.CURSOR_DIR_BASE_TYPE}/$CONTENT_AUTHORITY/$TABLE_NAME"

        /**
         * The MIME type of the [.CONTENT_URI] for a single pet.
         */
        const val CONTENT_ITEM_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/$CONTENT_AUTHORITY/$TABLE_NAME"
    }
}

