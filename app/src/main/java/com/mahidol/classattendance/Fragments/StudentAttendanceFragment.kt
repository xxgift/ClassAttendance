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
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_studenattendance.*


class StudentAttendanceFragment(val selectnamecourse: String, val selectjoinID: String, val date: String, val time: String) : Fragment() {
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
            FirebaseDatabase.getInstance().getReference("Attendance").child("${selectnamecourse}+${selectjoinID}")
                .child(date)

        backbtn_studentAttentdance.setOnClickListener {
            Toast.makeText(mContext, "back", Toast.LENGTH_SHORT).show()
            if (isScanning) {
                replaceFragment(AttendanceFragment())
            } else {
                replaceFragment(LogAttendanceFragment(selectnamecourse, selectjoinID))
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
                return helper.getHTTPData("https://studenttracking-47241.firebaseio.com/Attendance/" + selectnamecourse + "+" + selectjoinID + "/" + date + "/" + currentuser + ".json")
            }

            override fun onPostExecute(result: String?) {
                if (result != "null") {
                    LogAttendance = Gson().fromJson(result, Attendance::class.java)
                    if (isScanning) {
                        course_studentAttendance.text = "Course : ${selectnamecourse}"
                        starttime.text = "Check in at : ${LogAttendance.starttime}"
                        attendance.text = "Present"
                        durationtime.text = LogAttendance.durationtime
                        datestudentattendance.text = "Date : ${date}"
                    } else {
                        println(result)
                        course_studentAttendance.text = "Course : ${selectnamecourse}"
                        if (LogAttendance.attendance == "Present") {
                            starttime.text = "Check in at : ${LogAttendance.starttime}"
                            durationtime.text = LogAttendance.durationtime
                            if (LogAttendance.starttime == "") {
                                starttime.text = "Manual Add"
                            }
                        }
                        if (LogAttendance.attendance == "Absent") {
                            durationtime.setText("00:00:00")
                        }
                        attendance.text = LogAttendance.attendance
                        datestudentattendance.text = "Date : ${LogAttendance.date}"
                    }

                    if (LogAttendance.durationtime != "") {
                        var array = LogAttendance.durationtime.split(":")
                        println("hellooo${array}")
                        var olddurationtime = array[0].toLong() * 60 * 60 * 1000 + array[1].toLong() * 60 * 1000 + array[2].toLong() * 1000
                        currentdurationtime = SystemClock.elapsedRealtime() - olddurationtime
                        println("olddddddddddddduration${olddurationtime} current ${currentdurationtime}")
                    }

                } else {
                    if (isScanning) {
                        course_studentAttendance.text = "Course : ${selectnamecourse}"
                        starttime.text = "Check in at : ${time}"
                        attendance.text = "Present"
                        datestudentattendance.text = "Date : ${date}"
                        println("notfoundddduration ${durationtime.base} ${currentdurationtime}")
                    }
                }
            }
        }
        asyncTask.execute()

        durationtime.setOnChronometerTickListener {
            println("isscaninggggggggggg ${isScanning}")
            var time: Long = 0

            if (currentdurationtime == 0.toLong()) {
                time = SystemClock.elapsedRealtime() - durationtime.base
            } else {
                time = SystemClock.elapsedRealtime() - currentdurationtime
            }
            var h = time / 3600000
            var m = (time - h * 3600000) / 60000
            var s = (time - h * 3600000 - m * 60000) / 1000
            var hh = ""
            var mm = ""
            var ss = ""
            if (h < 10) {
                hh = "0" + h.toString()
            } else {
                hh = h.toString()
            }
            if (m < 10) {
                mm = "0" + m.toString()
            } else {
                mm = m.toString()
            }
            if (s < 10) {
                ss = "0" + s.toString()
            } else {
                ss = s.toString()
            }
            durationtime.text = hh + ":" + mm + ":" + ss

            val childUpdates = HashMap<String, Any>()
            childUpdates.put(currentuser + "/durationtime/", durationtime.text.toString())
            dataReference.updateChildren(childUpdates)
            if (!isScanning) {
                durationtime.stop()
                println("stoppppppppppjjjjjjj" + durationtime.text)
            }
        }


    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}