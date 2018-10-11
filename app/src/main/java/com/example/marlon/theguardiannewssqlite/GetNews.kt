package com.example.marlon.theguardiannewssqlite

import android.content.ContentValues
import android.content.Context


import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference


class GetNews(val url: String, private val getNewsCallback: GetNewsCallback, private val weakReference: WeakReference<Context>) : AsyncTask<Void, Void, Void>() {
    private val tag = MainActivity::class.java.simpleName
    private var news: MutableList<New> = mutableListOf()

    interface GetNewsCallback {
        fun displayMessage(message: String)
        fun displayErrorMessage(errorMessage: String)
        fun finished(newsList: MutableList<New>)
    }

    // Save the downloaded data
    private fun insertNews(newsList: MutableList<New>) {
        for (new in newsList) {
            val values = ContentValues().apply {
                put(NewEntry.COLUMN_ID, new.id)
                put(NewEntry.COLUMN_HEADLINE, new.headline)
                put(NewEntry.COLUMN_SECTION_NAME, new.sectionName)
                put(NewEntry.COLUMN_URL, new.url)
                put(NewEntry.COLUMN_THUMBNAIL, new.thumbnail)
                put(NewEntry.COLUMN_BODY_TEXT, new.bodyText)
                put(NewEntry.COLUMN_SEE_LATER, new.seeLater)
            }
            weakReference.get()?.contentResolver?.insert(NewEntry.CONTENT_URI, values)
        }
    }

    // Get the request results
    override fun doInBackground(vararg params: Void?): Void? {
        val sh = HttpHandler()
        // Making a request to url and getting response
        val jsonStr = sh.makeServiceCall(url)
        if (jsonStr != null) {
            try {
                val jsonObj = JSONObject(jsonStr)
                // Getting JSON Array node
                val response = jsonObj.getJSONObject("response")
                val results = response.getJSONArray("results")
                // looping through All results
                if (results.length() != 0) {
                    for (i in 0 until results.length()) {
                        val result = results.getJSONObject(i)
                        val id = result.getString("id")
                        val sectionName = result.getString("sectionName")
                        // field node is JSON Object
                        val field = result.getJSONObject("fields")
                        val headline = field.getString("headline")
                        val url = field.getString("shortUrl")
                        val thumbnail = field.optString("thumbnail", "Not found")
                        val bodyText = field.getString("bodyText")
                        // tmp hash map for single new
                        val new = New(id, sectionName, headline, url, thumbnail, bodyText)
                        news.add(new)
                    }
                }
                insertNews(news)
                changeIfSeeLater(news)
            } catch (e: JSONException) {
                Log.e(tag, "Json parsing error: " + e.message)
                getNewsCallback.displayErrorMessage("Json parsing error: " + e.message)
            }
        }
        return null
    }

    // Send data to the fragment
    override fun onPostExecute(result: Void?) {
        getNewsCallback.finished(news)
        getNewsCallback.displayMessage("The data is fully downloaded")
    }

    private fun changeIfSeeLater(newsList: MutableList<New>){
        // Verify if see later field is checked
        val projection = arrayOf(
                NewEntry.COLUMN_SEE_LATER)
        val sortOrder = "${NewEntry.COLUMN_ID} ASC"
        val selection = "${NewEntry.COLUMN_SEE_LATER} LIKE ? AND ${NewEntry.COLUMN_ID} LIKE ?"
        for (new in newsList) {
            val selectionArgs = arrayOf(TRUE, new.id)
            val cursor = weakReference.get()?.contentResolver?.query(
                    NewEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            )
            cursor.use { it ->
                with(it) {
                    if (this!!.moveToNext()) {
                        val seeLater = it!!.getString(it.getColumnIndex(com.example.marlon.theguardiannewssqlite.NewEntry.COLUMN_SEE_LATER))
                        new.seeLater = seeLater
                    }
                }
            }
        }
    }


}

