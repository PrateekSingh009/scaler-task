package com.example.scalertask.Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scalertask.Adapters.IAdapter
import com.example.scalertask.Adapters.ParticipantListAdapter
import com.example.scalertask.R
import com.example.scalertask.dataModels.Interview
import com.example.scalertask.dataModels.UserModel
import com.example.scalertask.utils.Constants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class EditFragment: BaseFragment(), IAdapter {

    lateinit var database : FirebaseFirestore
    lateinit var rv : RecyclerView
    lateinit var addPart : Button
    lateinit var listview : FrameLayout
    lateinit var interviewTitle : EditText
    lateinit var date : EditText
    lateinit var stime : EditText
    lateinit var etime : EditText
    lateinit var calender : ImageView
    lateinit var timeselect1 : ImageView
    lateinit var timeselect2 : ImageView
    lateinit var btnUpdate: TextView
    private val listOfActiveParticipants = mutableListOf<UserModel>()
    private val emailList = mutableListOf<String>()
    private var interview: Interview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interview = arguments?.getParcelable(Constants.INTERVIEW)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        database = FirebaseFirestore.getInstance()

        rv = view.findViewById(R.id.question_list)
        addPart  = view.findViewById(R.id.bAdd)
        listview  = view.findViewById(R.id.frame)
        interviewTitle = view.findViewById(R.id.interview_title_text)
        date = view.findViewById(R.id.date_text)
        stime = view.findViewById(R.id.stime_text)
        etime = view.findViewById(R.id.etime_text)
        calender = view.findViewById(R.id.calender_button)
        timeselect1 = view.findViewById(R.id.start_Select_button)
        timeselect2 = view.findViewById(R.id.end_Select_button)
        btnUpdate = view.findViewById(R.id.update_btn)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interview?.participants?.clear()
        interview?.emailList?.clear()
        initView()
        btnUpdate.setOnClickListener {
            if(listOfActiveParticipants.size < 2) Toast.makeText(requireContext(), "Minimum Two Participants required", Toast.LENGTH_SHORT).show()
            else {
                Interview().apply {
                    title = interviewTitle.text.toString().trim()
                    date = this@EditFragment.date.text.toString().trim()
                    end_time = etime.text.toString().trim()
                    start_time = stime.text.toString().trim()
                    participants = listOfActiveParticipants as ArrayList<UserModel>
                    emailList = this@EditFragment.emailList as ArrayList<String>
                    interviewID = interview?.interviewID.toString()
                    database.collection("Interviews").document(interviewID).set(this@apply, SetOptions.merge()).addOnSuccessListener {
                            replaceFragment(Constants.LIST, Constants.MAIN_CONTAINER, null)
                        }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show()
                            }
                }
            }
        }
        calender.setOnClickListener {

            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    var mday = ""
                    var month = ""
                    val monthplus1 = monthOfYear+1
                    if(dayOfMonth.toString().length ==1) mday = "0$dayOfMonth"
                    else mday= dayOfMonth.toString()
                    if(monthplus1.toString().length ==1) month = "0$monthplus1"
                    else month = monthplus1.toString()

                    date.setText("$mday/$month/$year")
                },

                year,
                month,
                day
            )

            datePickerDialog.show()
        }


        //TIME

        timeselect1.setOnClickListener {
            timeSelect(stime)

        }

        timeselect2.setOnClickListener {
            timeSelect(etime)
        }

    }

    private fun timeSelect(time : EditText)
    {
        val c = Calendar.getInstance()

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)


        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { view, hourOfDay, minute ->
                var h = ""
                var m = ""
                if(hourOfDay.toString().length ==1) h = "0$hourOfDay"
                else h = hourOfDay.toString()
                if(minute.toString().length ==1) m = "0$minute"
                else m = minute.toString()


                time.setText("$h:$m")
            },
            hour,
            minute,
            false
        )

        timePickerDialog.show()

    }



    private fun initView() {
        interview?.apply {
            interviewTitle.setText(title)
            this@EditFragment.date.setText(date)
            this@EditFragment.stime.setText(start_time)
            this@EditFragment.etime.setText(end_time)
            updateDocument()
            addParticipants()
        }
    }

    private fun updateDocument() {
//        interview?.interviewID?.let { database.collection("Interviews").document(it).delete() }
        interview?.interviewID?.let {
           val documentRef = database.collection("Interviews").document(it)
           val map = hashMapOf<String, Any>()
            map["participants"] = FieldValue.delete()
            map["emailList"] = FieldValue.delete()
            documentRef.update(map)
        }
    }

    private fun addParticipants()
    {
        listview.visibility = View.VISIBLE
        val users : ArrayList<UserModel> = ArrayList()

        val docRef : CollectionReference = database.collection("Users")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    for(doc in document)
                    {
                        val user = doc.toObject<UserModel>()
                        users.add(user)
                    }
                    rv.apply {
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(true)
                        adapter = interview?.start_time?.let {
                            interview?.end_time?.let { it1 ->
                                ParticipantListAdapter(users,
                                    it, it1,database,context, this@EditFragment)
                            }
                        }
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
    }
    override fun onCheckClick(userModel: UserModel) {
        listOfActiveParticipants.add(userModel)
        emailList.add(userModel.uid)
    }
}