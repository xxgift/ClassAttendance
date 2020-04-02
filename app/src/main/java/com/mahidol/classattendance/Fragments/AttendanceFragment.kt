package com.mahidol.classattendance.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.estimote.coresdk.observation.region.beacon.BeaconRegion
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.service.BeaconManager
import com.mahidol.classattendance.Adapter.MycourseAdapter
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_attendance.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

import android.os.Handler
import android.widget.*
import com.google.firebase.database.*

class AttendanceFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var beaconList: ArrayList<BBeacon>
    lateinit var statusList: ArrayList<Status>
    private var beaconManager: BeaconManager? = null
    private var region: BeaconRegion? = null
    lateinit var adapter: MycourseAdapter
    lateinit var mActivity: Activity
    lateinit var dataReference: DatabaseReference
    private var subappBar: TextView? = null

    var countforOut = 0
    var counttoEnd = 0
    var isScanning = false
    var alreadyInclass = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Toast.makeText(mContext, "scanning...", Toast.LENGTH_LONG).show()
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
        mActivity = activity!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var addbtn = view.findViewById<Button>(R.id.addbtn_studentlist)
        addbtn.visibility = View.INVISIBLE

        dataReference = FirebaseDatabase.getInstance().getReference("OnlineCourse")

        subappBar = view.findViewById<TextView>(R.id.subtitleText)


        val gif = view!!.findViewById<ImageView>(R.id.loading_gif)

        var handler = Handler()
        var r = Runnable {
            alreadyInclass = false
            Toast.makeText(
                context,
                "not found any beacon",
                Toast.LENGTH_SHORT
            ).show()
            gif.visibility = View.INVISIBLE
            statusText.visibility = View.VISIBLE
            adapter = MycourseAdapter(
                mContext,
                activity!!,
                R.layout.list_detail,
                courselistdetail
            )
            listview_attendance.adapter = null
            adapter.notifyDataSetChanged()
            if (isScanning) {
                statusText.text = "Class is over"
                beaconManager!!.stopRanging(region)

            } else {

                statusText.text = "Not found any class"
            }
            println("oooooooooooooooooooooooooooooooooooooooooo")
        }

        Glide
            .with(this)
            .load(R.drawable.scanning)
            .fitCenter()
            .into(loading_gif)


        beaconList = arrayListOf()
        statusList = arrayListOf()


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
                isScanning = true
                handler.removeCallbacksAndMessages(null)
                if (!alreadyInclass) {
                    statusText.text = "Scanning"
                    statusText.visibility = View.VISIBLE
                    gif.visibility = View.VISIBLE
                }
                println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr")
                val nearestBeacon = beacons[0]
                currentstatus = findBeacon(nearestBeacon)
                if (currentstatus == "in class") {
                    counttoEnd = 0
                    countforOut = 0
                    Toast.makeText(mContext, currentstatus, Toast.LENGTH_LONG).show()
                    statusText.visibility = View.INVISIBLE
                    gif.visibility = View.INVISIBLE
                    if (!alreadyInclass) {
                        if (currenttype == "Teacher") {
                            adapter = MycourseAdapter(
                                mContext,
                                activity!!,
                                R.layout.list_detail,
                                courselistdetail
                            )
                            listview_attendance!!.adapter = adapter
                            adapter.notifyDataSetChanged()

                            listview_attendance!!.onItemClickListener =
                                AdapterView.OnItemClickListener { parent, view, position, id ->
                                        Toast.makeText(mContext, "open ${courselistdetail[position].courseID
                                        }", Toast.LENGTH_SHORT).show()
                                    courselistdetail[position].courseStatus = "Online"
                                    currentcourse = courselistdetail[position].courseID
                                    onlinecourse.add(Course(courselistdetail[position].courseID,courselistdetail[position].joinID,courselistdetail[position].owner,courselistdetail[position].courseStatus))
                                    dataReference.setValue(onlinecourse)
                                    subappBar!!.text = currentcourse
                                    addbtn.visibility = View.VISIBLE


                                }

                            println("b/////////////////" + countforOut + "////" + counttoEnd)

                        } else {

                            var query = dataReference.orderByChild("joinID")
                            query.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                }
                                override fun onDataChange(p0: DataSnapshot?) {
                                    if (p0!!.exists()) {
                                        for (i in p0.children) {
                                            val result = i.getValue(Course::class.java)
                                            courselistdetail.forEach {
                                                if(it.courseID==result!!.courseID){
                                                    onlinecourse.add(Course(result!!.courseID, result!!.joinID,result!!.owner,result!!.courseStatus))
                                                }
                                            }
                                        }
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            })
                            adapter = MycourseAdapter(mContext, activity!!, R.layout.list_detail, onlinecourse)
                            listview_attendance!!.adapter = adapter
                            adapter.notifyDataSetChanged()

                            listview_attendance!!.onItemClickListener =
                                AdapterView.OnItemClickListener { parent, view, position, id ->
                                    Toast.makeText(mContext, "open ${courselistdetail[position].courseID
                                    }", Toast.LENGTH_SHORT).show()
                                    courselistdetail[position].courseStatus = "Online"
                                    onlinecourse.add(Course(courselistdetail[position].courseID,courselistdetail[position].joinID,courselistdetail[position].owner,courselistdetail[position].courseStatus))
                                    dataReference.setValue(onlinecourse)
                                }

                        }
                    }
                    alreadyInclass = true
                }
                if (currentstatus == "out of class") {
                    countforOut = 0
                    counttoEnd = counttoEnd + 1
                    Toast.makeText(mContext, currentstatus, Toast.LENGTH_LONG).show()
                    statusText.text = currentstatus

                    println("/////////////////" + countforOut + "////" + counttoEnd)
                }
                if (currentstatus == "waiting for another") {

                    countforOut = countforOut + 1
                    println("2/////////////////" + countforOut + "////" + counttoEnd)
                    if (countforOut == 3) {
                        currentstatus = "out of class"
                        countforOut = 0
                        counttoEnd = counttoEnd + 1
                        statusText.text = currentstatus
                        println("3/////////////////" + countforOut + "////" + counttoEnd)

                    }
                    Toast.makeText(mContext, currentstatus, Toast.LENGTH_SHORT).show()
                }
            } else {
                currentstatus = "out of class"
                countforOut = 0
                counttoEnd = counttoEnd + 1
                statusText.text = currentstatus
                Toast.makeText(mContext, currentstatus, Toast.LENGTH_SHORT).show()
                println("4/////////////////" + countforOut + "////" + counttoEnd)

            }

            if (counttoEnd == 2) {
                currentstatus = "Class is over"
                alreadyInclass = false
                Toast.makeText(mContext, currentstatus, Toast.LENGTH_SHORT).show()
                gif.visibility = View.INVISIBLE
                adapter = MycourseAdapter(
                    mContext,
                    activity!!,
                    R.layout.list_detail,
                    courselistdetail
                )
                listview_attendance!!.adapter = null
                adapter.notifyDataSetChanged()
                statusText.visibility = View.VISIBLE
                statusText.text = currentstatus
                beaconManager!!.stopRanging(region)
            }

            println("ggggggggggggggggggggg$isScanning${beaconList.size}")
            handler.postDelayed(r, 15000)
        })


        if (!isScanning) {
            handler.postDelayed(r, 10000)
            println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
        }


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

        var detail = BBeacon(
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
                beaconList.clear()

            } else {
                currentstatus = "waiting for another"
                statusList.add(Status(currentstatus))

            }

        } else {
            //ไม่เจออันซ้ำ
            beaconList.add(detail)
            if (beaconList.size >= 2) {
                checkInClass()
                beaconList.clear()
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
        super.onPause()
        Handler().removeCallbacksAndMessages(null)
    }
}