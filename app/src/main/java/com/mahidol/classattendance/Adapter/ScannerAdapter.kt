package com.mahidol.classattendance.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.mahidol.classattendance.Models.BBeacon
import com.mahidol.classattendance.Models.currentcourse
import com.mahidol.classattendance.R


class ScannerAdapter(
    val mContext: Context,
    val layoutResId: Int,
    val beaconList: ArrayList<BBeacon>
) :
    ArrayAdapter<BBeacon>(mContext, layoutResId, beaconList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)

        val beaconname = view.findViewById<TextView>(R.id.detailBeacon)
        val placename = view.findViewById<TextView>(R.id.placeName)
        val time = view.findViewById<TextView>(R.id.dateText)
        val beacon = beaconList[position]
        beaconname.text =
            "major:${beacon.major} minor:${beacon.minor} rssi: ${beacon.rssi} \ndistance: ${beacon.distance} meters"
        placename.text = "${beacon.region}"
        time.text = "${beacon.time}"
        view.setOnClickListener {
            currentcourse = beacon.region
            Toast.makeText(mContext, "Selected ${beacon.region} Place", Toast.LENGTH_SHORT).show()
        }


        return view
    }
}