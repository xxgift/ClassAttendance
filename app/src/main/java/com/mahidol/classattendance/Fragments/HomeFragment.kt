package com.mahidol.classattendance.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import com.mahidol.classattendance.Adapter.HomeAdapter
import com.mahidol.classattendance.Models.Checkin
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var checkinList: ArrayList<Checkin>
    lateinit var adapter: HomeAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onAttach(context!!)
        checkinList = arrayListOf()
        dataReference = FirebaseDatabase.getInstance().getReference()
        var dataQuery = dataReference.child("CheckIn").orderByChild("timestamp")

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
                    checkinList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Checkin::class.java)
                        checkinList.add(oneUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })
        adapter = HomeAdapter(mContext, R.layout.list_checkin, checkinList)
        listview_home!!.adapter = adapter


    }
}