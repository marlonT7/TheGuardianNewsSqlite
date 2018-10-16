package com.example.marlon.theguardiannewssqlite

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.marlon.theguardiannews.R
import java.io.IOException
import java.lang.ref.WeakReference

// the fragment initialization parameters
const val KEY_URL = "url key"
const val KEY_FIELD = "field_key"
const val ARG_PARAM1 = "new"
const val GENERAL = ""
const val SPORT = "sport"
const val POLITICS = "politics"
const val TECHNOLOGY = "technology"
const val SCIENCE = "science"
const val BUSINESS = "business"
const val SECTION = "section"
const val QUERY = "&q="
const val SEE_LATER = "see later"

class NewListFragment : Fragment(),
        NewsListAdapter.SelectedNew,
        GetNews.GetNewsCallback,
        LoaderManager.LoaderCallbacks<Cursor> {
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        // List of columns to display
        val projection = arrayOf(
                NewEntry.COLUMN_ID,
                NewEntry.COLUMN_HEADLINE,
                NewEntry.COLUMN_SECTION_NAME,
                NewEntry.COLUMN_THUMBNAIL,
                NewEntry.COLUMN_BODY_TEXT,
                NewEntry.COLUMN_URL,
                NewEntry.COLUMN_SEE_LATER)
        val selectionArgs: Array<String?>
        val selection: String?
        when {
            urlQueryParam == GENERAL -> {
                selection = null
                selectionArgs = arrayOf()
            }
            queryField == SECTION -> {
                selection = "${NewEntry.COLUMN_SECTION_NAME} LIKE ?"
                selectionArgs = arrayOf(urlQueryParam)
            }
            queryField == SEE_LATER -> {
                selection = "${NewEntry.COLUMN_SEE_LATER} LIKE ?"
                selectionArgs = arrayOf(TRUE)
            }
            else -> {
                selection = "${NewEntry.COLUMN_BODY_TEXT} LIKE ?"
                selectionArgs = arrayOf("%$urlQueryParam%")
            }
        }
        val sortOrder = "${NewEntry.COLUMN_ID} ASC"
        return CursorLoader(context!!,
                NewEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data.use {
            if (news[0] == newLoading) {
                news.removeAt(0)
            }
            if (it?.count != 0) {
                with(it) {
                    if (this!!.moveToNext()) {
                        val id = it!!.getString(it.getColumnIndex(com.example.marlon.theguardiannewssqlite.NewEntry.COLUMN_ID))
                        val headline = it.getString(it.getColumnIndex(com.example.marlon.theguardiannewssqlite.NewEntry.COLUMN_HEADLINE))
                        val sectionName = it.getString(it.getColumnIndex(com.example.marlon.theguardiannewssqlite.NewEntry.COLUMN_SECTION_NAME))
                        val url = it.getString(it.getColumnIndex(com.example.marlon.theguardiannewssqlite.NewEntry.COLUMN_URL))
                        val thumbnail = it.getString(it.getColumnIndex(com.example.marlon.theguardiannewssqlite.NewEntry.COLUMN_THUMBNAIL))
                        val bodyText = it.getString(it.getColumnIndex(com.example.marlon.theguardiannewssqlite.NewEntry.COLUMN_BODY_TEXT))
                        val seeLater = it.getString(it.getColumnIndex(com.example.marlon.theguardiannewssqlite.NewEntry.COLUMN_SEE_LATER))
                        val new = New(id, sectionName, headline, url, thumbnail, bodyText, seeLater)
                        news.add(new)
                    }
                }
            }
            if (news.size == 0) {
                news.add(newNotFound)
            }
            viewAdapter.notifyItemChanged(news.size - 1)
        }

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

    // Changes see later state
    override fun seeLater(position: Int) {
        news[position].changeSeeLater()
        updateNew(news[position])
    }


    // Updates the data when async task is finished
    override fun finished(newsList: MutableList<New>) {
        if (newsList.size == 0 && (news[0] == newLoading)) {
            // Remove the default value and set Not found if the result don't has data
            news.removeAt(0)
            news.add(newNotFound)
            viewAdapter.notifyDataSetChanged()
        } else {
            updateData(newsList)
        }
    }

    // Send the new data to the adapter
    private fun updateData(newsList: MutableList<New>) {
        if (page == 1) {
            news = newsList
        } else {
            news.addAll(newsList)
        }
        viewAdapter.setData(news)
    }

    // Displays in background errors
    override fun displayErrorMessage(errorMessage: String) {
        this.activity?.runOnUiThread {
            Toast.makeText(this.activity,
                    errorMessage,
                    Toast.LENGTH_LONG).show()
        }
    }

    // Display async task messages
    override fun displayMessage(message: String) {
        Toast.makeText(this.activity,
                message,
                Toast.LENGTH_LONG).show()
    }

    // Open the new in other activity
    override fun openNew(new: New) {
        val bundle = Bundle()
        bundle.putParcelable(ARG_PARAM1, new)
        val intent = Intent(context, NewActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    //  Result request  page
    private var page: Int = 0
    // If the param is section, the search  is by section,
    var queryField: String? = null
    // Word to search or section
    private var urlQueryParam: String? = null
    // Final url to send to request
    private lateinit var urlQuery: String
    lateinit var getNews: GetNews
    // Displays this new when the search not found result
    private val newNotFound = New(
            id = "Not Found",
            headline = "Not found",
            sectionName = "Not found",
            url = "Not found",
            thumbnail = "Not found",
            bodyText = "Not found")
    // Displays this new when the search is loading
    private val newLoading = New(
            id = "loading",
            headline = "loading",
            url = "loading",
            bodyText = "loading",
            thumbnail = "loading",
            sectionName = "loading")

    private var news: MutableList<New> = mutableListOf(newLoading)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            urlQueryParam = it.getString(KEY_URL)
            queryField = it.getString(KEY_FIELD)
        }
        getNews = GetNews(createUrl(), this, WeakReference(context!!))
        if (isConnected() && queryField != SEE_LATER) {
            // Request data to the API y an async task
            deleteNews()
            getNews.execute()
        } else {
            readNews()
        }
    }
    // Increase in 1 the page and formats the url to request to the API
    fun createUrl(): String {
        // change the page in the url
        page++
        urlQuery = when {
            urlQueryParam == GENERAL -> "https://content.guardianapis.com/search?show-fields=headline%2CbodyText%2CshortUrl%2Cthumbnail&page=$page&page-size=30&api-key=4c50cee8-de61-4d74-ae45-f9a0c6b57a71"
            queryField == SECTION -> "https://content.guardianapis.com/search?section=$urlQueryParam&show-fields=headline%2CbodyText%2CshortUrl%2Cthumbnail&page=$page&page-size=30&api-key=4c50cee8-de61-4d74-ae45-f9a0c6b57a71"
            else -> "https://content.guardianapis.com/search?show-fields=headline%2CbodyText%2CshortUrl%2Cthumbnail&page=$page&page-size=30$QUERY$urlQueryParam&api-key=4c50cee8-de61-4d74-ae45-f9a0c6b57a71"
        }
        return urlQuery
    }

    private lateinit var viewManager: LinearLayoutManager

    private lateinit var viewAdapter: NewsListAdapter


    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_list, container, false)
        viewManager = LinearLayoutManager(this.context)
        // Sets data to the recycler view
        viewAdapter = NewsListAdapter(news, this)
        // Divides the data in categories and send to the corresponding view page
        recyclerView = view.findViewById<RecyclerView>(R.id.news_list_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager
            // specify an viewAdapter
            adapter = viewAdapter

            // Add the pagination, loads more data when cant scroll down
            addOnScrollListener(OnScrollListener(this@NewListFragment))
        }
        return view
    }

    // Return true if is connected
    @Throws(InterruptedException::class, IOException::class)
    fun isConnected(): Boolean {
        val command = "ping -c 1 theguardian.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }

    // Cleans old news if seeLater is not checked
    private fun deleteNews() {
        // Define 'where' part of query.
        val selectionArgs: Array<String?>
        val selection: String?
        when {
            urlQueryParam == GENERAL -> {
                selection = "${NewEntry.COLUMN_SEE_LATER} LIKE ?"
                selectionArgs = arrayOf(FALSE)
                context?.contentResolver?.delete(NewEntry.CONTENT_URI, selection, selectionArgs)
            }
            // Specify arguments in placeholder order.
            queryField == SECTION -> {
                selection = "${NewEntry.COLUMN_SEE_LATER} LIKE ? AND ${NewEntry.COLUMN_SECTION_NAME} LIKE ?"
                selectionArgs = arrayOf(FALSE, urlQueryParam)
                context?.contentResolver?.delete(NewEntry.CONTENT_URI, selection, selectionArgs)
            }
        }

    }

    // Update field seeLater
    private fun updateNew(new: New) {
        // New value for one column
        val values = ContentValues().apply {
            put(NewEntry.COLUMN_SEE_LATER, new.seeLater)
        }
        // Which row to update, based on the title
        val selection = "${NewEntry.COLUMN_ID} LIKE ?"
        val selectionArgs = arrayOf(new.id)
        context?.contentResolver?.update(
                NewEntry.CONTENT_URI,
                values,
                selection,
                selectionArgs)
    }


    // Read from the database
    private fun readNews() {
        // List of columns to display
        val projection = arrayOf(
                NewEntry.COLUMN_ID,
                NewEntry.COLUMN_HEADLINE,
                NewEntry.COLUMN_SECTION_NAME,
                NewEntry.COLUMN_THUMBNAIL,
                NewEntry.COLUMN_BODY_TEXT,
                NewEntry.COLUMN_URL,
                NewEntry.COLUMN_SEE_LATER)
        val selectionArgs: Array<String?>
        val selection: String?
        when {
            urlQueryParam == GENERAL -> {
                selection = null
                selectionArgs = arrayOf()
            }
            queryField == SECTION -> {
                selection = "${NewEntry.COLUMN_SECTION_NAME} LIKE ?"
                selectionArgs = arrayOf(urlQueryParam)
            }
            queryField == SEE_LATER -> {
                selection = "${NewEntry.COLUMN_SEE_LATER} LIKE ?"
                selectionArgs = arrayOf(TRUE)
            }
            else -> {
                selection = "${NewEntry.COLUMN_BODY_TEXT} LIKE ?"
                selectionArgs = arrayOf("%$urlQueryParam%")
            }
        }
        val sortOrder = "${NewEntry.COLUMN_ID} ASC"
        val cursor = context?.contentResolver?.query(
                NewEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )
        cursor.use {
            news.removeAt(0)
            if (it?.count != 0) {
                with(it) {
                    while (this!!.moveToNext()) {
                        val id = it!!.getString(it.getColumnIndex(NewEntry.COLUMN_ID))
                        val headline = it.getString(it.getColumnIndex(NewEntry.COLUMN_HEADLINE))
                        val sectionName = it.getString(it.getColumnIndex(NewEntry.COLUMN_SECTION_NAME))
                        val url = it.getString(it.getColumnIndex(NewEntry.COLUMN_URL))
                        val thumbnail = it.getString(it.getColumnIndex(NewEntry.COLUMN_THUMBNAIL))
                        val bodyText = it.getString(it.getColumnIndex(NewEntry.COLUMN_BODY_TEXT))
                        val seeLater = it.getString(it.getColumnIndex(NewEntry.COLUMN_SEE_LATER))
                        news.add(New(id, sectionName, headline, url, thumbnail, bodyText, seeLater))
                    }

                }
            } else {
                news.add(newNotFound)
            }
        }
    }

    override fun onDestroy() {
        // Cancel the async task when the fragment destroys
        if (!getNews.isCancelled) {
            getNews.cancel(true)
        }
        super.onDestroy()
    }

    // Pagination if need more elements from the api
    class OnScrollListener(private val fragment: NewListFragment) : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // If the task has finished, run the task with a new url
            if (!(recyclerView!!.canScrollVertically(1)) &&
                    fragment.isConnected() &&
                    fragment.queryField != SEE_LATER &&
                    fragment.getNews.status != (AsyncTask.Status.RUNNING)) {
                fragment.getNews.cancel(true)
                fragment.getNews = GetNews(fragment.createUrl(), fragment, WeakReference(fragment.context!!))
                fragment.getNews.execute()
            }
        }
    }
}