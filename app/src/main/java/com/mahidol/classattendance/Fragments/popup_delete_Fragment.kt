package com.mahidol.classattendance.Fragments

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter

import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.FirebaseDatabase

import com.mahidol.classattendance.Adapter.ChatroomAdapter
import com.mahidol.classattendance.Adapter.MycourseAdapter
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.popup_delete.*
import kotlinx.android.synthetic.main.popup_postdetail.*
import java.text.SimpleDateFormat
import java.util.*



class popup_delete_Fragment(var mView: View,var position:Int,var fcontext: Context) : DialogFragment() {
    lateinit var mContext: Context
    var dataReference = FirebaseDatabase.getInstance().getReference("UserProfile").child(
        currentuser).child("courselist")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_delete, container, false)
    }

    override fun onStart() {
        super.onStart()
        setInitDialog()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       delete_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }

        delete_deletebtn.setOnClickListener {
            courselistdetail.removeAt(position)
            dataReference.setValue(courselistdetail)
            ArrayAdapter<Course>(fcontext, R.layout.list_detail, courselistdetail).notifyDataSetChanged()
            println("hhhhhhhhhhh")
            dialog!!.dismiss()
        }

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
