package com.mahidol.classattendance.Fragments


import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
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

import androidx.fragment.app.FragmentTransaction
import com.mahidol.classattendance.BodyActivity
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AttendanceFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var beaconList: ArrayList<IBeacon>
    lateinit var statusList: ArrayList<Status>
    lateinit var studentList: HashMap<String, Attendance>
    private var beaconManager: BeaconManager? = null
    private var region: BeaconRegion? = null
    lateinit var myRunnable: Runnable
    lateinit var adapter: MycourseAdapter
    lateinit var mActivity: Activity
    lateinit var dataReference: DatabaseReference
    lateinit var dataReference2: DatabaseReference
    lateinit var dataReference3: DatabaseReference
    lateinit var dataReference4: DatabaseReference
    var handler: Handler? = null
    lateinit var fragmentTransaction: FragmentTransaction

    lateinit var onlinecourse: ArrayList<Course>
    lateinit var onlineListValue: HashMap<String, Any>
    lateinit var whoEnroll: ArrayList<String>

    val tmp = SimpleDateFormat("dd-MM-yy")
    val date = tmp.format(Date())
//    val date = "12-04-20"
    val tmp2 = SimpleDateFormat("HH:mm:ss a")
    val time = tmp2.format(Date())


    var countforOut = 0
    var counttoEnd = 0
    var alreadyInclass = false

    private val CHANNEL_ID = "100"


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

        dataReference = FirebaseDatabase.getInstance().getReference("OnlineCourse")
        dataReference2 = FirebaseDatabase.getInstance().getReference("Attendance")
        dataReference3 = FirebaseDatabase.getInstance().getReference("AllCourse")
        dataReference4 = FirebaseDatabase.getInstance().getReference("UserProfile")


        val gif = view!!.findViewById<ImageView>(R.id.loading_gif)
        val img = view!!.findViewById<ImageView>(R.id.img_attendance)



        onlineListValue = hashMapOf()
        whoEnroll = arrayListOf()
        onlinecourse = arrayListOf()

        img.visibility = View.INVISIBLE


        handler = Handler()

        myRunnable = Runnable {
            if (currentstatus == "out of class") {
                if (currenttype == "Student") {
                    alreadyInclass = false
                    if (isScanning) {
                        isScanning = false
                    }
                }
                return@Runnable
            }
            alreadyInclass = false
            Toast.makeText(
                context,
                "not found any beacon",
                Toast.LENGTH_SHORT
            ).show()

            gif.visibility = View.INVISIBLE
            statusText.visibility = View.INVISIBLE
            if (isScanning) {
                img.visibility = View.INVISIBLE
                isScanning = false
                if (currentcourse != null) {
                    if (currenttype == "Teacher") {
                        beaconManager!!.stopRanging(region)
                        dataReference.child("${currentcourse}+${currentjoinID}").removeValue()

                        studentList = hashMapOf()

                        var dataQuery = dataReference2.child("${currentcourse}+${currentjoinID}").child(date).orderByChild("username")

                        dataQuery.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0!!.exists()) {
                                    studentList.clear()
                                    for (i in p0.children) {
                                        val oneUser = i.getValue(Attendance::class.java)
                                        studentList.put(oneUser!!.username, oneUser)
                                    }
                                }
                            }
                        })

                        dataReference3.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0!!.exists()) {
                                    whoEnroll.clear()
                                    for (i in p0.children) {
                                        val oneUser = i.getValue(Course::class.java)
                                        if (oneUser!!.courseID == currentcourse && oneUser!!.joinID == currentjoinID) {
                                            whoEnroll = oneUser!!.whoEnroll
                                            println("whooooooooooooooooooo${whoEnroll}")
                                        }
                                    }

                                    whoEnroll.forEach {
                                        val tmp = it
                                        if (studentList.any { it.key == tmp }) {
                                        } else {
                                            studentList.put(it, Attendance(it, "Student", currentcourse!!, date, "", "", "Absent"))
                                        }
                                    }
                                    dataReference2.child("${currentcourse}+${currentjoinID}").child(date).setValue(studentList)
                                    showNotification()
                                }
                            }
                        })
                        currentstatus = "class is over"
                        showDialog(view, adapter)
                        adapter.notifyDataSetChanged()
                    }
                    courseList.forEach {
                        if (it.courseID == currentcourse && it.joinID == currentjoinID) {
                            it.courseStatus = ""
                        }
                    }
                    courselistdetail.forEach {
                        if (it.key == currentcourse && it.value.joinID == currentjoinID) {
                            it.value.courseStatus = ""
                        }
                    }
                    dataReference4.child(currentuser).child("courselist").setValue(courselistdetail)

                } else {
                    val img = view!!.findViewById<ImageView>(R.id.img_attendance)
                    img.setImageResource(R.mipmap.ic_pleaseenterthecr)
                    img.visibility = View.VISIBLE
                }

            } else {
                val img = view!!.findViewById<ImageView>(R.id.img_attendance)
                img.setImageResource(R.mipmap.ic_pleaseenterthecr)
                img.visibility = View.VISIBLE
            }
            println("oooooooooooooooooooooooooooooooooooooooooo")
            if (currenttype == "Student") {
                dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var tmp: HashMap<String, Course> = hashMapOf()
                        if (p0!!.exists()) {
                            tmp.clear()
                            for (i in p0.children) {
                                val oneUser = i.getValue(Course::class.java)
                                tmp.put("${oneUser!!.courseID}+${oneUser!!.joinID}", oneUser!!)
                            }
                        }
                        if (tmp.any { it.key == "${currentcourse}+${currentjoinID}" }) {
                        } else {
                            beaconManager!!.stopRanging(region)
                            currentstatus = "class is over"
                            if (currentcourse != "") {
                                showDialog(view, adapter)
                                showNotification()
                                currentcourse = ""
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                )
            }
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
                handler!!.removeCallbacksAndMessages(null)
                if (!alreadyInclass) {
                    statusText.text = "Scanning"
                    statusText.visibility = View.VISIBLE
                    img.visibility = View.INVISIBLE
                    gif.visibility = View.VISIBLE
                }
                println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr")
                val nearestBeacon = beacons[0]
                currentstatus = findBeacon(nearestBeacon)
                if (currentstatus == "in class") {

                    isScanning = true

                    counttoEnd = 0
                    countforOut = 0
                    Toast.makeText(mContext, currentstatus, Toast.LENGTH_LONG).show()
                    statusText.visibility = View.INVISIBLE
                    img.visibility = View.INVISIBLE
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
                                    queryOnline()

                                    if (courseList[position].courseStatus == "Online") {
                                        currentcourse = courseList[position].courseID
                                        currentjoinID = courseList[position].joinID
                                        listview_attendance!!.adapter = null
                                        addFragment(StudentAttendanceFragment(courseList[position].courseID, courseList[position].joinID, date, time))
                                        addFragment(StudentlistFragment(courseList[position].courseID, courseList[position].joinID, date, true))
                                        showNotification()
                                        return@OnItemClickListener
                                    } else {
                                        courseList[position].courseStatus = "Online"
                                        currentcourse = courseList[position].courseID
                                        currentjoinID = courseList[position].joinID
                                        courselistdetail[currentcourse!!]!!.courseStatus = "Online"
                                        onlineListValue.put(
                                            "${courseList[position].courseID}+${courseList[position].joinID}", Course(
                                                courseList[position].courseID,
                                                courseList[position].joinID,
                                                courseList[position].owner,
                                                courseList[position].courseStatus,
                                                courseList[position].whoEnroll,
                                                ArrayList<Material>()

                                            )
                                        )
                                        dataReference4.child(currentuser).child("courselist").setValue(courselistdetail)
                                        dataReference.setValue(onlineListValue)
                                        val temp = HashMap<String, Attendance>()
                                        temp.put(currentuser!!, Attendance(currentuser!!, currenttype!!, currentcourse!!, date, time, "", "Teacher"))
                                        dataReference2.child("${currentcourse}+${currentjoinID}").child(date).setValue(temp)
                                        listview_attendance!!.adapter = null
                                        addFragment(StudentAttendanceFragment(courseList[position].courseID, courseList[position].joinID, date, time))
                                        addFragment(StudentlistFragment(courseList[position].courseID, courseList[position].joinID, date, true))
                                        showNotification()
                                        return@OnItemClickListener
                                    }

                                }

                            println("b/////////////////" + countforOut + "////" + counttoEnd)

                        } else {
                            queryOnline()
                            //not finish durationtime

                            println("dddddddddddddddddddddddddddd${onlinecourse}")


                        }
                    }
                    alreadyInclass = true
                }
                if (currentstatus == "out of class") {
                    handler!!.postDelayed(myRunnable, 1000)

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
                        handler!!.postDelayed(myRunnable, 1000)

                        countforOut = 0
                        counttoEnd = counttoEnd + 1
                        statusText.text = currentstatus
                        println("3/////////////////" + countforOut + "////" + counttoEnd)

                    }
                    Toast.makeText(mContext, currentstatus, Toast.LENGTH_SHORT).show()
                }
            } else {
                currentstatus = "out of class"
                handler!!.postDelayed(myRunnable, 1000)
                countforOut = 0
                counttoEnd = counttoEnd + 1
                statusText.text = currentstatus
                Toast.makeText(mContext, currentstatus, Toast.LENGTH_SHORT).show()
                println("4/////////////////" + countforOut + "////" + counttoEnd)

            }

            if (counttoEnd == 20) {
                currentstatus = "class is over"
                Toast.makeText(mContext, currentstatus, Toast.LENGTH_LONG).show()
                handler!!.postDelayed(myRunnable, 1000)
                counttoEnd = 0
            }

            println("ggggggggggggggggggggg$isScanning${beaconList.size}")
            handler!!.postDelayed(myRunnable, 15000)
        })


        if (!isScanning) {
            handler!!.postDelayed(myRunnable, 10000)
            println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
        }


    }

    private fun isEnabled(position: Int): Boolean {
        return false
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
        adapter = MycourseAdapter(
            mContext,
            R.layout.list_detail,
            courseList
        )
        var query = dataReference.orderByChild("courseID")
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    onlinecourse.clear()
                    for (i in p0.children) {
                        val result = i.getValue(Course::class.java)
                        if (currenttype == "Teacher") {
                            if (onlinecourse.any { it.joinID == result!!.joinID }) {
                            } else {
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
                        } else {
                            courselistdetail.forEach {
                                if (it.key == result!!.courseID && it.value.joinID == result!!.joinID) {
                                    println("uuuuuuuuuuuuuuuuuuuuuuuuu${it.key} result:: ${result.courseID}  online:::${onlinecourse}")
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
                                    println("uuuuuuuuuuuuuuuuuuuurrrrrrrrrrrrrrrrrrr${onlinecourse}")
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                if (currenttype == "Student") {
                    if (onlinecourse.size == 0) {
                        println("size000000000000000000")
                        val listviewAtten = view!!.findViewById<ListView>(R.id.listview_attendance)
                        listviewAtten.adapter = null
                        val img = view!!.findViewById<ImageView>(R.id.img_attendance)
                        img.setImageResource(R.mipmap.ic_noonlinecourse)
                        img.visibility = View.VISIBLE

                    }
                    if (onlinecourse.size == 1) {
                        currentcourse = onlinecourse[0].courseID
                        currentjoinID = onlinecourse[0].joinID
                        val listviewAtten = view!!.findViewById<ListView>(R.id.listview_attendance)
                        listviewAtten.adapter = null
                        addFragment(StudentAttendanceFragment(currentcourse!!, onlinecourse[0].joinID, date, time))
                        showNotification()
                    } else {
                        println("sizeeeeeeenot000000000")
                        adapter = MycourseAdapter(mContext, R.layout.list_detail, onlinecourse)
                        listview_attendance!!.adapter = adapter
                        adapter.notifyDataSetChanged()
                        listview_attendance!!.onItemClickListener =
                            AdapterView.OnItemClickListener { parent, view, position, id ->
                                Toast.makeText(
                                    mContext, "Started ${courseList[position].courseID
                                    }", Toast.LENGTH_SHORT
                                ).show()
                                courseList.forEach {
                                    if (it.courseID == onlinecourse[position].courseID && it.joinID == onlinecourse[position].joinID) {
                                        it.courseStatus = "Online"
                                    }
                                }
                                courselistdetail.forEach {
                                    if (it.key == onlinecourse[position].courseID && it.value.joinID == onlinecourse[position].joinID) {
                                        it.value.courseStatus = "Online"
                                    }
                                }
                                currentcourse = onlinecourse[position].courseID
                                currentjoinID = onlinecourse[position].joinID
                                listview_attendance.adapter = null
                                addFragment(StudentAttendanceFragment(currentcourse!!, onlinecourse[position].joinID, date, time))
                                showNotification()
                            }

                    }
                }
                adapter.notifyDataSetChanged()
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

    private fun showNotification() {
        createNotificationChannel()
        if (currentstatus == "in class") {
            if (currenttype == "Teacher") {
                notifyMessage("${currentuser} started course", "Started at ${time} \nCourse : ${currentcourse} ")
            } else {
                notifyMessage("${currentuser} checked in", "Checked in at ${time} \nCourse : ${currentcourse} ")
            }
        }
        if (currentstatus == "class is over") {
            if (currenttype == "Teacher") {

                var presentList = ArrayList<String>()
                var absentList = ArrayList<String>()

                dataReference2.child("${currentcourse}+${currentjoinID}").child(date).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0!!.exists()) {
                            presentList.clear()
                            absentList.clear()
                            for (i in p0.children) {
                                val oneUser = i.getValue(Attendance::class.java)
                                if (oneUser!!.attendance == "Present") {
                                    presentList.add(oneUser.username)
                                }
                                if (oneUser!!.attendance == "Absent") {
                                    absentList.add(oneUser.username)
                                }
                                println("present ${presentList}  absent ${absentList}")
                            }
                        }
                        notifyMessage("${currentuser} ended course", "Ended at ${time}  \nCourse ${currentcourse} \nPresent : ${presentList.size} Absent : ${absentList.size}")
                    }
                })
            } else {
                notifyMessage("${currentuser} checked out", "Attendance hours : ${currentattendancetime} \nCourse : ${currentcourse}")
            }
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
        handler!!.removeCallbacksAndMessages(null)
        println("88888888888888888888  on  pause 88888888888888888")
    }


    private fun addFragment(fragment: Fragment) {
        fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }


    private fun showDialog(view: View, adapter: MycourseAdapter) {
        val applypopup = popup_attendancedetail_Fragment(view, adapter, currentcourse!!, date)
        applypopup.show(activity!!.supportFragmentManager, "exampleBottomSheet")
    }

//    override fun onDestroy() {
//        handler!!.removeCallbacks(myRunnable)
//        println("55555555555555555 on destroy 5555555555555555555")
//        super.onDestroy()
//    }


    private fun createNotificationChannel() {
        // if you want to handle all version then use if-else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ClassAttendance"
            val description = "ClassAttendance Notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)

            channel.description = description

            val notificationManager = getSystemService(context!!, NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)

        }
    }

    private fun notifyMessage(title: String, message: String) {
        val intent1 = Intent(context, BodyActivity::class.java)
        val pIntent1 = PendingIntent.getActivity(context, 1001, intent1, 0)

        val option1Action =
            NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground, "Open App", pIntent1)
                .build()
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_attend)


        val mBuilder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setLargeIcon(icon)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .addAction(option1Action)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(System.currentTimeMillis() + 200)
        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.notify(0, mBuilder.build())

    }


}