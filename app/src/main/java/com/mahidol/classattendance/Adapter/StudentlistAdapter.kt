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
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.R

class StudentlistAdapter(
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
        val start = view.findViewById<TextView>(R.id.SubtitleLeft)
        val attendance = view.findViewById<TextView>(R.id.TitletxtRight)

        val ic = view.findViewById<ImageView>(R.id.list_ic)
        val imageview = view.findViewById<ImageView>(R.id.imageView_list)
        val onlinesign = view.findViewById<ImageView>(R.id.imageView2)


        val time = studentList[position]

        if (time.type == "Teacher") {
            ic.setImageResource(R.mipmap.ic_teachernew)
            imageview.setImageResource(R.drawable.rectangleteacher)
        }else{
            ic.setImageResource(R.mipmap.ic_studentnew)
        }

        if (time.attendance == "Absent") {
            attendance.setTextColor(Color.parseColor("#E0563C"))
            duration.text = ""
            username.text = "${time.username}"
            attendance.text = "${time.attendance}"
            start.text = ""
            onlinesign.setImageResource(R.drawable.offlinecircle)

        }
        if (time.attendance == "Present") {
            attendance.setTextColor(Color.parseColor("#57BE37"))
            duration.text = "${time.durationtime}"
            username.text = "${time.username}"

            if (time.starttime != "") {
                start.text = "Check in @${time.starttime}"
            } else {
                start.text = "(Manual Add)"
            }
            attendance.text = "${time.attendance}"
        }
        if (time.attendance == "Teacher") {
            attendance.setTextColor(Color.parseColor("#A254F2"))
            duration.text = "${time.durationtime}"
            username.text = "${time.username}"
            start.text = "Course started at ${time.starttime}"
            attendance.text = "${time.attendance}"
            onlinesign.visibility = View.INVISIBLE
        }


        return view
    }
}
