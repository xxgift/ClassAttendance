package com.mahidol.classattendance.Fragments

import android.content.Context

import android.graphics.Point

import android.os.Bundle
import android.view.*
import android.widget.ImageView

import androidx.fragment.app.DialogFragment
import com.google.firebase.database.*
import com.mahidol.classattendance.Adapter.MycourseAdapter

import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.popup_addcourse.*
import com.mahidol.classattendance.Models.*


class popup_addcourse_Fragment(var mView: View, var adapter: MycourseAdapter) : DialogFragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var dataReference2: DatabaseReference
    lateinit var imgEmpty: ImageView
    lateinit var addcourseID: String
    lateinit var addjoinID: String
    lateinit var whoEnrollList:ArrayList<String>
    lateinit var allcourse:HashMap<String,Course>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_addcourse, container, false)
    }

    override fun onStart() {
        super.onStart()
        setInitDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataReference = FirebaseDatabase.getInstance().getReference("UserProfile")
        dataReference2 = FirebaseDatabase.getInstance().getReference("AllCourse")

        whoEnrollList = arrayListOf()
        allcourse = hashMapOf()

        var dataQuery = dataReference2.orderByChild("courseID")
        dataQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    allcourse.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Course::class.java)
                        allcourse.put(oneUser!!.courseID,oneUser!!)
                    }
                }
//                adapter.notifyDataSetChanged()
            }
        })


        if (currenttype == "Teacher") {
            addcourse_ID.setHint("Course ID")
        } else {
            addcourse_ID.setHint("Enter Join ID")
        }

        addcourse_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }

        addcourse_addbtn.setOnClickListener {
            //save course data
            saveData()
        }
    }


    private fun saveData(): Boolean {



        if (currenttype == "Teacher") {
            addcourseID = addcourse_ID.text.toString()
            //check each edittext must not be null
            if (addcourseID.isEmpty()) {
                addcourse_ID.error = "Please enter a message"
                return false
            }


            //check courseID is not already in use
            courselistdetail?.forEach {
                if (it.key == addcourseID) {
                    addcourse_ID.error = "ClassID already exists"
                    addcourse_ID.text = null
                    addcourse_ID.setHint("Enter Again")
                    return false
                }
            }


            //send value to firebase
            var randjoinID = ('A'..'z').map { it }.shuffled().subList(0, 4).joinToString("")
            courselistdetail!!.put(addcourseID,Course(addcourseID, randjoinID, currentuser!!,"",ArrayList<String>(),
                ArrayList<Material>()
            ))
            courseList.add(Course(addcourseID, randjoinID, currentuser!!,"",ArrayList<String>(),
                ArrayList<Material>()
            ))
            allcourse.put(addcourseID,Course(addcourseID, randjoinID, currentuser!!,"",ArrayList<String>(),ArrayList<Material>()))




            dataReference.child(currentuser).child("courselist").setValue(courselistdetail)
            dataReference2.setValue(allcourse)

            adapter.notifyDataSetChanged()
            imgEmpty = mView.findViewById<ImageView>(R.id.img_empty_course)
            imgEmpty.visibility = View.INVISIBLE
            dialog!!.dismiss()

        } else {
            addjoinID = addcourse_ID.text.toString()
            //check each edittext must not be null
            if (addjoinID.isEmpty()) {
                addcourse_ID.error = "Please enter a joinID"
                return false
            }
            //check joinID is not already in use
            courselistdetail?.forEach {
                if (it.value.joinID == addjoinID) {
                    addcourse_ID.error = "This course already exists"
                    addcourse_ID.text = null
                    addcourse_ID.setHint("Enter Again")
                    return false
                }
            }

            var query = dataReference2.orderByChild("joinID").equalTo(addjoinID)
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }
                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.exists()) {
                        whoEnrollList.clear()
                        for (i in p0.children) {
                            val result = i.getValue(Course::class.java)
                            whoEnrollList.add(currentuser!!)
                            courselistdetail.put(result!!.courseID,Course(result!!.courseID, result!!.joinID,result!!.owner,"",
                                ArrayList<String>(),result.material
                            ))
                            courseList.add(Course(result!!.courseID, result!!.joinID,result!!.owner,"",
                                ArrayList<String>(),result.material
                            ))

                            dataReference.child(currentuser).child("courselist").setValue(
                                courselistdetail)
                            dataReference.child(result.owner).child("courselist").child(result.courseID).setValue(Course(result!!.courseID, result!!.joinID,result!!.owner,"",whoEnrollList,result.material
                            ))
                            dataReference2.child(result.courseID).setValue(Course(result!!.courseID, result!!.joinID,result!!.owner,"",whoEnrollList,result.material))

                        }
                        adapter.notifyDataSetChanged()
                        imgEmpty = mView.findViewById<ImageView>(R.id.img_empty_course)
                        imgEmpty.visibility = View.INVISIBLE
                        dialog!!.dismiss()
                    }else{
                        addcourse_ID.error = "JoinID not found"
                    }
                }
            })

        }

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
