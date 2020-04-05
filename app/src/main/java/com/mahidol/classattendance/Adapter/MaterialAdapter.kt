package com.mahidol.classattendance.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mahidol.classattendance.Models.Material
import com.mahidol.classattendance.R

class MaterialAdapter(
    val mContext: Context,
    val layoutResId: Int,
    val materialList: ArrayList<Material>
) :
    ArrayAdapter<Material>(mContext, layoutResId, materialList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val materialID = view.findViewById<TextView>(R.id.TitletxtLeft)
        val coursename = view.findViewById<TextView>(R.id.SubtitleRight)
        val date = view.findViewById<TextView>(R.id.TitletxtRight)
        val link = view.findViewById<TextView>(R.id.SubtitleLeft)
        var ic = view.findViewById<ImageView>(R.id.list_ic)

        val meterial = materialList[position]

        ic.setImageResource(R.mipmap.ic_iconclassmat)

        link.text = "${meterial.link}"
        materialID.text = "${meterial.materialID}"
        date.text = "${meterial.date}"
        coursename.text = "${meterial.courseID}"

        return view
    }
}
