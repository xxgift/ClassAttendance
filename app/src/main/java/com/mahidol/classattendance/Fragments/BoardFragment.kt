package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.database.*
import com.mahidol.classattendance.Adapter.BoardAdapter
import com.mahidol.classattendance.Models.Post

import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_board.*
import kotlin.collections.ArrayList


class BoardFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var postList: ArrayList<Post>
    lateinit var adapter: BoardAdapter
    lateinit var mActivity: Activity
    lateinit var allCoursename:ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_board, container, false)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
        mActivity = activity!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postList = ArrayList<Post>()
        allCoursename = arrayListOf()

        dataReference = FirebaseDatabase.getInstance().getReference("Post")
        var dataQuery = dataReference.orderByChild("timestamp")

        dataQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    postList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Post::class.java)
                        postList.add(oneUser!!)
                    }
                    postList.reverse()
                    val imgEmpty = view.findViewById<ImageView>(R.id.img_empty_post)
                    if (postList.size > 0) {
                        imgEmpty.visibility = View.INVISIBLE
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })

        adapter = BoardAdapter(context!!,activity!!, R.layout.list_post, postList)
        listview_chatroom!!.adapter = adapter


        btn_addpost.setOnClickListener {
            showDialogPost(view,adapter)
            adapter.notifyDataSetChanged()

        }
        btn_sort.setOnClickListener {
            postList.forEach {
                var tmp = it.course
                if (allCoursename.any { it == tmp }) {
                } else {
                    allCoursename.add(it.course)
                }
            }
            showDialogSort(view,adapter,allCoursename,postList)
            adapter.notifyDataSetChanged()
        }


    }
    private fun showDialogPost(view: View,adapter: BoardAdapter) {
        val applypopup = popup_addpost_Fragment(view,adapter)
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }
    private fun showDialogSort(view: View, adapter: BoardAdapter, allcoursename:ArrayList<String>, postList:ArrayList<Post>) {
        val applypopup = popup_sort_Fragment(view,adapter,allcoursename,postList)
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }

}
