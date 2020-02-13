package com.mahidol.classattendance.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.fragment_thiscourseteacher.*


class ThiscourseteacherFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_thiscourseteacher, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addbtn_thiscourseT.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog(){
        val applypopup =  popup_addcourse_Fragment()
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }
}
