package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mahidol.classattendance.Helper.HTTPHelper
import com.mahidol.classattendance.Models.Attendance
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.Models.currentuser
import com.mahidol.classattendance.Models.isScanning
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_studenattendance.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class StudentAttendanceFragment(val selectnamecourse: String, val date: String, val time: String) : Fragment() {
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
        var durationtime = view.findViewById<Chronometer>(R.id.chronometer_attendance)

        dataReference =
                FirebaseDatabase.getInstance().getReference("Attendance").child(selectnamecourse)
                        .child(date)

        backbtn_studentAttentdance.setOnClickListener {
            Toast.makeText(mContext, "back", Toast.LENGTH_SHORT).show()
            if (isScanning) {
                replaceFragment(AttendanceFragment())
            } else {
                replaceFragment(LogAttendanceFragment(selectnamecourse))
            }
        }

        durationtime.setOnChronometerTickListener {
            println("isscaninggggggggggg ${isScanning}")
            if (!isScanning) {
                durationtime.stop()
                println("stoppppppppppjjjjjjj"+durationtime.text)
//                val temp = HashMap<String, Attendance>()
//                temp.put(currentuser!!, Attendance(currentuser!!, currenttype!!, selectnamecourse!!, date, currentstarttime , durationtime, "Present"))
            }

        }

        var asyncTask = object : AsyncTask<String, String, String>() {
            override fun onPreExecute() {
                super.onPreExecute()
                if (isScanning) {
                    durationtime.base = SystemClock.elapsedRealtime()
                    durationtime.start()

                    studentList = hashMapOf()

                    var dataQuery = dataReference.orderByChild("starttime")

                    dataQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0!!.exists()) {
                                studentList.clear()
                                for (i in p0.children) {
                                    val oneUser = i.getValue(Attendance::class.java)
                                    studentList.put(oneUser!!.username, oneUser)
                                }
                            }
                            if (studentList.any { it.key == currentuser }) {
                            } else {
                                studentList.put(currentuser!!, Attendance(currentuser!!, currenttype!!, selectnamecourse, date, time, "", "Present"))
                            }
                            dataReference.setValue(studentList)
                            println("fonnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn")
                        }
                    })
                }
            }

            override fun doInBackground(vararg p0: String?): String {
                val helper = HTTPHelper()
                return helper.getHTTPData("https://studenttracking-47241.firebaseio.com/Attendance/" + selectnamecourse + "/" + date + "/" + currentuser + ".json")
            }

            override fun onPostExecute(result: String?) {
                if (result != "null") {
                    LogAttendance = Gson().fromJson(result, Attendance::class.java)
                    currentstarttime = LogAttendance.starttime
                    if (isScanning) {
                        course_studentAttendance.text = "Course: ${selectnamecourse}"
                        starttime.text = "Check in at: ${LogAttendance.starttime}"
                        attendance.text = "Present"
                        datestudentattendance.text = "Date: ${date}"
                    } else {
                        println(result)
                        course_studentAttendance.text = "Course: ${selectnamecourse}"
                        if (LogAttendance.starttime != "") {
                            starttime.text = "Check in at: ${LogAttendance.starttime}"
                        } else {
                            starttime.text = "Manual Add"
                        }
                        attendance.text = LogAttendance.attendance
                        datestudentattendance.text = "Date: ${LogAttendance.date}"
                    }
                } else {
                    if (isScanning) {
                        currentstarttime = time
                        course_studentAttendance.text = "Course: ${selectnamecourse}"
                        starttime.text = "Check in at: ${time}"
                        attendance.text = "Present"
                        datestudentattendance.text = "Date: ${date}"
                    }
                }
            }
        }
        asyncTask.execute()


    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}