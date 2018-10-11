package com.example.marlon.theguardiannewssqlite

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.marlon.theguardiannews.R
import kotlinx.android.synthetic.main.item.view.*

class NewsListAdapter(private var news: MutableList<New>, private val selectedNew: SelectedNew) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //Interface to work in the fragment
    interface SelectedNew {
        fun openNew(new: New)
        fun seeLater(position: Int)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val seeLater= itemView.see_later
            itemView.setOnClickListener { news[adapterPosition].let { new -> selectedNew.openNew(new) } }
            seeLater.setOnClickListener{changeSeeLater(adapterPosition)}
        }
    }

    private fun changeSeeLater(position: Int){
        selectedNew.seeLater(position)
        notifyItemChanged(position)
    }
    // Set new data after an API request
    fun setData(news: MutableList<New>) {
        this.news = news
        notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view = view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from your dataSet at this position
        // - replace the contents of the view with that element
        if (holder is MyViewHolder) {
            holder.bind()
        }
        bindView(holder, position)
    }

    // Set values to the view
    private fun bindView(holder: RecyclerView.ViewHolder, position: Int): RecyclerView.ViewHolder {
        val headline: TextView = holder.itemView.headline_preview
        val section: TextView = holder.itemView.section_preview
        val seeLater: ImageButton = holder.itemView.see_later
        // Set the news values to the view
        headline.text = news[position].headline
        section.text = news[position].sectionName
        if (news[position].seeLater== TRUE) {
            seeLater.setImageResource(R.drawable.ic_done_24dp)
        } else {
            seeLater.setImageResource(R.drawable.ic_time_24dp)
        }
        return holder
    }

    // Return the size of your dataSet (invoked by the layout manager)
    override fun getItemCount() = news.size
}