package com.mahidol.classattendance.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mahidol.classattendance.Adapter.UserAdapter
import com.mahidol.classattendance.Models.User
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.fragment_studentinclass.*

class StudentinclassFragment : Fragment(){
    lateinit var studentlist: List<User>
    lateinit var mContext: Context
    lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_studentinclass, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addbtn_studentinclass.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog(){
        val applypopup =  popup_addstudent_Fragment()
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }
}
