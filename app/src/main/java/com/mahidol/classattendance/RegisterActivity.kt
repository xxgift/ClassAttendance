package com.mahidol.classattendance

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.*
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.Models.User

import kotlinx.android.synthetic.main.register.*


class RegisterActivity : AppCompatActivity() {
    lateinit var dataReference: DatabaseReference
    lateinit var userList: ArrayList<String>
    lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userList = arrayListOf()
        dataReference = FirebaseDatabase.getInstance().getReference("UserProfile")


        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    userList.clear()
                    for (i in p0.children) {
                         val oneUser = i.key
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
//            Toast.makeText(
//                applicationContext, " On checked change : ${type}",
//                Toast.LENGTH_SHORT
//            ).show()
        }

        register_btn.setOnClickListener {
            saveData()

        }
    }


    private fun saveData(): Boolean {
        val user = regist_username.text.toString().decapitalize()
        val pass = regist_password.text.toString()
        var id: Int = radioGroup.checkedRadioButtonId

        //check each edittext must not be null
        if (user.isEmpty()) {
            regist_username.error = "Please enter a message"
            return false
        }
        if (pass.isEmpty()) {
            regist_password.error = "Please enter a message"
            return false
        }

        //check radiobutton must be selected
        if (id != -1) { // If any radio button checked from radio group
            // Get the instance of radio button using id
            val radio: RadioButton = findViewById(id)
//            Toast.makeText(
//                applicationContext, "On button click : ${radio.text}",
//                Toast.LENGTH_SHORT
//            ).show()
        } else {
            // If no radio button checked in this radio group
            radio_student.error = "Please select a type"
            radio_teacher.error = "Please select a type"
            return false
        }

        if (type=="Teacher"){
            if(user.substring(0,1)== "t"){
            }else{
                regist_username.error = "Username of TEACHER must be in the form of t*******"
                return false
            }
        }
        if (type=="Student"){
            if(user.substring(0,1) == "u"){
            }else{
                regist_username.error = "Username of STUDENT must be in the form of u*******"
                return false
            }
        }



        //check username is not already in use
        userList.forEach {
            if (it == user) {
                regist_username.error = "Username already exists"
                regist_username.text = null
                regist_username.setHint("Enter Again")
                return false
            }
        }

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_PHONE_STATE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
        }
        var imei = telephonyManager.imei

        val userData = User(user, pass, type, HashMap<String,Course>(),imei)
        dataReference.child(user).setValue(userData)
            .addOnCompleteListener {
                Toast.makeText(applicationContext, "Register successfully", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        return true
    }
}

