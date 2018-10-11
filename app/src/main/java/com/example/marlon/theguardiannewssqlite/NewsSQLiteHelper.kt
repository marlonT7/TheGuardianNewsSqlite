package com.example.marlon.theguardiannewssqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME= "the_guardian_news.db"
private const val DATABASE_VERSION=1
class NewsSQLiteHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val sqlCreate = "CREATE TABLE " +
            "${NewEntry.TABLE_NAME}(" +
            "${NewEntry.COLUMN_ID} TEXT PRIMARY KEY, " +
            "${NewEntry.COLUMN_THUMBNAIL} TEXT, " +
            "${NewEntry.COLUMN_SECTION_NAME} TEXT, " +
            "${NewEntry.COLUMN_HEADLINE} TEXT, " +
            "${NewEntry.COLUMN_BODY_TEXT} TEXT, " +
            "${NewEntry.COLUMN_URL} TEXT, " +
            "${NewEntry.COLUMN_SEE_LATER} INTEGER)"
    private val sqlDrop = "DROP TABLE IF EXISTS ${NewEntry.TABLE_NAME}"
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(sqlCreate)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(sqlDrop)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}