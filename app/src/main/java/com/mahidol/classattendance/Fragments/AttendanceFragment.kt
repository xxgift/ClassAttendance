package com.mahidol.classattendance.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.estimote.coresdk.observation.region.beacon.BeaconRegion
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.service.BeaconManager
import com.google.firebase.database.*
import com.mahidol.classattendance.Adapter.MycourseAdapter
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R
import kotlinx.android.synthetic.main.fragment_attendance.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.*
import kotlin.collections.HashMap

class AttendanceFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var beaconList: ArrayList<IBeacon>
    lateinit var statusList: ArrayList<Status>
    lateinit var studentList: ArrayList<Attendance>
    private var beaconManager: BeaconManager? = null
    private var region: BeaconRegion? = null
    lateinit var myRunnable: Runnable
    lateinit var adapter: MycourseAdapter
    lateinit var mActivity: Activity
    lateinit var dataReference: DatabaseReference
    var handler :Handler? = null
    lateinit var fragmentTransaction: FragmentTransaction


    lateinit var onlineListValue:HashMap<String,Any>


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
        addbtn_studentlist.visibility = View.INVISIBLE
        subtitleTextAttendance.visibility = View.INVISIBLE

        dataReference = FirebaseDatabase.getInstance().getReference("OnlineCourse")

        onlineListValue = hashMapOf()

        val gif = view!!.findViewById<ImageView>(R.id.loading_gif)

        handler = Handler()

        myRunnable = Runnable {
            alreadyInclass = false
            Toast.makeText(
                context,
                "not found any beacon",
                Toast.LENGTH_SHORT
            ).show()
            gif.visibility = View.INVISIBLE
            addbtn_studentlist.visibility = View.INVISIBLE
            statusText.visibility = View.VISIBLE
            adapter = MycourseAdapter(
                mContext,
                R.layout.list_detail,
                courseList
            )
            listview_attendance.adapter = null
            adapter.notifyDataSetChanged()
            if (isScanning) {
                statusText.text = "Class is over"
                beaconManager!!.stopRanging(region)
                dataReference.child(currentcourse).removeValue()

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
        studentList = arrayListOf()


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
                handler!!.removeCallbacksAndMessages(null)
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
                                R.layout.list_detail,
                                courseList
                            )
                            listview_attendance!!.adapter = adapter
                            adapter.notifyDataSetChanged()

                            listview_attendance!!.onItemClickListener =
                                AdapterView.OnItemClickListener { parent, view, position, id ->
                                    Toast.makeText(
                                        mContext, "Get Started ${courseList[position].courseID
                                        }", Toast.LENGTH_SHORT
                                    ).show()
                                    courseList[position].courseStatus = "Online"
                                    currentcourse = courseList[position].courseID
                                    queryOnline()
                                    if (onlinecourse.any { it.courseID == courseList[position].courseID }) {
                                    } else {
                                        onlinecourse.add(
                                            Course(
                                                courseList[position].courseID,
                                                courseList[position].joinID,
                                                courseList[position].owner,
                                                courseList[position].courseStatus,
                                                courseList[position].whoEnroll,
                                                ArrayList<Material>()
                                            )
                                        )
                                        onlineListValue.put(
                                            courseList[position].courseID, Course(
                                                courseList[position].courseID,
                                                courseList[position].joinID,
                                                courseList[position].owner,
                                                courseList[position].courseStatus,
                                                courseList[position].whoEnroll,
                                                ArrayList<Material>()

                                            )
                                        )
                                    }

                                    dataReference.setValue(onlineListValue)
                                    addbtn_studentlist.visibility = View.VISIBLE
                                    subtitleTextAttendance.visibility = View.VISIBLE
                                    subtitleTextAttendance.text = currentcourse
                                    replaceFragment(LogAttendanceFragment(courseList[position].courseID))

                                }

                            println("b/////////////////" + countforOut + "////" + counttoEnd)

                        } else {

                            adapter = MycourseAdapter(mContext, R.layout.list_detail, onlinecourse)
                            listview_attendance!!.adapter = adapter
                            adapter.notifyDataSetChanged()
                            queryOnline()

                            val tmp1 = SimpleDateFormat("dd-MM-yy")
                            val tmp2 = SimpleDateFormat("HH:mm:ss")
                            val date = tmp1.format(Date())
                            val time = tmp2.format(Date())

                            //not finish durationtime
                            val durationtime = ""

                            if (onlinecourse.size == 1) {
                                currentcourse = onlinecourse[0].courseID
                                studentList.add(
                                    Attendance(
                                        currentuser!!, currenttype!!,
                                        currentcourse!!, date, time, durationtime,"Present"
                                    )
                                )
                            }

                            listview_attendance!!.onItemClickListener =
                                AdapterView.OnItemClickListener { parent, view, position, id ->
                                    Toast.makeText(
                                        mContext, "open ${courseList[position].courseID
                                        }", Toast.LENGTH_SHORT
                                    ).show()
                                    courseList[position].courseStatus = "Online"

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
                addbtn_studentlist.visibility = View.INVISIBLE
                adapter = MycourseAdapter(
                    mContext,
                    R.layout.list_detail,
                    courseList
                )
                listview_attendance!!.adapter = null
                adapter.notifyDataSetChanged()
                statusText.visibility = View.VISIBLE
                statusText.text = currentstatus
                beaconManager!!.stopRanging(region)
                dataReference.child(currentcourse).removeValue()
            }

            println("ggggggggggggggggggggg$isScanning${beaconList.size}")
            handler!!.postDelayed(myRunnable, 15000)
        })


        if (!isScanning) {
            handler!!.postDelayed(myRunnable, 10000)
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

    private fun queryOnline() {
        var query = dataReference.orderByChild("courseID")
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    for (i in p0.children) {
                        val result = i.getValue(Course::class.java)
                        if (currenttype == "Teacher") {
                            onlinecourse.add(
                                Course(
                                    result!!.courseID,
                                    result!!.joinID,
                                    result!!.owner,
                                    result!!.courseStatus,
                                    result!!.whoEnroll,
                                    ArrayList<Material>()
                                )
                            )
                        } else {
                            courselistdetail.forEach {
                                if (it.key == result!!.courseID) {
                                    onlinecourse.add(
                                        Course(
                                            result!!.courseID,
                                            result!!.joinID,
                                            result!!.owner,
                                            result!!.courseStatus,
                                            result!!.whoEnroll,
                                            ArrayList<Material>()
                                        )
                                    )
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })
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

//    override fun onPause() {
//        beaconManager!!.stopRanging(region)
//        super.onPause()
//        handler!!.removeCallbacksAndMessages(null)
//        println("88888888888888888888  on  pause 88888888888888888")
//    }


    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }

    override fun onDestroy() {
        handler!!.removeCallbacks(myRunnable)
        println("55555555555555555 on destroy 5555555555555555555")
        super.onDestroy()
    }
}