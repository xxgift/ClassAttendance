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
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.popup_addstudent.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class popup_addstudent_Fragment(var mView: View, var adapter: StudentlistAdapter, var coursename:String) : DialogFragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var imgEmpty: ImageView
    lateinit var studentList:ArrayList<Attendance>
    lateinit var addStudentID :String

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


        studentList = arrayListOf()
        val sdf = SimpleDateFormat("dd-MM-yy")
        val date = sdf.format(Date())

        dataReference = FirebaseDatabase.getInstance().getReference("AllCourse").child(coursename).child(date)

        var dataQuery = dataReference.orderByChild("username")
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

            //check MaterialID is not already in use
            studentList?.forEach {
                if (it.username == addStudentID) {
                    studentList.add(Attendance(it.username,it.type,it.coursename,it.date,it.starttime,it.durationtime,"Present"))
                    studentList.remove(it)
                    dataReference.setValue(studentList)
                    return false
                }else{
                    addstudent_studentID.error = "This student has not enrolled this course"
                    addstudent_studentID.text = null
                    addstudent_studentID.setHint("Enter Again")
                }
            }

            //send value to firebase

            adapter.notifyDataSetChanged()
            imgEmpty = mView.findViewById<ImageView>(R.id.img_empty_course)
            imgEmpty.visibility = View.INVISIBLE
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
