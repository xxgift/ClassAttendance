package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.hudomju.swipe.SwipeToDismissTouchListener
import com.hudomju.swipe.adapter.ListViewAdapter
import com.mahidol.classattendance.Adapter.MycourseAdapter
import com.mahidol.classattendance.Models.Course
import com.mahidol.classattendance.Models.courselistdetail
import com.mahidol.classattendance.Models.currentuser
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.fragment_mycourse.*


class MycourseFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var adapter: MycourseAdapter
    lateinit var mActivity: Activity

    var dataReference = FirebaseDatabase.getInstance().getReference("UserProfile").child(
        currentuser
    ).child("courselist")

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
            showDialog(view, adapter)
            adapter.notifyDataSetChanged()

        }

        val touchListener = SwipeToDismissTouchListener(
            ListViewAdapter(listview_courselist),
            object : SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter> {
                override fun canDismiss(position: Int): Boolean {
                    return true
                }

                override fun onDismiss(view: ListViewAdapter, position: Int) {
                    courselistdetail.removeAt(position)
                    dataReference.setValue(courselistdetail)
                    adapter.notifyDataSetChanged()

                }
            })

        listview_courselist!!.setOnTouchListener(touchListener)
//        listview_courselist!!.setOnScrollListener(touchListener.makeScrollListener() as AbsListView.OnScrollListener)
        listview_courselist!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss()
                } else {
                    Toast.makeText(mContext, "Select ${courselistdetail[position].courseID
                    }", LENGTH_SHORT).show()
                }
            }

    }


    private fun showDialog(view: View, adapter: MycourseAdapter) {
        val applypopup = popup_addcourse_Fragment(view, adapter)
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }
}
