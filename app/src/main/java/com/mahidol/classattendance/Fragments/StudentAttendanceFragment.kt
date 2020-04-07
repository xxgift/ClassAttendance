package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.mahidol.classattendance.Models.Attendance
import com.mahidol.classattendance.Models.currentuser
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_studenattendance.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StudentAttendanceFragment(val selectnamecourse: String) : Fragment() {
    lateinit var mContext: Context
    lateinit var mActivity: Activity

    lateinit var studentList: ArrayList<Attendance>
    lateinit var dataReference: DatabaseReference

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
        val tmp1 = SimpleDateFormat("dd-MM-yy")
        val date = tmp1.format(Date())


        dataReference =
                FirebaseDatabase.getInstance().getReference("Attendance").child(selectnamecourse)
                        .child(date)

        studentList = arrayListOf()

        var dataQuery = dataReference.orderByChild("starttime")

        dataQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    studentList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Attendance::class.java)
                        studentList.add(oneUser!!)
                    }
                }
            }
        })

        studentList.forEach {
            if (it.username == currentuser) {
                starttime.text = it.starttime
                attendance.text = it.attendance
                datestudentattendance.text = it.date
            }
        }


    }
}