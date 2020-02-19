package com.mahidol.classattendance.Fragments

import android.content.Context

import android.graphics.Point
import android.os.AsyncTask

import android.os.Bundle
import android.view.*
import android.widget.ImageView

import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mahidol.classattendance.Adapter.MycourseAdapter
import com.mahidol.classattendance.Helper.HTTPHelper

import com.mahidol.classattendance.Models.Course

import com.mahidol.classattendance.Models.courselistdetail
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.Models.currentuser
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.popup_addcourse.*

class popup_addcourse_Fragment(var mView: View, var adapter: MycourseAdapter) : DialogFragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var imgEmpty: ImageView
    lateinit var addcourseID:String
    lateinit var addjoinID:String

    var url = "https://studenttracking-47241.firebaseio.com/UserProfile/"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_addcourse, container, false)
    }

    override fun onStart() {
        super.onStart()
        setInitDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataReference = FirebaseDatabase.getInstance().getReference("UserProfile")


        if (currenttype=="Teacher"){
            addcourse_ID.setHint("Course ID")
        }else{
            addcourse_ID.setHint("Enter Join ID")
        }

        addcourse_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }

        addcourse_addbtn.setOnClickListener {
            //save course data
            saveData()
        }
    }


    private fun saveData(): Boolean {

        if (currenttype=="Teacher"){
            addcourseID = addcourse_ID.text.toString()
            //check each edittext must not be null
            if (addcourseID.isEmpty()) {
                addcourse_ID.error = "Please enter a message"
                return false
            }



          //check courseID is not already in use
            courselistdetail?.forEach {
                if (it.courseID == addcourseID) {
                    addcourse_ID.error = "ClassID is already in used"
                    addcourse_ID.text = null
                    addcourse_ID.setHint("Enter Again")
                    return false
                }
            }

            //send value to firebase
            var randjoinID = ('A'..'z').map { it }.shuffled().subList(0, 4).joinToString("")
            courselistdetail!!.add(Course(addcourseID, randjoinID))

            courselistdetail.forEach {
                val key = it.courseID
                dataReference.child(currentuser).child("courselist").child(key).setValue(it)
            }


        }else{
            addjoinID = addcourse_ID.text.toString()
            //check each edittext must not be null
            if (addjoinID.isEmpty()) {
                addcourse_ID.error = "Please enter a message"
                return false
            }
//            var asyncTask = object : AsyncTask<String, String, String>() {
//
//                override fun onPreExecute() {
//                }
//
//                override fun doInBackground(vararg p0: String?): String {
//                    val helper = HTTPHelper()
//                    return helper.getHTTPData(url + ".json")
//                }

//                override fun onPostExecute(result: String?) {
//                    if (result != "null") {
//                        userprofile = Gson().fromJson(result, User::class.java)
//                        courselistdetail = userprofile!!.courselist
//
//                        //check username and password is matched
//                        if (userprofile!!.password == pname) {
//                            if(userprofile!!.type == "Student"){
//                                val intent = Intent(this@LoginActivity, BodystudentActivity::class.java)
//                                //transfer value of username to scan
//                                intent.putExtra("uname", uname)
//                                applicationContext.startActivity(intent)
//                                currenttype = userprofile!!.type
//                            }else{
//                                val intent = Intent(this@LoginActivity, BodyActivity::class.java)
//                                //transfer value of username to scan
//                                intent.putExtra("uname", uname)
//                                applicationContext.startActivity(intent)
//                                currenttype = userprofile!!.type
//                            }
//                        } else {
//                            Toast.makeText(
//                                this@LoginActivity,
//                                "Username or Password is not matched",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                    } else {
//                        Toast.makeText(
//                            this@LoginActivity,
//                            "Username or Password is not matched",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    login_username.text = null
//                    login_password.text = null
//                }
//
//            }
//            asyncTask.execute()

        }





        adapter.notifyDataSetChanged()
        imgEmpty = mView.findViewById<ImageView>(R.id.img_empty_course)
        imgEmpty.visibility = View.INVISIBLE
        dialog!!.dismiss()
        return true
    }


    private fun setInitDialog() {
        val window = dialog!!.window
        setSizeDialog(window!!)
        setPositionCenterDialog(window)
        setBackgroundDialog(window)
        setCanceledOnTouchOutside(false)
    }

    private fun setSizeDialog(window: Window) {
        val size = Point()
        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        val width = size.x
        window.setLayout((width * 0.85).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setPositionCenterDialog(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    private fun setBackgroundDialog(window: Window) {
        //window.setBackgroundDrawableResource(R.drawable.bg_popup)
    }

    private fun setCanceledOnTouchOutside(set: Boolean) {
        dialog!!.setCanceledOnTouchOutside(set)
    }
}
