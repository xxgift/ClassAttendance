package com.mahidol.classattendance

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.Models.User
import com.mahidol.classattendance.Models.courselistdetail

import kotlinx.android.synthetic.main.bestregister.*


class RegisterActivity : AppCompatActivity() {
    lateinit var dataReference: DatabaseReference
    lateinit var userList: ArrayList<User>
    lateinit var courseList: ArrayList<Course>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bestregister)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        courseList = arrayListOf()

        userList = arrayListOf()
        dataReference = FirebaseDatabase.getInstance().getReference("UserProfile")


        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    userList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(User::class.java)
                        userList.add(oneUser!!)
                    }
                }
            }
        })

        regist_username.text = null
        regist_password.text = null


        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            type = radio.text.toString()
            Toast.makeText(
                applicationContext, " On checked change : ${type}",
                Toast.LENGTH_SHORT
            ).show()
        }

        register_btn.setOnClickListener {
            if (saveData()) {
                var id: Int = radioGroup.checkedRadioButtonId

                val registuser = regist_username.text.toString()
                println(registuser)
                if (id != -1) { // If any radio button checked from radio group
                    // Get the instance of radio button using id
                    val radio: RadioButton = findViewById(id)
                    Toast.makeText(
                        applicationContext, "On button click : ${radio.text}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // If no radio button checked in this radio group
                    Toast.makeText(
                        applicationContext, "On button click : nothing selected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    var type: String = ""
    private fun saveData(): Boolean {
        val user = regist_username.text.toString()
        val pass = regist_password.text.toString()
        //check each edittext must not be null
        if (user.isEmpty()) {
            regist_username.error = "Please enter a message"
            return false
        }
        if (pass.isEmpty()) {
            regist_password.error = "Please enter a message"
            return false
        }

        //check username is not already in use
        userList.forEach {
            if (it.username == user) {
                regist_username.error = "Incorrect Username"
                regist_username.text = null
                regist_username.setHint("Enter Again")
                return false
            }
        }

        val userData = User(user, pass, type,ArrayList<Course>())
        dataReference.child(user).setValue(userData)
            .addOnCompleteListener {
                Toast.makeText(applicationContext, "Message save successfully", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        return true
    }
}

