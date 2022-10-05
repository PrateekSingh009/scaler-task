package com.example.scalertask

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.scalertask.Fragments.CreateFragment
import com.example.scalertask.Fragments.ListFragment
import com.example.scalertask.dataModels.Interview
import com.example.scalertask.dataModels.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var database : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         auth = FirebaseAuth.getInstance()

         database = Firebase.firestore



        if(auth.currentUser == null)  {
            PrePopulate()
            //PrePopulateUser()
        }

        else {
            Login()

        }





        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, ListFragment())
            .commit()

    }

    private fun PrePopulate() {

        auth.createUserWithEmailAndPassword("admin@gmail.com","1234567890")
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    Log.d("User Creation" ,"Successful")
                    Toast.makeText(this,"Created new user",Toast.LENGTH_SHORT).show()
                    database.collection("Admins")
                        .add(UserModel("Admin",auth.currentUser!!.uid))
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                }
                else Log.d("User Creation" ,"Failed")

            }



    }

    private fun Login(){
        Toast.makeText(this,"Already created",Toast.LENGTH_SHORT).show()
        auth.signInWithEmailAndPassword("admin@gmail.com","1234567890")
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    Log.d("User Login" ,"Successful")
                    Toast.makeText(this,"Logged in previous user",Toast.LENGTH_SHORT).show()


                }
                else Log.d("User Login" ,"Failed")

            }
    }


    private fun PrePopulateUser()
    {

        for(i in 1..5) {
            auth.createUserWithEmailAndPassword("user$i@gmail.com", "1234567890")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("User Creation", "Successful")

                        database.collection("Users")
                            .add(UserModel("User$i","user$i@gmail.com" ))
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }

                    } else Log.d("User Creation", "Failed")

                }
        }
    }


}
