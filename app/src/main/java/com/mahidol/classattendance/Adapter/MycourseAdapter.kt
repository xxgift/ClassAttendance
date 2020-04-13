package com.mahidol.classattendance.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.R


class MycourseAdapter(
    val mContext: Context,
    val layoutResId: Int,
    val courseList: ArrayList<Course>?
) :
    ArrayAdapter<Course>(mContext, layoutResId, courseList!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val courseID = view.findViewById<TextView>(R.id.TitletxtLeft)
        val joinIDtxt = view.findViewById<TextView>(R.id.TitletxtRight)
        val joinID = view.findViewById<TextView>(R.id.SubtitleRight)
        val owner = view.findViewById<TextView>(R.id.SubtitleLeft)
        val onlinesign = view.findViewById<ImageView>(R.id.imageView2)
        val course = courseList!![position]

        onlinesign.visibility = View.INVISIBLE
        joinIDtxt.text = "JoinID"
        courseID.text = "${course.courseID}"
        if (currenttype == "Student") {
            joinIDtxt.visibility = View.INVISIBLE
            joinID.visibility = View.INVISIBLE
        }
        joinID.text = "${course.joinID}"
        owner.text = "${course.owner}"

        return view
    }

}
