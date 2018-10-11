package com.example.marlon.theguardiannewssqlite

import android.content.ContentValues
import android.content.ContentProvider
import android.content.ContentUris
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.database.sqlite.SQLiteDatabase
import android.util.Log


private const val NEWS = 1
private const val NEW_ID = 2

class NewProvider : ContentProvider() {
    private lateinit var dbHelper: NewsSQLiteHelper
    private lateinit var database: SQLiteDatabase
    private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            .apply {
                // The calls to addURI() go here, for all of the content URI patterns that the provider
                // should recognize. All paths added to the UriMatcher have a corresponding code to return
                // when a match is found.
                addURI(NewEntry.CONTENT_AUTHORITY, NewEntry.TABLE_NAME, NEWS)
                addURI(NewEntry.CONTENT_AUTHORITY, NewEntry.TABLE_NAME + "/*", NEW_ID)
            }

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        val match = uriMatcher.match(uri)
        when (match) {
            NEWS -> return insertNew(uri, values)
            else -> throw IllegalArgumentException("Insertion is not supported for $uri")
        }
    }

    private fun insertNew(uri: Uri?, values: ContentValues?): Uri? {
        // Get writeable database
        database = dbHelper.writableDatabase
        val id = database.insertWithOnConflict(
                NewEntry.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE)
        if (id == -1L) {
            Log.e(LOG_TAG, "Failed to insert row for $uri")
            return null
        }
        return ContentUris.withAppendedId(uri, id)
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        database = dbHelper.readableDatabase

        // This cursor will hold the result of the query
        val cursor: Cursor
        // Figure out if the URI matcher can match the URI to a specific code
        val match = uriMatcher.match(uri)
        when (match) {
            NEWS -> cursor = database.query(NewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            //NEW_ID ->
            else -> throw IllegalArgumentException("Cannot query unknown URI $uri")
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(context!!.contentResolver, uri)

        // Return the cursor
        return cursor
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val match = uriMatcher.match(uri)
        return when (match) {
            NEWS -> updateNew(values, selection, selectionArgs)
//            NEW_ID -> {
//                // For the PET_ID code, extract out the ID from the URI,
//                // so we know which row to update. Selection will be "_id=?" and selection
//                // arguments will be a String array containing the actual ID.
//                val newSelection = NewEntry.COLUMN_ID + "= ?"
//                val newSelectionArgs = arrayOf(ContentUris..toString())
//                updateNew(uri, values, newSelection, newSelectionArgs)
//            }
            else -> throw IllegalArgumentException("Update is not supported for $uri")
        }
    }

    private fun updateNew(values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {

        // If there are no values to update, then don't try to update the database
        if (values?.size() == 0) {
            return 0
        }
        database = dbHelper.writableDatabase
        return database.update(NewEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        database = dbHelper.writableDatabase
        val match = uriMatcher.match(uri)
        when (match) {
            // Delete all rows that match the selection and selection args
            NEWS -> return database.delete(NewEntry.TABLE_NAME, selection, selectionArgs)
            //NEW_ID ->
            else -> throw IllegalArgumentException("Deletion is not supported for $uri")
        }
    }

    override fun getType(uri: Uri?): String {
        val match = uriMatcher.match(uri)
        return when (match) {
            NEWS -> NewEntry.CONTENT_LIST_TYPE
            NEW_ID -> NewEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalStateException("Unknown URI $uri with match $match")
        }
    }

    /**
     * Initialize the provider and the database helper object.
     */
    override fun onCreate(): Boolean {
        dbHelper = NewsSQLiteHelper(context)
        return true
    }

    companion object {

        /** Tag for the log messages  */
        val LOG_TAG: String = NewProvider::class.java.simpleName

    }
}