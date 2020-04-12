package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mahidol.classattendance.Helper.HTTPHelper
import com.mahidol.classattendance.Models.Attendance
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.Models.currentuser
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_studenattendance.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class StudentAttendanceFragment(val selectnamecourse: String, val date: String, val isScanning: Boolean) : Fragment() {
    lateinit var mContext: Context
    lateinit var mActivity: Activity

    lateinit var studentList: HashMap<String, Attendance>
    lateinit var dataReference: DatabaseReference
    lateinit var fragmentTransaction: FragmentTransaction
    lateinit var LogAttendance: Attendance


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_studenattendance, container, false)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
        mActivity = activity!!


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tmp = SimpleDateFormat("HH:mm:ss")
        val time = tmp.format(Date())

        backbtn_studentAttentdance.visibility = View.INVISIBLE
        dataReference =
                FirebaseDatabase.getInstance().getReference("Attendance").child(selectnamecourse)
                        .child(date)

        backbtn_studentAttentdance.setOnClickListener {
            Toast.makeText(mContext, "back", Toast.LENGTH_SHORT).show()
            replaceFragment(LogAttendanceFragment(selectnamecourse))
        }

        if (isScanning) {

            studentList = hashMapOf()

            var dataQuery = dataReference.orderByChild("starttime")

            dataQuery.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0!!.exists()) {
                        studentList.clear()
                        if (studentList.any { it.key == currentuser }) {
                        } else {
                            studentList.put(currentuser!!, Attendance(currentuser!!, currenttype!!, selectnamecourse, date, time, "", "Present"))
                        }
                        for (i in p0.children) {
                            val oneUser = i.getValue(Attendance::class.java)
                            if (studentList.any { it.key == oneUser!!.username }) {
                            } else {
                                studentList.put(oneUser!!.username, oneUser)
                            }
                        }
                    } else {
                        if (studentList.any { it.key == currentuser }) {
                        } else {
                            studentList.put(currentuser!!, Attendance(currentuser!!, currenttype!!, selectnamecourse, date, time, "", "Present"))
                        }
                    }
                    dataReference.setValue(studentList)
                    println("fonnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn")
                }

            })
            course_studentAttendance.text = "Course: ${selectnamecourse}"
            starttime.text = "Check in at: ${time}"
            attendance.text = "Present"
            datestudentattendance.text = "Date: ${date}"
        } else {
            var asyncTask = object : AsyncTask<String, String, String>() {
                override fun doInBackground(vararg p0: String?): String {
                    val helper = HTTPHelper()
                    return helper.getHTTPData("https://studenttracking-47241.firebaseio.com/Attendance/" + selectnamecourse + "/" + date + "/" + currentuser + ".json")
                }

                override fun onPostExecute(result: String?) {
                    if (result != "null") {
                        println(result)
                        LogAttendance = Gson().fromJson(result, Attendance::class.java)
                        backbtn_studentAttentdance.visibility = View.VISIBLE
                        course_studentAttendance.text = "Course: ${selectnamecourse}"
                        if (LogAttendance.starttime != "") {
                            starttime.text = "Check in at: ${LogAttendance.starttime}"
                        }else{
                            starttime.text = "Manual Add"
                        }
                        attendance.text = LogAttendance.attendance
                        datestudentattendance.text = "Date: ${LogAttendance.date}"
                    }
                }
            }
            asyncTask.execute()

        }


    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}