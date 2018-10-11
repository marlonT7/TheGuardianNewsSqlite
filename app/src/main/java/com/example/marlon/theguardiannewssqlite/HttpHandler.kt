package com.example.marlon.theguardiannewssqlite

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL

// Http request an parse to string
class HttpHandler {
    fun makeServiceCall(reqUrl: String): String? {
        var response: String? = null
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            // Read the response
            val inputStream = BufferedInputStream(conn.inputStream)
            // Convert the stream to string
            response = inputStream.bufferedReader().use(BufferedReader::readText)
        } catch (e: MalformedURLException) {
            Log.e(TAG, "MalformedURLException: " + e.message)
        } catch (e: ProtocolException) {
            Log.e(TAG, "ProtocolException: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "IOException: " + e.message)
            response=null
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }
        return response
    }

    companion object {
        private val TAG = HttpHandler::class.java.simpleName
    }
}