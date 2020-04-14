package com.mahidol.classattendance.Fragments

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mahidol.classattendance.Adapter.BoardAdapter
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.popup_addpost.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class popup_addpost_Fragment(var mView: View, var adapter: BoardAdapter) : DialogFragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var courseListinspinner: ArrayList<String>
    lateinit var selectedItem: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_addpost, container, false)
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

        post_username.text = "@" + currentuser
        post_usertype.text = currenttype
        dataReference = FirebaseDatabase.getInstance().getReference("Post")

        courseListinspinner = arrayListOf("Please select course")

        if (currenttype == "Student") {
            imagepost.setImageResource(R.mipmap.ic_studentnew)
        } else {
            imagepost.setImageResource(R.mipmap.ic_teachernew)
        }

        courselistdetail.forEach {
            courseListinspinner.add(it.key)
        }


        val adapter_spinner = ArrayAdapter(
            mContext, // Context
            android.R.layout.simple_spinner_item, // Layout
            courseListinspinner // Array
        )
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.adapter = adapter_spinner

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // Display the selected item text on text view
                selectedItem = parent.getItemAtPosition(position).toString()
                if (position > 0) {
                    currentcourse = selectedItem
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        addpost_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }

        addpost_addbtn.setOnClickListener {
            if (selectedItem == "Please select course") {
                post_content.error = "Please select course"
            } else {
                saveData()
            }
        }
    }


    private fun saveData(): Boolean {

        val user = currentuser!!
        val course = currentcourse!!
        val content = post_content.text.toString()
        val sdf = SimpleDateFormat("dd-MM-yy @HH:mm a")
        val date = sdf.format(Date())
        val tmp = SimpleDateFormat("yyMMddHHmmss")
        val timestamp = tmp.format(Date())
        val type = currenttype!!

        //check each edittext must not be null
        if (content.isEmpty()) {
            post_content.error = "Please enter a message"
            return false
        }

        //send value to firebase
        val postData = Post(user, type, course, content, date, timestamp)
        var updateValue = dataReference.child(timestamp).setValue(postData)

        adapter.notifyDataSetChanged()
        val imgEmpty = mView.findViewById<ImageView>(R.id.img_empty_post)
        imgEmpty.visibility = View.INVISIBLE
        dialog!!.dismiss()
        return true
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
