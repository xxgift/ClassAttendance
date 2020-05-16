package com.mahidol.classattendance.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mahidol.classattendance.Models.Status
import com.mahidol.classattendance.R


class BeaconstatusAdapter(
    val mContext: Context,
    val layoutResId: Int,
    val statusList: ArrayList<Status>
) :
    ArrayAdapter<Status>(mContext, layoutResId, statusList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val attendent = view.findViewById<TextView>(R.id.msgView)

        val status = statusList[position]
        attendent.text = "${status.status}"


        return view
    }
}
