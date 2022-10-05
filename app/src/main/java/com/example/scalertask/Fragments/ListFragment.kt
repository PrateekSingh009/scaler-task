package com.example.scalertask.Fragments

import android.content.ContentValues
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scalertask.Adapters.IListener
import com.example.scalertask.Adapters.InterviewListAdapter
import com.example.scalertask.Adapters.ParticipantListAdapter
import com.example.scalertask.R
import com.example.scalertask.dataModels.Interview
import com.example.scalertask.dataModels.UserModel
import com.example.scalertask.databinding.FragmentListBinding
import com.example.scalertask.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class ListFragment : BaseFragment(), IListener {

   private lateinit var binding: FragmentListBinding
    lateinit var database : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list, container, false)
        database = FirebaseFirestore.getInstance()

        val fab : FloatingActionButton = view.findViewById(R.id.fab)
        val interviewlist : RecyclerView = view.findViewById(R.id.interviewList)
        fab.setOnClickListener {
            replaceFragment(Constants.CREATE, Constants.MAIN_CONTAINER, null)

        }

        val users : ArrayList<Interview> = ArrayList()
        val docRef : CollectionReference = database.collection("Interviews")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    for(doc in document)
                    {
                        val user = doc.toObject<Interview>()
                        users.add(user)
                    }
                   interviewlist.apply {
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(true)
                        adapter = InterviewListAdapter(users, this@ListFragment)
                    }


                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }





        return view
    }

    override fun onEditClick(interview: Interview) {
        Bundle().apply {
            putParcelable(Constants.INTERVIEW, interview)
            replaceFragment(Constants.EDIT, Constants.MAIN_CONTAINER, this)
        }
    }

}