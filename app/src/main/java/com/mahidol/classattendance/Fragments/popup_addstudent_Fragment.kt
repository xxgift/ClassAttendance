package com.mahidol.classattendance.Fragments

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.*
import com.mahidol.classattendance.Adapter.StudentlistAdapter
import com.mahidol.classattendance.Models.Attendance
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.popup_addstudent.*
import java.lang.ref.PhantomReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class popup_addstudent_Fragment(var mView: View, var adapter: StudentlistAdapter, var coursename: String,val joinID: String, val date: String) : DialogFragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var dataReference2: DatabaseReference
    lateinit var imgEmpty: ImageView
    lateinit var studentAttendaceList: HashMap<String, Attendance>
    lateinit var whoEnroll: ArrayList<String>
    lateinit var addStudentID: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_addstudent, container, false)
    }

    override fun onStart() {
        super.onStart()
        setInitDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        studentAttendaceList = hashMapOf()
        whoEnroll = arrayListOf()

        dataReference = FirebaseDatabase.getInstance().getReference("Attendance").child("${coursename}+${joinID}").child(date)
        dataReference2 = FirebaseDatabase.getInstance().getReference("AllCourse")

        var dataQuery = dataReference.orderByChild("starttime")
        dataQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    studentAttendaceList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Attendance::class.java)
                        studentAttendaceList.put(oneUser!!.username, oneUser)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })

        dataReference2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    whoEnroll.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Course::class.java)
                        if (oneUser!!.courseID == coursename) {
                            whoEnroll = oneUser!!.whoEnroll
                            println("whooooooooooooooooooo${whoEnroll}")
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })


        addstudent_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }

        addstudent_addbtn.setOnClickListener {
            //save course data
            saveData()
        }
    }


    private fun saveData(): Boolean {

        if (currenttype == "Teacher") {
            addStudentID = addstudent_studentID.text.toString()
            //check each edittext must not be null
            if (addStudentID.isEmpty()) {
                addstudent_studentID.error = "Please enter a message"
                return false
            }

            //check this studentID has enrolled this course

            if (whoEnroll.any { it == addStudentID }) {
                if (studentAttendaceList.any { it.key == addStudentID }) {
                    addstudent_studentID.error = "This student already in class"
                    return false
                } else {
                    studentAttendaceList.put(addStudentID, Attendance(addStudentID, "Student", coursename, date,
                            "", "", "Present"))
                }
            } else {
                println("notwhoooooooooooooo")
                addstudent_studentID.error = "This student has not enrolled this course"
                addstudent_studentID.text = null
                addstudent_studentID.setHint("Enter Again")
                return false
            }


            //send value to firebase
            dataReference.setValue(studentAttendaceList)

            adapter.notifyDataSetChanged()
            dialog!!.dismiss()

        }

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
