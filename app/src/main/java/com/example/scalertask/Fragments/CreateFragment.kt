package com.example.scalertask.Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateFragment : BaseFragment(), IAdapter {


    lateinit var database : FirebaseFirestore
    lateinit var participants : RecyclerView
    lateinit var addPart : Button
    lateinit var listview : FrameLayout
    lateinit var interviewTitle : EditText
    lateinit var date : EditText
    lateinit var stime : EditText
    lateinit var etime : EditText
    lateinit var calender : ImageView
    lateinit var timeselect1 : ImageView
    lateinit var timeselect2 : ImageView
    lateinit var btnSave: TextView
    lateinit var btnCancel: TextView
    private val listOfActiveParticipants = mutableListOf<UserModel>()
    private val emailList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create, container, false)


        database = FirebaseFirestore.getInstance()

        participants= view.findViewById(R.id.question_list)
        addPart  = view.findViewById(R.id.bAdd)
        listview  = view.findViewById(R.id.frame)
        interviewTitle = view.findViewById(R.id.interview_title_text)
        date = view.findViewById(R.id.date_text)
        stime = view.findViewById(R.id.stime_text)
        etime = view.findViewById(R.id.etime_text)
        calender = view.findViewById(R.id.calender_button)
        timeselect1 = view.findViewById(R.id.start_Select_button)
        timeselect2 = view.findViewById(R.id.end_Select_button)
        btnSave = view.findViewById(R.id.save_button)
        btnCancel = view.findViewById(R.id.cancel_button)
        addPart.setOnClickListener {

            val dateT : String  = date.text.toString().trim()
            val sTimeT : String  = stime.text.toString().trim()
            val eTimeT : String  = etime.text.toString().trim()

            if(dateT.isBlank() || sTimeT.isBlank()  || eTimeT.isBlank() )
            {
                Toast.makeText(activity,"Specify Date & Time",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(Integer.parseInt(sTimeT.substring(0,2)) > Integer.parseInt(eTimeT.substring(0,2)))
            {
                Toast.makeText(activity,"Timing Error",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addParticipants("$dateT $sTimeT" , "$dateT $sTimeT")
        }

        btnSave.setOnClickListener {
            if(listOfActiveParticipants.size < 2) Toast.makeText(requireContext(), "Minimum Two Participants required", Toast.LENGTH_SHORT).show()
            else {
                Interview().apply {
                    title = interviewTitle.text.toString().trim()
                    date = this@CreateFragment.date.text.toString().trim()
                    end_time = etime.text.toString().trim()
                    start_time = stime.text.toString().trim()
                    participants = listOfActiveParticipants as ArrayList<UserModel>
                    emailList = this@CreateFragment.emailList as ArrayList<String>
                    interviewID = Calendar.getInstance().timeInMillis.toString()
                    lifecycleScope.launch {
                        database.collection("Interviews").document(interviewID).set(this@apply).addOnSuccessListener {
                                replaceFragment(Constants.LIST, Constants.MAIN_CONTAINER, null)
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }

        btnCancel.setOnClickListener {
            popBackStack()
        }

        //Calendar

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


        return view
    }


    private fun addParticipants( start : String , end : String)
    {
        listview.visibility = VISIBLE
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
                    participants.apply {
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(true)
                        adapter = ParticipantListAdapter(users,start,end,database,context, this@CreateFragment)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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

    override fun onCheckClick(userModel: UserModel) {
        listOfActiveParticipants.add(userModel)
        emailList.add(userModel.uid)
    }


}