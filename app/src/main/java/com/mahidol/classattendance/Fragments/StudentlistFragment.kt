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
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_mycourse.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class

StudentlistFragment(val selectnamecourse: String, val date: String, val isScanning: Boolean) : Fragment() {
    lateinit var mContext: Context
    lateinit var adapter: StudentlistAdapter
    lateinit var mActivity: Activity
    lateinit var fragmentTransaction: FragmentTransaction

    lateinit var studentListValue: HashMap<String,Attendance>
    lateinit var studentList:ArrayList<Attendance>
    lateinit var whoEnroll:ArrayList<String>

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
        addbtn_thiscourse.setBackgroundResource(R.mipmap.btn_addstudent)

        if (isScanning) {
            backbtn_thiscourse.visibility = View.INVISIBLE
            if (currenttype=="Teacher"){
                img_empty_course.setImageResource(R.mipmap.ic_emptystudentlist)
                addbtn_thiscourse.visibility = View.VISIBLE
            }
        }else{
            img_empty_course.setImageResource(R.mipmap.ic_nostudentinclass)
        }

        if (currenttype == "Teacher") {
            val touchListener = SwipeToDismissTouchListener(
                    ListViewAdapter(listview_courselist),
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

            listview_courselist!!.setOnTouchListener(touchListener)
//        listview_courselist!!.setOnScrollListener(touchListener.makeScrollListener() as AbsListView.OnScrollListener)
            listview_courselist!!.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        if (touchListener.existPendingDismisses()) {
                            touchListener.undoPendingDismiss()
                        }
                    }
        }

        studentList = arrayListOf()
        studentListValue = hashMapOf()
        whoEnroll = arrayListOf()

        var dataQuery = dataReference.child(date).orderByChild("type")

        dataQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    studentList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Attendance::class.java)
                        studentListValue.put(oneUser!!.username,Attendance(oneUser!!.username, oneUser.type, oneUser.coursename, oneUser.date, oneUser.starttime, oneUser.durationtime, oneUser.attendance))
                        studentList.add(Attendance(oneUser!!.username, oneUser.type, oneUser.coursename, oneUser.date, oneUser.starttime, oneUser.durationtime, oneUser.attendance))
                    }
                    val imgEmpty = view.findViewById<ImageView>(R.id.img_empty_course)
                    if (studentList.size > 0) {
                        imgEmpty.visibility = View.INVISIBLE
                    }
                    studentList.reverse()
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

        addbtn_thiscourse.setOnClickListener {
            showDialog(view,adapter)
            adapter.notifyDataSetChanged()
        }

    }

    private fun showDialog(view: View, adapter: StudentlistAdapter) {
        val applypopup = popup_addstudent_Fragment(view, adapter,selectnamecourse,date)
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}