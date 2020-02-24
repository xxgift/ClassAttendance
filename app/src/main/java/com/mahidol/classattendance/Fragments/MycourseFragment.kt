package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.mahidol.classattendance.Adapter.MycourseAdapter
import com.mahidol.classattendance.Models.courselistdetail
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.fragment_mycourse.*


class MycourseFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var adapter: MycourseAdapter
    lateinit var mActivity: Activity


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mycourse, container, false)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
        mActivity = activity!!


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MycourseAdapter(mContext, R.layout.list_detail, courselistdetail)
        listview_courselist!!.adapter = adapter
        val imgEmpty = view.findViewById<ImageView>(R.id.img_empty_course)
        if (courselistdetail.size > 0) {
             imgEmpty.visibility = View.INVISIBLE
        }
        addbtn_thiscourse.setOnClickListener {
            showDialog(view,adapter)
            adapter.notifyDataSetChanged()

        }


    }


    private fun showDialog(view: View,adapter: MycourseAdapter) {
        val applypopup = popup_addcourse_Fragment(view,adapter)
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }
}
