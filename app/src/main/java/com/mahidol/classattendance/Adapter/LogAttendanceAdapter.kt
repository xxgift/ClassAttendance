package com.mahidol.classattendance.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mahidol.classattendance.Models.Attendance
import com.mahidol.classattendance.R

class LogAttendanceAdapter(
    val mContext: Context,
    val layoutResId: Int,
    val studentList: ArrayList<Attendance>
) :
    ArrayAdapter<Attendance>(mContext, layoutResId, studentList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val username = view.findViewById<TextView>(R.id.TitletxtLeft)
        val duration = view.findViewById<TextView>(R.id.SubtitleRight)
        val start= view.findViewById<TextView>(R.id.SubtitleLeft)
        val attendance = view.findViewById<TextView>(R.id.TitletxtRight)


        val ic = view.findViewById<ImageView>(R.id.list_ic)

        val time = studentList[position]

        ic.setImageResource(R.mipmap.ic_iconstudentlist)

        if(time.attendance=="Absent"){
            attendance.setTextColor(Color.parseColor("#7C0A02"))
        }

        duration.text = "${time.durationtime}"
        username.text = "${time.username}"
        start.text = "Check in ${time.date} @${time.starttime}"


        attendance.text = "${time.attendance}"


        return view
    }
}