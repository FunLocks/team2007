package com.example.kanikani3

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // 独自に作成したListener
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
        abstract fun RecyclerAdapter(context: MainActivity, itemClickListener: MainActivity, itemList_user: MutableList<String>): RecyclerAdapter
    }

    val itemTextView_user: TextView = view.findViewById(R.id.longitude_user)
    val itemTextView_twitter: TextView = view.findViewById(R.id.longitude_twitter)
    val itemTextView_comment: TextView = view.findViewById(R.id.longitude_comment)

    init {
        // layoutの初期設定するときはココ
    }

}