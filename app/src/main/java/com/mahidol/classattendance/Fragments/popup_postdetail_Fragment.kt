package com.mahidol.classattendance.Fragments

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*

import android.widget.ImageView
import androidx.fragment.app.DialogFragment

import com.mahidol.classattendance.Adapter.ChatroomAdapter
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.popup_postdetail.*
import java.text.SimpleDateFormat
import java.util.*



class popup_postdetail_Fragment(var mView: View,var user:String,var content:String,var course:String,var date:String) : DialogFragment() {
    lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_postdetail, container, false)
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

        popup_post_username.text = "@" + user
        popup_post_content.text = " "+content
        popup_post_course.text = course
        popup_post_time.text = date

       popup_post_okbtn.setOnClickListener {
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
