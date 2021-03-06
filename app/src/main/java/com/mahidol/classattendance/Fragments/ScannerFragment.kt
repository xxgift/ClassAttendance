package com.mahidol.classattendance.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.estimote.coresdk.observation.region.beacon.BeaconRegion
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.service.BeaconManager
import com.mahidol.classattendance.Adapter.BeaconstatusAdapter
import com.mahidol.classattendance.Adapter.MycourseAdapter
import com.mahidol.classattendance.Adapter.ScannerAdapter
import com.mahidol.classattendance.Models.IBeacon
import com.mahidol.classattendance.Models.Status
import com.mahidol.classattendance.Models.currentstatus
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.fragment_scanner.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow


class ScannerFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var beaconList: ArrayList<IBeacon>
    lateinit var statusList: ArrayList<Status>
    private var beaconManager: BeaconManager? = null
    private var region: BeaconRegion? = null
    lateinit var adaptertop: ScannerAdapter
    lateinit var adapterbottom: BeaconstatusAdapter
    lateinit var adapter: MycourseAdapter
    lateinit var mActivity: Activity
    var count = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Toast.makeText(mContext, "scanning...", Toast.LENGTH_LONG).show()
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
        mActivity = activity!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        beaconList = arrayListOf()
        statusList = arrayListOf()

        adaptertop = ScannerAdapter(mContext, R.layout.list_beacon, beaconList)
        listview_scanner!!.adapter = adaptertop

        adapterbottom = BeaconstatusAdapter(mContext, R.layout.row_course, statusList)
        listview_detail!!.adapter = adapterbottom

        beaconManager = BeaconManager(context)

        beaconManager!!.setLocationListener {
            Toast.makeText(mContext, it.size, Toast.LENGTH_SHORT).show()
        }
        region = BeaconRegion(
            "region",
            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null
        )

        setConnect()

        beaconManager!!.setForegroundScanPeriod(3000, 0)
        beaconManager!!.setBackgroundScanPeriod(3000, 0)

        beaconManager!!.setRangingListener(BeaconManager.BeaconRangingListener { beaconRegion, beacons ->


            if (beacons!!.isNotEmpty()) {
                val nearestBeacon = beacons[0]

                adapterbottom.notifyDataSetChanged()
                adaptertop.notifyDataSetChanged()
                currentstatus = findBeacon(nearestBeacon)
                if (currentstatus == "in class") {
                    Toast.makeText(mContext, currentstatus, Toast.LENGTH_SHORT).show()
                }
                if (currentstatus == "out of class") {
                    Toast.makeText(mContext, currentstatus, Toast.LENGTH_SHORT).show()
                }
                if (currentstatus == "waiting for another") {
                    count = count + 1
                    Toast.makeText(mContext, currentstatus, Toast.LENGTH_SHORT).show()
                    if (count == 10) {
                        currentstatus = "out of class"
                    }
                }
            } else {
                currentstatus == "out of class"
                Toast.makeText(mContext, currentstatus, Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun findBeacon(beacon: Beacon): String {
        var distance =
            (10.toDouble().pow((beacon.measuredPower.toDouble() - beacon.rssi.toDouble()) / 20))
        var area: String? = null

        when (beacon.major) {
            38845 -> area = "Blue"
            51284 -> area = "Mint"
            31937 -> area = "CoCo"
            12436 -> area = "Mash"
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy @HH:mm:ss a")
        val currentDate = sdf.format(Date())

        var detail = IBeacon(
            area!!,
            beacon.major.toString(),
            beacon.minor.toString(),
            beacon.rssi.toString(),
            String.format("%.2f", distance),
            currentDate
        )
        println("++++++++++______$beaconList")

        if (beaconList.any { it.region == area }) {
            //เจออันซ้ำ
            if (beaconList.size >= 2) {
                checkInClass()

                val handler = Handler()
                val r = Runnable {
                    beaconList.clear()
                }
                handler.postDelayed(r,1000)


            } else {
                currentstatus = "waiting for another"
                statusList.add(Status(currentstatus))

            }

        } else {
            //ไม่เจออันซ้ำ
            beaconList.add(detail)
            if (beaconList.size >= 2) {
                checkInClass()

                val handler = Handler()
                val r = Runnable {
                    beaconList.clear()
                }
                handler.postDelayed(r,1000)

            } else {
                currentstatus = "waiting for another"
                statusList.add(Status(currentstatus))
            }
        }
        return currentstatus
    }

    private fun checkInClass() {
        if (beaconList[0].rssi.toInt() >= -79 && beaconList[1].rssi.toInt() >= -75) {
            println("-------> in class")
            currentstatus = "in class"
            statusList.add(Status(currentstatus))
        } else {
            currentstatus = "out of class"
            statusList.add(Status(currentstatus))
        }
    }


    private fun setConnect() {
        beaconManager!!.connect {
            beaconManager!!.startMonitoring(region)
        }
    }

    override fun onResume() {
        super.onResume()
        beaconManager!!.connect(object : BeaconManager.ServiceReadyCallback {
            override fun onServiceReady() {
                beaconManager!!.startRanging(region)
            }

        })
    }

    override fun onPause() {
        beaconManager!!.stopRanging(region)
        Handler().removeCallbacks(null)
        super.onPause()
    }


}