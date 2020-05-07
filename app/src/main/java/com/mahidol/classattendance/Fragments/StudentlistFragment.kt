package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_studentlist.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class

StudentlistFragment(val selectnamecourse: String,val selectjoinID: String, val date: String, val isScanning: Boolean) : Fragment() {
    lateinit var mContext: Context
    lateinit var adapter: StudentlistAdapter
    lateinit var mActivity: Activity
    lateinit var fragmentTransaction: FragmentTransaction

    lateinit var studentListValue: HashMap<String, Attendance>
    lateinit var studentList: ArrayList<Attendance>
    lateinit var whoEnroll: ArrayList<String>
    lateinit var presentList: ArrayList<String>
    lateinit var absentList: ArrayList<String>
    lateinit var total:TextView

    var dataReference =
        FirebaseDatabase.getInstance().getReference("Attendance").child("${selectnamecourse}+${selectjoinID}")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_studentlist, container, false)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
        mActivity = activity!!


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backbtn_studentlist.visibility = View.VISIBLE
        addbtn_studentlist.visibility = View.INVISIBLE
        appbar_course_studentlist.text = "Course : ${selectnamecourse}"
        appbar_date_studentlist.text = "Date : ${date}"


        if (isScanning) {
            if (currenttype == "Teacher") {
                img_empty_studentlist.setImageResource(R.mipmap.ic_emptystudentlist)
                addbtn_studentlist.visibility = View.VISIBLE
            }
        } else {
            img_empty_studentlist.setImageResource(R.mipmap.ic_nostudentinclass)
            if (currenttype == "Teacher") {
                val touchListener = SwipeToDismissTouchListener(
                    ListViewAdapter(listview_studentlist),
                    object : SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter> {
                        override fun canDismiss(position: Int): Boolean {
                            return true
                        }

                        override fun onDismiss(view: ListViewAdapter, position: Int) {
                            studentListValue.remove(studentList[position].username)
                            studentList.removeAt(position)
                            dataReference.child(date).setValue(studentListValue)
                            adapter.notifyDataSetChanged()
                        }
                    })

                listview_studentlist!!.setOnTouchListener(touchListener)
//        listview_courselist!!.setOnScrollListener(touchListener.makeScrollListener() as AbsListView.OnScrollListener)
                listview_studentlist!!.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        if (touchListener.existPendingDismisses()) {
                            touchListener.undoPendingDismiss()
                        }
                    }
            }
        }
        studentList = arrayListOf()
        studentListValue = hashMapOf()
        whoEnroll = arrayListOf()
        presentList = arrayListOf()
        absentList = arrayListOf()

        var dataQuery = dataReference.child(date).orderByChild("username")

        dataQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    studentList.clear()
                    presentList.clear()
                    absentList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Attendance::class.java)
                        studentListValue.put(oneUser!!.username, Attendance(oneUser!!.username, oneUser.type, oneUser.coursename, oneUser.date, oneUser.starttime, oneUser.durationtime, oneUser.attendance))
                        studentList.add(Attendance(oneUser!!.username, oneUser.type, oneUser.coursename, oneUser.date, oneUser.starttime, oneUser.durationtime, oneUser.attendance))
                        if (oneUser!!.attendance == "Present") {
                            presentList.add(oneUser.username)
                        }
                        if (oneUser!!.attendance == "Absent") {
                            absentList.add(oneUser.username)
                        }
                        total = view.findViewById<TextView>(R.id.appbar_total_studentlist)
                        total.text = "Present : ${presentList.size} Absent : ${absentList.size}"

                    }

                    val imgEmpty = view.findViewById<ImageView>(R.id.img_empty_studentlist)
                    if (studentList.size > 0) {
                        imgEmpty.visibility = View.INVISIBLE
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })

        adapter = StudentlistAdapter(mContext, R.layout.list_detail, studentList)
        listview_studentlist!!.adapter = adapter

        backbtn_studentlist.setOnClickListener {
            Toast.makeText(mContext, "back", LENGTH_SHORT).show()
            if (isScanning) {
                replaceFragment(AttendanceFragment())
            } else {
                replaceFragment(LogAttendanceFragment(selectnamecourse,selectjoinID))
            }
        }

        addbtn_studentlist.setOnClickListener {
            showDialog(view, adapter)
            adapter.notifyDataSetChanged()
        }

    }

    private fun showDialog(view: View, adapter: StudentlistAdapter) {
        val applypopup = popup_addstudent_Fragment(view, adapter, selectnamecourse, selectjoinID, date)
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}