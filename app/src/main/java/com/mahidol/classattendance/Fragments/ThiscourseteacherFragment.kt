package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.mahidol.classattendance.Adapter.CourseAdapter
import com.mahidol.classattendance.Models.courselistdetail
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.fragment_thiscourseteacher.*


class ThiscourseteacherFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var adapter: CourseAdapter
    lateinit var mActivity: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_thiscourseteacher, container, false)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
        mActivity = activity!!


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgEmpty = view!!.findViewById<ImageView>(R.id.img_empty2)

        if ( courselistdetail!![0].courseID =="" && courselistdetail!![0].joinID=="") {
            courselistdetail!!.removeAt(0)
        }

        if (courselistdetail!!.size >= 1) {
            imgEmpty.visibility = ImageView.INVISIBLE
        }

        adapter = CourseAdapter(mContext, R.layout.list_detail, courselistdetail)
        listview_courselist!!.adapter = adapter

        addbtn_thiscourseT.setOnClickListener {
            showDialog()

            if (courselistdetail!!.size >= 1) {
                imgEmpty.visibility = ImageView.INVISIBLE
            }
            adapter.notifyDataSetChanged()
        }


    }


    private fun showDialog() {
        val applypopup = popup_addcourse_Fragment()
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }
}
