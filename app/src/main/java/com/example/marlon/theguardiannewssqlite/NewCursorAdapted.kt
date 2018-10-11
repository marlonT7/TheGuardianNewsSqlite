package com.example.marlon.theguardiannewssqlite

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.example.marlon.theguardiannews.R


class NewCursorAdapted(context: Context,cursor:Cursor):CursorAdapter(context,cursor,0){
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.item,parent,false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val headline = view?.findViewById<TextView>(R.id.headline_preview)
        val section = view?.findViewById<TextView>(R.id.section_preview)

        // Set the news values to the view
        headline?.text = cursor?.getString(cursor.getColumnIndex(NewEntry.COLUMN_HEADLINE))
        section?.text = cursor?.getString(cursor.getColumnIndex(NewEntry.COLUMN_SECTION_NAME))
    }

}