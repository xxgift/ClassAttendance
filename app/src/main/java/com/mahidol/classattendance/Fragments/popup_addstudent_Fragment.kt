package com.mahidol.classattendance.Fragments

import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.popup_addcourse.*
import kotlinx.android.synthetic.main.popup_addstudent.*

class popup_addstudent_Fragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.popup_addstudent,container,false)
    }

    override fun onStart() {
        super.onStart()
        setInitDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addstudent_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }
    }

    private fun setInitDialog(){
        val window = dialog!!.window
        setSizeDialog(window!!)
        setPositionCenterDialog(window)
        setBackgroundDialog(window)
        setCanceledOnTouchOutside(false)
    }

    private fun setSizeDialog(window: Window){
        val size = Point()
        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        val width = size.x
        window.setLayout((width * 0.85).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setPositionCenterDialog(window : Window){
        window.setGravity(Gravity.CENTER)
    }

    private fun setBackgroundDialog(window : Window){
        //window.setBackgroundDrawableResource(R.drawable.bg_popup)
    }

    private fun setCanceledOnTouchOutside( set : Boolean){
        dialog!!.setCanceledOnTouchOutside(set)
    }
}
