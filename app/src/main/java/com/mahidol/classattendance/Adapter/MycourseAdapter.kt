package com.mahidol.classattendance.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.FirebaseDatabase
import com.mahidol.classattendance.Fragments.popup_delete_Fragment
import com.mahidol.classattendance.Fragments.popup_postdetail_Fragment
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.Models.courselistdetail
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.Models.currentuser
import com.mahidol.classattendance.R


class MycourseAdapter (
    val mContext: Context,
    val mActivity: FragmentActivity,
    val layoutResId: Int,
    val courseList: ArrayList<Course>?
) :
    ArrayAdapter<Course>(mContext, layoutResId, courseList!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var dataReference = FirebaseDatabase.getInstance().getReference("UserProfile").child(
            currentuser).child("courselist")

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val courseID = view.findViewById<TextView>(R.id.Titletxt)
        val joinIDtxt = view.findViewById<TextView>(R.id.Subtitletxt)
        val joinID = view.findViewById<TextView>(R.id.SubSubtitletxt)
        val owner = view.findViewById<TextView>(R.id.ownerCoursetxt)
        val course = courseList!![position]
        joinIDtxt.text = "JoinID"
        courseID.text = "${course.courseID}"
        if(currenttype == "Student"){
            joinIDtxt.visibility = View.INVISIBLE
          joinID.visibility = View.INVISIBLE
        }
        joinID.text = "${course.joinID}"
        owner.text = "${course.owner}"
        view.setOnClickListener {
//            Toast.makeText(mContext, "Selected", Toast.LENGTH_SHORT).show()
        }
        view.setOnLongClickListener {

            view.animate().setDuration(500).alpha(0f).withEndAction {
                courselistdetail.removeAt(position)
                dataReference.setValue(courselistdetail)
                notifyDataSetChanged()
                view.alpha = 1.0F
                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show()
            }
            return@setOnLongClickListener true
        }

//        view.setOnLongClickListener {
//            showDialog(view,position,mContext)
//            notifyDataSetChanged()
//            return@setOnLongClickListener true
//        }
        return view
    }

    private fun showDialog(view: View,position: Int,fcontext:Context) {
        val applypopup = popup_delete_Fragment(view,position,fcontext)
        applypopup.show(mActivity!!.supportFragmentManager, "exampleBottomSheet")
    }
}
