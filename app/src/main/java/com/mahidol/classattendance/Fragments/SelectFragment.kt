package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.FirebaseDatabase
import com.mahidol.classattendance.Adapter.SelectAdapter
import com.mahidol.classattendance.Models.Select
import com.mahidol.classattendance.Models.currentuser
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_mycourse.*

class SelectFragment(val selectnamecourse: String) : Fragment() {
    lateinit var mContext: Context
    lateinit var adapter: SelectAdapter
    lateinit var mActivity: Activity
    lateinit var fragmentTransaction: FragmentTransaction


    lateinit var selectList: ArrayList<Select>

    var dataReference = FirebaseDatabase.getInstance().getReference("UserProfile").child(
        currentuser
    ).child("courselist")

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
        img_empty_course.visibility = View.INVISIBLE

        selectList = arrayListOf()

        selectList.add(Select(selectnamecourse, "Log Attendance"))
        selectList.add(Select(selectnamecourse, "Course Materials"))

        adapter = SelectAdapter(mContext, R.layout.list_detail, selectList)
        listview_courselist!!.adapter = adapter

        backbtn_thiscourse.setOnClickListener {
            Toast.makeText(mContext, "back", LENGTH_SHORT).show()
            replaceFragment(MycourseFragment())
        }

        listview_courselist!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Toast.makeText(mContext, "${selectList[position].detail}", LENGTH_SHORT).show()
                when (position) {
                    0 -> replaceFragment(LogAttendanceFragment(selectnamecourse))
                    1 -> replaceFragment(MaterialFragment(selectnamecourse))
                }
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}
