package com.example.scalertask.Adapters

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.scalertask.R
import com.example.scalertask.dataModels.Interview
import com.example.scalertask.dataModels.UserModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ParticipantListAdapter(
    private var list: ArrayList<UserModel>,
    private var start: String,
    private var end: String,
    private var database: FirebaseFirestore,
    private var context: Context,
    private var iAdapter: IAdapter? = null
) : RecyclerView.Adapter<ParticipantListAdapter.ViewHolder>() {

    lateinit var usersList: ArrayList<UserModel>

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt: TextView
        val check: CheckBox

        init {
            txt = view.findViewById(R.id.name)
            check = view.findViewById(R.id.checkbox)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParticipantListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.participantlistview, parent, false)
        return ParticipantListAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantListAdapter.ViewHolder, position: Int) {

      //  usersList  = ArrayList()

        holder.txt.text = list[position].name

        holder.check.setOnClickListener {

            onCheckListener(position,holder)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun onCheckListener(position: Int,holder: ViewHolder) {

        holder.check.isChecked = false


        val docRef: CollectionReference = database.collection("Interviews")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    var flag =0
                    for (doc in document) {
                        val interview = doc.toObject<Interview>()

                        if (dataChecker(
                                start,
                                end,
                                "${interview.date} ${interview.start_time}",
                                "${interview.date} ${interview.end_time}"
                            )
                        ) {
                            continue;
                        } else {
                            if(interview.emailList.contains(list[position].uid)){
                                Toast.makeText(
                                    context,
                                    "User has another interview in this timing",
                                    Toast.LENGTH_SHORT
                                ).show()
                                holder.check.isChecked = false
                                flag=1
                                break
                            }
                        }
                    }
                    if(flag==0)
                    {
                        //usersList.add(list[position])
                        holder.check.isChecked = true
                        iAdapter?.onCheckClick(list[position])
                    }

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                    //usersList.add(list[position])
                    holder.check.isChecked = true

                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }




    }

    private fun dataChecker(
        sNewInterview: String,
        eNewInterview: String,
        sInterview: String,
        eInterview: String
    ): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")

        try {

            val sN: Date = dateFormat.parse(sNewInterview)
            val eN: Date = dateFormat.parse(eNewInterview)
            val s: Date = dateFormat.parse(sInterview)
            val e: Date = dateFormat.parse(eInterview)

            return (e.before(sN)) || (s.after(eN))


        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

}

interface IAdapter {
    fun onCheckClick(userModel: UserModel)
}



