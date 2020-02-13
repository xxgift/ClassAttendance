package com.mahidol.classattendance.Fragments



import android.graphics.Color
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor


import com.google.firebase.database.*
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_checkin.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckinFragment : Fragment() {

    lateinit var dataReference: DatabaseReference
    lateinit var checkinList: ArrayList<Checkin>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_checkin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkin_username.text = "@" + currentuser
        checkinList = arrayListOf()
        dataReference = FirebaseDatabase.getInstance().getReference("CheckIn")

        if (currenttype == "Student") {
            println("#########################    $currenttype   ####################")
            imageView2.setImageResource(R.mipmap.ic_studentlove)
            imageView4.setImageResource(R.mipmap.ic_studentavatar)
            checkinBtn.setBackgroundResource(R.drawable.roundrectangleforstudent)

        } else {
            imageView2.setImageResource(R.mipmap.ic_teacherlove)
            imageView4.setImageResource(R.mipmap.ic_teacheravatar)
            checkinBtn.setBackgroundResource(R.drawable.roundrectangleforteacher)

        }

        if (currentbeacon == null) {
            checkin_beaconname.setTextColor(Color.RED)
            checkin_beaconname.text = "Shake to select your location"
            checkin_content.error = "Please select a place in Scanner Tab first"
        } else {
            checkin_beaconname.text = currentbeacon
            dataReference.addValueEventListener(object : ValueEventListener {
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
                }
            })
        }

        checkinBtn.setOnClickListener {

            if (currentbeacon == null) {
                checkin_content.error = "Please select a place in Scanner Tab first"
            } else {
                checkin_beaconname.text = currentbeacon
                saveData()
                checkin_content.text = null
                checkin_beaconname.setTextColor(Color.RED)
                checkin_beaconname.text = "Shake to select your location"
                currentbeacon = null
            }
        }
    }

    private fun saveData() {

        val user = currentuser!!
        val beacon = currentbeacon!!
        val content = checkin_content.text.toString()
        val sdf = SimpleDateFormat("dd-MM-yy @HH:mm a")
        val date = sdf.format(Date())
        val tmp = SimpleDateFormat("ddMMyyHHmmss")
        val timestamp = tmp.format(Date())
        val type = currenttype!!

        if (content.isEmpty()) {
            checkin_content.error = "Please enter a message"
            return
        }

        val checkinData = Checkin(user,type, beacon, content, date, timestamp)
        dataReference.child(user).setValue(checkinData)
            .addOnCompleteListener {
                Toast.makeText(context, "Post successfully", Toast.LENGTH_SHORT)
                    .show()
            }

        currentcontent = content
        currenttime = date
        return
    }
}