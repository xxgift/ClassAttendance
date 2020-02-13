package com.mahidol.classattendance.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment

import com.google.gson.Gson
import com.mahidol.classattendance.Helper.HTTPHelper
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.Models.User
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.popup_addcourse.*

class popup_addcourse_Fragment : DialogFragment() {
    lateinit var mContext: Context
    lateinit var courseList: ArrayList<Course>
    lateinit var currentUser: User
    var url = "https://studenttracking-47241.firebaseio.com/UserProfile/"
    var userprofile: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.popup_addcourse,container,false)
    }

    override fun onStart() {
        super.onStart()
        setInitDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addcourse_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }

        addcourse_addbtn.setOnClickListener {
          //save course data

        }
    }
    private fun saveData():Boolean{
        val addcourseID = addcourse_courseID.text.toString()
        val addcourseH = addcourse_hours.text.toString()
        val addcourseM = addcourse_minutes.text.toString()
        //check each edittext must not be null

        if (addcourseID.isEmpty()) {
            addcourse_courseID.error = "Not valid"
            return false
        }

        if (addcourseH.isEmpty()) {
            addcourse_courseID.error = "Not valid"
            return false
        }
        if (addcourseM.isEmpty()) {
            addcourse_courseID.error = "Not valid"
            return false
        }

        //check courseID is not already in use
        var asyncTask = object : AsyncTask<String, String, String>() {

            override fun onPreExecute() {

            }

            override fun doInBackground(vararg p0: String?): String {
                val helper = HTTPHelper()
                return helper.getHTTPData(url + currentUser + courseList + ".json")
            }

            override fun onPostExecute(result: String?) {
                if (result != "null") {
                    userprofile = Gson().fromJson(result, User::class.java)
                    //check username and password is matched


                }
            }

        }
        asyncTask.execute()

        courseList.forEach {
            if(it.courseID == addcourseID){
                addcourse_courseID.error = "Invalid CourseID"
                addcourse_courseID.setHint("Enter Again")
                return false
            }
        }
        //send value to firebase
//        val userData = User(user,pass,type)
//        dataReference.child(user).setValue(userData)
//            .addOnCompleteListener {
//                Toast.makeText(applicationContext, "Message save successfully", Toast.LENGTH_SHORT)
//                    .show()
//            }

        return true
    }

    private fun setInitDialog(){
        val window = dialog!!.window
        setSizeDialog(window!!)
        setPositionCenterDialog(window)
        setBackgroundDialog(window)
        setCanceledOnTouchOutside(false)
    }

    private fun setSizeDialog(window: Window){
        val size = Point()
        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        val width = size.x
        window.setLayout((width * 0.85).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setPositionCenterDialog(window : Window){
        window.setGravity(Gravity.CENTER)
    }

    private fun setBackgroundDialog(window : Window){
        //window.setBackgroundDrawableResource(R.drawable.bg_popup)
    }

    private fun setCanceledOnTouchOutside( set : Boolean){
        dialog!!.setCanceledOnTouchOutside(set)
    }
}
