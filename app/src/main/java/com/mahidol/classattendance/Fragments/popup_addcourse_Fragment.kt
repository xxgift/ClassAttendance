package com.mahidol.classattendance.Fragments

import android.content.Context

import android.graphics.Point

import android.os.Bundle
import android.view.*

import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import com.mahidol.classattendance.Models.Course

import com.mahidol.classattendance.Models.courselistdetail
import com.mahidol.classattendance.Models.currentuser
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.popup_addcourse.*

class popup_addcourse_Fragment : DialogFragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_addcourse, container, false)
    }

    override fun onStart() {
        super.onStart()
        setInitDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataReference = FirebaseDatabase.getInstance().getReference("UserProfile")



        addcourse_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }

        addcourse_addbtn.setOnClickListener {
            //save course data
            saveData()

        }
    }


    private fun saveData(): Boolean {

        val addcourseID = addcourse_courseID.text.toString()
        //check each edittext must not be null
        if (addcourseID.isEmpty()) {
            addcourse_courseID.error = "Please enter a message"
            return false
        }
        //check courseID is not already in use
        courselistdetail?.forEach {
            if (it.courseID == addcourseID) {
                addcourse_courseID.error = "ClassID is already in used"
                addcourse_courseID.text = null
                addcourse_courseID.setHint("Enter Again")
                return false
            }
        }
        //send value to firebase
        var randjoinID = ('A'..'z').map { it }.shuffled().subList(0, 4).joinToString("")
        courselistdetail!!.add(Course(addcourseID,randjoinID))
        var updateValue = dataReference.child(currentuser).child("courselist").setValue(
            courselistdetail
        )
        dialog!!.dismiss()
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
