package com.mahidol.classattendance.Fragments

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.*
import com.mahidol.classattendance.Adapter.BoardAdapter
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.popup_sort.*

class popup_sort_Fragment(
        var mView: View,
        var adapter: BoardAdapter,
        var allcoursename: ArrayList<String>,
        var postList: ArrayList<Post>
) : DialogFragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var courseListinspinner: ArrayList<String>
    lateinit var selectedItem: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_sort, container, false)
    }

    override fun onStart() {
        super.onStart()
        setInitDialog()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        courseListinspinner = arrayListOf("All Course")

        allcoursename.forEach {
            courseListinspinner.add(it)
        }

        val adapter_spinner = ArrayAdapter(
            mContext, // Context
            android.R.layout.simple_spinner_item, // Layout
            courseListinspinner // Array
        )
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner_sort.adapter = adapter_spinner

        spinner_sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // Display the selected item text on text view
                selectedItem = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        sort_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }

        sort_sortbtn.setOnClickListener {
            if (selectedItem == "All Course") {
            } else {
                currentcourse = selectedItem
                postList.sortByDescending { it.course == currentcourse }
            }
            adapter.notifyDataSetChanged()
            dialog!!.dismiss()
        }
    }

    private fun setInitDialog() {
        val window = dialog!!.window
        setSizeDialog(window!!)
        setPositionCenterDialog(window)
        setBackgroundDialog(window)
        setCanceledOnTouchOutside(false)
    }

    private fun setSizeDialog(window: Window) {
        val size = Point()
        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        val width = size.x
        window.setLayout((width * 0.85).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setPositionCenterDialog(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    private fun setBackgroundDialog(window: Window) {
        //window.setBackgroundDrawableResource(R.drawable.bg_popup)
    }

    private fun setCanceledOnTouchOutside(set: Boolean) {
        dialog!!.setCanceledOnTouchOutside(set)
    }
}
