package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mahidol.classattendance.Adapter.ChatroomAdapter
import com.mahidol.classattendance.Helper.HTTPHelper
import com.mahidol.classattendance.Models.Post
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_chatroom.*


class ChatroomFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var postList: ArrayList<Post>
    lateinit var adapter: ChatroomAdapter
    lateinit var mActivity: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chatroom, container, false)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
        mActivity = activity!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postList = ArrayList<Post>()


        dataReference = FirebaseDatabase.getInstance().getReference("Post")
        var dataQuery = dataReference.orderByChild("timestamp")

        //add listener of search button to open navigation bar when clicked

//        search.setOnClickListener {
//            val searchView: SearchView? = null
//            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//
//                override fun onQueryTextChange(newText: String): Boolean {
//                    if (TextUtils.isEmpty(newText)) {
//                        adapter.filter("")
//                        listview_home.clearTextFilter()
//                    } else {
//                        adapter.filter(newText)
//                    }
//                    return true
//                }
//
//                override fun onQueryTextSubmit(query: String): Boolean {
//                    // task HERE
//                    return false
//                }
//            })
//        }

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
                    val imgEmpty = view.findViewById<ImageView>(R.id.img_empty_post)
                    if (postList.size > 0) {
                        imgEmpty.visibility = View.INVISIBLE
                    }
                }
                adapter.notifyDataSetChanged()

            }
        })
        adapter = ChatroomAdapter(context!!,activity!!, R.layout.list_post, postList)
        listview_chatroom!!.adapter = adapter


        btn_addpost.setOnClickListener {
            showDialog(view,adapter)
            adapter.notifyDataSetChanged()

        }


    }
    private fun showDialog(view: View,adapter: ChatroomAdapter) {
        val applypopup = popup_addpost_Fragment(view,adapter)
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }
}
