package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mahidol.classattendance.Adapter.LogAttendanceAdapter
import com.mahidol.classattendance.Models.Attendance
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.Models.isScanning
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_mycourse.*


class LogAttendanceFragment(val selectnamecourse: String) : Fragment() {
    lateinit var mContext: Context
    lateinit var adapter: LogAttendanceAdapter
    lateinit var mActivity: Activity
    lateinit var fragmentTransaction: FragmentTransaction

    lateinit var logList: ArrayList<String>

    var dataReference =
            FirebaseDatabase.getInstance().getReference("Attendance").child(selectnamecourse)

    var dataQuery = dataReference.orderByChild("date")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mycourse, container, false)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
        mActivity = activity!!


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backbtn_thiscourse.visibility = View.VISIBLE
        addbtn_thiscourse.visibility = View.INVISIBLE
        img_empty_course.setImageResource(R.mipmap.ic_nologattendance)

        isScanning= false

        logList = arrayListOf()

        dataQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    logList.clear()
                    for (i in p0.children) {
                        val oneUser = i.key
                        println("gggggggggggggg${oneUser!!}")
                        if (logList.any { it == oneUser }) {
                        } else {
                            logList.add(oneUser)
                        }
                    }
                    if (logList.size > 0) {
                        view.findViewById<ImageView>(R.id.img_empty_course).visibility = View.INVISIBLE
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })
        println("lllllllllllllllll${logList}")
        adapter = LogAttendanceAdapter(mContext, R.layout.list_detail, logList, selectnamecourse)
        listview_courselist!!.adapter = adapter

        backbtn_thiscourse.setOnClickListener {
            Toast.makeText(mContext, "back", LENGTH_SHORT).show()
            replaceFragment(SelectFragment(selectnamecourse))
        }

        listview_courselist!!.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->

                    Toast.makeText(
                            mContext, "Select ${logList[position]
                    }", LENGTH_SHORT
                    ).show()
                    if (currenttype == "Teacher") {
                        replaceFragment(StudentlistFragment(selectnamecourse, logList[position], false))
                    }else{
                        replaceFragment(StudentAttendanceFragment(selectnamecourse,logList[position],""))
                    }
                }

    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}