package com.mahidol.classattendance.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mahidol.classattendance.Models.Time
import com.mahidol.classattendance.R

class AttendanceAdapter(
    val mContext: Context,
    val layoutResId: Int,
    val studentList: ArrayList<Time>
) :
    ArrayAdapter<Time>(mContext, layoutResId, studentList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val username = view.findViewById<TextView>(R.id.TitletxtLeft)
        val timeTxt = view.findViewById<TextView>(R.id.TitletxtRight)
        val durationtime= view.findViewById<TextView>(R.id.SubtitleRight)
        val starttime = view.findViewById<TextView>(R.id.SubtitleLeft)

        val time = studentList[position]
        timeTxt.text = "Time Duration"
        username.text = "${time.username}"
        durationtime.text = "${time.durationtime}"
        starttime.text = "${time.starttime}"


        return view
    }
}
