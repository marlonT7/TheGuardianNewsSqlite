package com.example.marlon.theguardiannewssqlite

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.marlon.theguardiannews.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_new.*
import kotlinx.android.synthetic.main.fragment_new.view.*


// the fragment initialization parameters

class NewFragment : Fragment() {

    private var new: New? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            new = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new, container, false)
        view.apply {
            headline_view.text = new?.headline
        // Load image from url
        Picasso.get().load(new?.thumbnail).fit().centerCrop()
                .placeholder(R.drawable.no_image_available)
                .error(R.drawable.no_image_available)
                .into(photo_view)
        body_view.text = new?.bodyText
        section_view.text = new?.sectionName
        url_view.text = new?.url
        url_view.setOnClickListener { openWebSite() }
        if (new?.seeLater == TRUE) {
            see_later.setImageResource(R.drawable.ic_done_24dp)
        } else {
            see_later.setImageResource(R.drawable.ic_time_24dp)
        }
        see_later.setOnClickListener { seeLater(see_later) }
        }
        return view

    }

    // Update see later field in the new
    private fun seeLater(seeLaterView: ImageButton) {
        if (new?.seeLater == TRUE) {
            new?.seeLater = FALSE
            seeLaterView.setImageResource(R.drawable.ic_time_24dp)
        } else {
            new?.seeLater = TRUE
            seeLaterView.setImageResource(R.drawable.ic_done_24dp)
        }
        updateNew(new!!)
    }

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

    // Open the web site in an intent
    private fun openWebSite() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(new?.url)
        startActivity(intent)
    }
}
