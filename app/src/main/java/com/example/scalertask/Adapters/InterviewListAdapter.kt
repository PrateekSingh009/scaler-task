package com.example.scalertask.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scalertask.R
import com.example.scalertask.dataModels.Interview

import com.google.firebase.firestore.FirebaseFirestore

class InterviewListAdapter(list :  ArrayList<Interview>, private var iListener: IListener? = null) : RecyclerView.Adapter<InterviewListAdapter.ViewHolder>() {

    private var list : ArrayList<Interview> = list
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val nametxt : TextView
        val datetxt : TextView
        val start: TextView
        val end: TextView
        val edit_button : ImageView

        init {
            nametxt= view.findViewById(R.id.sender_text_view)
            datetxt = view.findViewById(R.id.date_text_view)
            start = view.findViewById(R.id.start_time)
            end = view.findViewById(R.id.end_time)
            edit_button = view.findViewById(R.id.edit_button)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterviewListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_feed,parent,false)

        return InterviewListAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: InterviewListAdapter.ViewHolder, position: Int) {

        holder.nametxt.text = list[position].title
        holder.datetxt.text =  "Date : "+list[position].date
        holder.start.text =  "Start Time : "+list[position].start_time
        holder.end.text =  "End Time : "+list[position].end_time

        holder.edit_button.setOnClickListener {
            iListener?.onEditClick(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

interface IListener {
    fun onEditClick(interview: Interview)
}