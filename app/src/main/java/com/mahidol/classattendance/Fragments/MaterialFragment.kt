package com.mahidol.classattendance.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hudomju.swipe.SwipeToDismissTouchListener
import com.hudomju.swipe.adapter.ListViewAdapter
import com.mahidol.classattendance.Adapter.MaterialAdapter
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_mycourse.*

class MaterialFragment(val selectnamecourse: String,val selectjoinID: String) : Fragment() {
    lateinit var mContext: Context
    lateinit var adapter: MaterialAdapter
    lateinit var mActivity: Activity
    lateinit var fragmentTransaction: FragmentTransaction

    lateinit var materialList: ArrayList<Material>

    var dataReference =
        FirebaseDatabase.getInstance().getReference("AllCourse").child(selectnamecourse)
            .child("courseMaterial")
    var dataReference2 = FirebaseDatabase.getInstance().getReference("UserProfile").child(currentuser).child("courselist").child(selectnamecourse).child("courseMaterial")
    var dataQuery = dataReference.orderByChild("timestamp")

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

        val imgEmpty = view.findViewById<ImageView>(R.id.img_empty_course)

        backbtn_thiscourse.visibility = View.VISIBLE
        addbtn_thiscourse.setBackgroundResource(R.mipmap.add_btn)



        materialList = arrayListOf()

        dataQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    materialList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Material::class.java)
                        if(selectjoinID==oneUser!!.joinID){
                        materialList.add(oneUser!!)}
                    }
                    materialList.reverse()

                    if (materialList.size > 0) {
                        imgEmpty.visibility = View.INVISIBLE
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })


        adapter = MaterialAdapter(mContext, R.layout.list_detail, materialList)
        listview_courselist!!.adapter = adapter

        addbtn_thiscourse.setOnClickListener {
            showDialog(view, adapter, selectnamecourse,selectjoinID)
            adapter.notifyDataSetChanged()
        }

        backbtn_thiscourse.setOnClickListener {
//            Toast.makeText(mContext, "back", LENGTH_SHORT).show()
            replaceFragment(SelectFragment(selectnamecourse,selectjoinID))
        }

        if (currenttype == "Teacher") {
            imgEmpty.setImageResource(R.mipmap.ic_emptycm_new)
            addbtn_thiscourse.visibility = View.VISIBLE

            val touchListener = SwipeToDismissTouchListener(
                    ListViewAdapter(listview_courselist),
                    object : SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter> {
                        override fun canDismiss(position: Int): Boolean {
                            return true
                        }

                        override fun onDismiss(view: ListViewAdapter, position: Int) {
                            materialList.removeAt(position)
                            dataReference.setValue(materialList)
                            dataReference2.setValue(materialList)
                            adapter.notifyDataSetChanged()

                        }
                    })

            listview_courselist!!.setOnTouchListener(touchListener)
            listview_courselist!!.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        if (touchListener.existPendingDismisses()) {
                            touchListener.undoPendingDismiss()
                        } else {
                            Toast.makeText(
                                    mContext, "Select ${materialList[position].materialID
                            }", LENGTH_SHORT
                            ).show()
                            val browserIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(materialList[position].link))
                            mContext.startActivity(browserIntent)
                        }
                    }

        } else {
            imgEmpty.setImageResource(R.mipmap.ic_emptycmstudent)
            addbtn_thiscourse.visibility = View.INVISIBLE
            listview_courselist!!.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                            Toast.makeText(
                                    mContext, "Select ${materialList[position].materialID
                            }", LENGTH_SHORT
                            ).show()
                            val browserIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(materialList[position].link))
                            mContext.startActivity(browserIntent)

                    }
        }



    }


    private fun showDialog(view: View, adapter: MaterialAdapter, coursename: String,joinID: String) {
        val applypopup = popup_addcm_Fragment(view, adapter, coursename,joinID)
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}