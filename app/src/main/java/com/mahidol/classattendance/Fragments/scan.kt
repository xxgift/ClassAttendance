package com.mahidol.classattendance.Fragments

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker
import com.estimote.coresdk.observation.region.beacon.BeaconRegion
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.service.BeaconManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.mahidol.classattendance.LoginActivity
import com.mahidol.classattendance.Models.BBeacon
import com.mahidol.classattendance.R

import kotlinx.android.synthetic.main.home_teacher.*


import kotlinx.android.synthetic.main.layout_navbar.*
import kotlinx.android.synthetic.main.list_student.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.pow


class scan : AppCompatActivity() {

    private lateinit var adapter: myCustomAdapter
    private val detailBeacon = arrayListOf<BBeacon>()
    private var beaconManager: BeaconManager? = null
    private var region: BeaconRegion? = null
    private var PLACES_BY_BEACONS: Map<String, List<String>>? = null
    private val cocoRegion = BeaconRegion(
        "coco region",
        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 31937, 52697
    )
    private val mashRegion = BeaconRegion(
        "mash region",
        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 12436, 27016
    )
    private val blueRegion = BeaconRegion(
        "blue region",
        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 38845, 58352
    )
    private val mintRegion = BeaconRegion(
        "mint region",
        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 51284, 49927
    )
    private var listRegion: List<BeaconRegion> =
        listOf(cocoRegion, mashRegion, blueRegion, mintRegion)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_teacher)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        val NavView: NavigationView = findViewById(R.id.nav_view)

        PLACES_BY_BEACONS = Collections.unmodifiableMap(setPlaceBybeacon())
        beaconManager = BeaconManager(this)
        region = BeaconRegion(
            "mint region",
            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null
        )

        setConnect()

        beaconManager!!.setForegroundScanPeriod(5000, 0)
        beaconManager!!.setBackgroundScanPeriod(5000, 0)

        beaconManager!!.setRangingListener(BeaconManager.BeaconRangingListener { beaconRegion, beacons ->
            if (beacons!!.isNotEmpty()) {
                val nearestBeacon = beacons[0]
                val places = placesNearBeacon(nearestBeacon)
                // TODO: update the UI here
                checkRegionInrange(beacons)
                Log.d("room", "Nearest places: $places ")
            }
        })
        beaconManager!!.setMonitoringListener(object : BeaconManager.BeaconMonitoringListener {
            override fun onExitedRegion(beaconRegion: BeaconRegion?) {

            }

            override fun onEnteredRegion(
                beaconRegion: BeaconRegion?,
                beacons: MutableList<Beacon>?
            ) {
                showNotification(
                    "You are in room",
                    "start counting the time"
                );
                placesNearBeacon(beacons!![0])
            }

        })


        adapter =
            myCustomAdapter(detailBeacon)
        listview.adapter = adapter
        listview.setOnItemClickListener { adapterView, view, position, id ->
            val item = adapterView.getItemAtPosition(position) as String
        }
        backBtn2.setOnClickListener {
            finish()
        }
        settingBtn.setOnClickListener {
            finish()
        }

        adapter.notiChange()

    }

    private fun checkRegionInrange(beacons: List<Beacon>) {
        var listNameBeacon: ArrayList<String> = ArrayList()
        beacons.forEach { beacon ->
            var regionSpecific = listRegion.filter { it.major == beacon.major }
            if (regionSpecific.isNotEmpty()) {
                listNameBeacon.add(regionSpecific[0].toString())
              //  Toast.makeText(this, regionSpecific[0].toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun placesNearBeacon(beacon: Beacon): List<String>? {
        var beaconKey = String.format("%d:%d", beacon.major, beacon.minor)
        var distance =
            10.toDouble().pow(beacon.measuredPower.toDouble() - beacon.rssi.toDouble() / 20)
        var area: String? = null
        when (beacon.major) {
            38845 -> area = "Blue"
            51284 -> area = "Mint"
            31937 -> area = "Coco"
            12436 -> area = "Mash"
        }
        if (PLACES_BY_BEACONS!!.containsKey(beaconKey)) {
            val sdf = SimpleDateFormat("dd-MMMM-yyyy  HH:mm:ss a  ")
            val currentDate = sdf.format(Date())
            Toast.makeText(
                this,
                "$area $beaconKey Tx:${beacon.measuredPower} rssi:${beacon.rssi} d:$distance",
                Toast.LENGTH_LONG
            ).show()
            val detail = BBeacon(area!!,beacon.major.toString(),beacon.minor.toString(),beacon.rssi.toString(),distance.toString(),currentDate)
            detailBeacon.add(detail)
            adapter.notiChange()

            return PLACES_BY_BEACONS?.get(beaconKey)
        }
        return Collections.emptyList()
    }

    private fun setConnect() {

        beaconManager!!.connect {
            beaconManager!!.startMonitoring(region)
        }
    }

    private fun setPlaceBybeacon(): HashMap<String, ArrayList<String>> {
        val placesByBeacons = HashMap<String, ArrayList<String>>()
        placesByBeacons.put("38845:58352", arrayListOf("Blue", "Mint", "Coco", "Mash"))
        placesByBeacons.put("51284:49927", arrayListOf("Mint", "Blue", "Coco", "Mash"))
        placesByBeacons.put("31937:52697", arrayListOf("Coco", "Blue", "Mint", "Mash"))
        placesByBeacons.put("12436:27016", arrayListOf("Mash", "Blue", "Mint", "Coco"))
        return placesByBeacons
    }

    private fun showNotification(title: String, message: String) {
        val notifyIntent = Intent(this, LoginActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivities(
            this, 0,
            arrayOf(notifyIntent), PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = Notification.Builder(this)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notification.defaults = notification.defaults or Notification.DEFAULT_SOUND
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    override fun onResume() {
        super.onResume()
        SystemRequirementsChecker.checkWithDefaultDialogs(this)
        beaconManager!!.connect(object : BeaconManager.ServiceReadyCallback {
            override fun onServiceReady() {
                beaconManager!!.startRanging(region);
            }

        })
    }

    override fun onPause() {
        beaconManager!!.stopRanging(region);
        super.onPause()
    }


    override fun onStart() {
        super.onStart()
        adapter.notiChange()

    }

    private class myCustomAdapter(val data: ArrayList<BBeacon>) : BaseAdapter() {

        fun notiChange() {
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): BBeacon? {
            return data[position]
        }


        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            val rowMain: View

            if (convertView == null) {
                val layoutInflator = LayoutInflater.from(viewGroup!!.context)
                rowMain = layoutInflator.inflate(R.layout.list_student, viewGroup, false)
                val viewHolder =
                    ViewHolder(
                        rowMain.resultText,
                        rowMain.dateText,
                        rowMain.textImgmain
                    )
                rowMain.tag = viewHolder
            } else {
                rowMain = convertView
            }

            val viewHolder = rowMain.tag as ViewHolder

            viewHolder.resultTextView.text = "major:${data[position].major.toString()} minor:${data[position].minor.toString()} rssi: ${data[position].rssi.toString()} distance: ${data[position].distance.toString()}"
            viewHolder.dateTextView.text = "Time : ${data[position].time.toString()}"
            viewHolder.textImgView.text = data[position].region.toString()






            rowMain.setOnClickListener {
                rowMain.animate().setDuration(500).alpha(0f).withEndAction {
                    notifyDataSetChanged()
                    rowMain.alpha = 1.0F

                }
            }
            return rowMain
        }

        private class ViewHolder(
            val resultTextView: TextView,
            val dateTextView: TextView,
            val textImgView: TextView
        )


    }
}