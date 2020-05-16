package com.mahidol.classattendance.Fragments

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.*
import com.mahidol.classattendance.Adapter.MaterialAdapter
import com.mahidol.classattendance.Models.Material
import com.mahidol.classattendance.Models.courselistdetail
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.Models.currentuser
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.popup_addcoursematerial.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class popup_addcm_Fragment(var mView: View, var adapter: MaterialAdapter,var coursename:String,var joinID: String) : DialogFragment() {
    lateinit var mContext: Context
    lateinit var dataReference: DatabaseReference
    lateinit var dataReference2: DatabaseReference
    lateinit var imgEmpty: ImageView
    lateinit var materialList:ArrayList<Material>
    lateinit var addMaterialID :String
    lateinit var addLink:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_addcoursematerial, container, false)
    }

    override fun onStart() {
        super.onStart()
        setInitDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addcm_coursename.text = coursename

        dataReference = FirebaseDatabase.getInstance().getReference("AllCourse").child(coursename).child("courseMaterial")
        dataReference2 = FirebaseDatabase.getInstance().getReference("UserProfile").child(currentuser).child("courselist").child(coursename).child("courseMaterial")


        materialList = arrayListOf()

        var dataQuery = dataReference.orderByChild("date")
        dataQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    materialList.clear()
                    for (i in p0.children) {
                        val oneUser = i.getValue(Material::class.java)
                        materialList.add(oneUser!!)
                    }
                    materialList.reverse()
                }
                adapter.notifyDataSetChanged()
            }
        })


        addcm_cancelbtn.setOnClickListener {
            dialog!!.dismiss()
        }

        addcm_addbtn.setOnClickListener {
            //save course data
            saveData()
        }
    }


    private fun saveData(): Boolean {

        if (currenttype == "Teacher") {
            addMaterialID = addcm_materialID.text.toString()
            addLink = addcm_materialLink.text.toString()
            //check each edittext must not be null
            if (addMaterialID.isEmpty()) {
                addcm_materialID.error = "Please enter a message"
                return false
            }
            if (addLink.isEmpty()) {
                addcm_materialLink.error = "Please enter a message"
                return false
            }

            //check MaterialID is not already in use
            materialList?.forEach {
                if (it.materialID == addMaterialID) {
                    addcm_materialID.error = "MaterialID already exists"
                    addcm_materialID.text = null
                    addcm_materialID.setHint("Enter Again")
                    return false
                }
            }

            val sdf = SimpleDateFormat("dd-MM-yy @HH:mm a")
            val date = sdf.format(Date())
            val tmp = SimpleDateFormat("yyMMddHHmmss")
            val timestamp = tmp.format(Date())

            //send value to firebase

            courselistdetail[coursename]!!.courseMaterial.add(Material(coursename,joinID,addMaterialID,date,addLink,timestamp))
            materialList.add(Material(coursename,joinID,addMaterialID,date,addLink,timestamp))

            dataReference.setValue(materialList)
            dataReference2.setValue(materialList)

            adapter.notifyDataSetChanged()
            imgEmpty = mView.findViewById<ImageView>(R.id.img_empty_course)
            imgEmpty.visibility = View.INVISIBLE
            dialog!!.dismiss()

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
