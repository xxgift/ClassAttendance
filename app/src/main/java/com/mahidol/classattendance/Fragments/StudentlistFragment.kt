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
import com.hudomju.swipe.SwipeToDismissTouchListener
import com.hudomju.swipe.adapter.ListViewAdapter
import com.mahidol.classattendance.Adapter.StudentlistAdapter
import com.mahidol.classattendance.Models.Attendance
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_mycourse.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class

StudentlistFragment (val selectnamecourse: String,val date :String) : Fragment() {
    lateinit var mContext: Context
    lateinit var adapter: StudentlistAdapter
    lateinit var mActivity: Activity
    lateinit var fragmentTransaction: FragmentTransaction

    lateinit var studentList: ArrayList<Attendance>

    var dataReference =
        FirebaseDatabase.getInstance().getReference("Attendance").child(selectnamecourse)


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
        img_empty_course.setImageResource(R.mipmap.emptylog)

        studentList = arrayListOf()

        var dataQuery = dataReference.child(date).orderByChild("username")

        dataQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    studentList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Attendance::class.java)
                        studentList.add(Attendance(oneUser!!.username,oneUser.type,oneUser.coursename,oneUser.date,oneUser.starttime,oneUser.durationtime,oneUser.attendance))
                    }
                    val imgEmpty = view.findViewById<ImageView>(R.id.img_empty_post)
                    imgEmpty.setImageResource(R.mipmap.ic_emptystudentlist)
                    if (studentList.size > 0) {
                        imgEmpty.visibility = View.INVISIBLE
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })

        adapter = StudentlistAdapter(mContext, R.layout.list_detail, studentList)
        listview_courselist!!.adapter = adapter

        backbtn_thiscourse.setOnClickListener {
            Toast.makeText(mContext, "back", LENGTH_SHORT).show()
            replaceFragment(SelectFragment(selectnamecourse))
        }

        val touchListener = SwipeToDismissTouchListener(
            ListViewAdapter(listview_courselist),
            object : SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter> {
                override fun canDismiss(position: Int): Boolean {
                    return true
                }

                override fun onDismiss(view: ListViewAdapter, position: Int) {
                    studentList.removeAt(position)
                    dataReference.setValue(studentList)
                    adapter.notifyDataSetChanged()
                }
            })

        listview_courselist!!.setOnTouchListener(touchListener)
//        listview_courselist!!.setOnScrollListener(touchListener.makeScrollListener() as AbsListView.OnScrollListener)
        listview_courselist!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss()
                } else {
                    Toast.makeText(
                        mContext, "Select ${studentList[position].username
                        }", LENGTH_SHORT
                    ).show()
                }
            }

    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}