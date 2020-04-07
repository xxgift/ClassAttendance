package com.mahidol.classattendance.Adapter

import android.content.Context
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
        val logList: ArrayList<Attendance>
) :
        ArrayAdapter<Attendance>(mContext, layoutResId, logList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val coursename = view.findViewById<TextView>(R.id.TitletxtLeft)
        val date= view.findViewById<TextView>(R.id.SubtitleLeft)


        val ic = view.findViewById<ImageView>(R.id.list_ic)

        val log = logList[position]

        ic.setImageResource(R.mipmap.ic_iconcourseschedule)

        coursename.text = "${log.coursename}"
        date.text = "${log.date}"

        return view
    }
}
