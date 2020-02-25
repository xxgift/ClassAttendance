package com.mahidol.classattendance.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.R


class MycourseAdapter (
    val mContext: Context,
    val layoutResId: Int,
    val courseList: ArrayList<Course>?
) :
    ArrayAdapter<Course>(mContext, layoutResId, courseList!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val courseID = view.findViewById<TextView>(R.id.Titletxt)
        val joinIDtxt = view.findViewById<TextView>(R.id.Subtitletxt)
        val joinID = view.findViewById<TextView>(R.id.SubSubtitletxt)
        val owner = view.findViewById<TextView>(R.id.ownerCoursetxt)
        val course = courseList!![position]
        joinIDtxt.text = "JoinID"
        courseID.text = "${course.courseID}"
        joinID.text = "${course.joinID}"
        owner.text = "${course.owner}"
        view.setOnClickListener {
            Toast.makeText(mContext, "Selected", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}
