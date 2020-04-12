package com.mahidol.classattendance.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.mahidol.classattendance.Models.Select
import com.mahidol.classattendance.Models.currenttype
import com.mahidol.classattendance.R

class SelectAdapter(
    val mContext: Context,
    val layoutResId: Int,
    val selectList: ArrayList<Select>?
) :
    ArrayAdapter<Select>(mContext, layoutResId, selectList!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val titleLeft = view.findViewById<TextView>(R.id.TitletxtLeft)
        val subtitleLeft = view.findViewById<TextView>(R.id.SubtitleLeft)
        val titleRight = view.findViewById<TextView>(R.id.TitletxtRight)
        val subtitleRight = view.findViewById<TextView>(R.id.SubtitleRight)

        val ic = view.findViewById<ImageView>(R.id.list_ic)


        val select = selectList!![position]

        if(select.detail == "Log Attendance"){
            ic.setImageResource(R.mipmap.ic_log_folder)
        }else{
            ic.setImageResource(R.mipmap.ic_cm_folder)
        }



        titleRight.visibility = View.INVISIBLE
        subtitleRight.visibility = View.INVISIBLE
        titleLeft.text = "${select.name}"
        subtitleLeft.text = "${select.detail}"

        

        return view
    }

}
