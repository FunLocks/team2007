package com.example.kanikani3

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val context: Context,private val itemClickListener: RecyclerViewHolder.ItemClickListener,private val itemList_user:List<String>,private val itemList_twitter:List<String>, private val itemList_comment:List<String>) : RecyclerView.Adapter<RecyclerViewHolder>() {

    private var mRecyclerView : RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecyclerView = null

    }

    override fun onBindViewHolder(holder: RecyclerViewHolder?, posi_user: Int, posi_twitter: Int,posi_comment: Int) {
        holder?.let {
            it.itemTextView_user.text = itemList_user.get(posi_user)
            it.itemTextView_twitter.text = itemList_twitter.get(posi_twitter)
            it.itemTextView_comment.text = itemList_comment.get(posi_comment)
        }
    }

    override fun getItemCount_user(): Int {
        return itemList_user.size
    }
    override fun getItemCount_twitter(): Int {
        return itemList_twitter.size
    }
    override fun getItemCount_comment(): Int {
        return itemList_comment.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerViewHolder {

        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.list_item, parent, false)

        mView.setOnClickListener { view ->
            mRecyclerView?.let {
                itemClickListener.onItemClick(view, it.getChildAdapterPosition(view))
            }
        }

        return RecyclerViewHolder(mView)
    }

}