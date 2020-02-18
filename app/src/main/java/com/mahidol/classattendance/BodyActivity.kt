package com.mahidol.classattendance

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.Bundle

import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.mahidol.classattendance.Fragments.*
import com.mahidol.classattendance.Helper.HTTPHelper
import com.mahidol.classattendance.Models.SearchModel
import com.mahidol.classattendance.Models.User
import com.mahidol.classattendance.Models.currentuser

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener


import kotlinx.android.synthetic.main.activity_body.*
import kotlinx.android.synthetic.main.header_nav.*

class BodyActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SensorEventListener {

    lateinit var fragmentTransaction: FragmentTransaction
    private var sensorManager: SensorManager? = null
    private var drawer: DrawerLayout? = null
    private var appBar: TextView? = null
    private var subappBar:TextView? = null
    private var username: TextView? = null
    private var type: TextView? = null

    private var lastUpdate = 0L

    var url = "https://studenttracking-47241.firebaseio.com/UserProfile/"
    var uname: String? = null
    var name: String? = null
    var userprofile: User? = null

    //set listener for selected item from bottom navigation bar to go to that fragment
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_timeline -> {
                    drawer!!.closeDrawer(GravityCompat.START)
                    appBar!!.text = "Timeline"
                    subappBar!!.text = userprofile!!.type
                    replaceFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_scanner -> {
                    drawer!!.closeDrawer(GravityCompat.START)
                    appBar!!.text = "Scanner"
                    subappBar!!.text = userprofile!!.type
                    replaceFragment(ScannerFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_post -> {
                    drawer!!.closeDrawer(GravityCompat.START)
                    appBar!!.text = "Post"
                    subappBar!!.text = userprofile!!.type
                    replaceFragment(CheckinFragment())
                    return@OnNavigationItemSelectedListener true
                }

            };false

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        replaceFragment(HomeFragment())
        Toast.makeText(this, "loading...", Toast.LENGTH_SHORT).show()

        //sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lastUpdate = System.currentTimeMillis()

        //set title page
        appBar = findViewById(R.id.titleText)
        subappBar = findViewById(R.id.subtitleText)


                //get username from transfer data of LoginActivity
        uname = intent.getStringExtra("uname")

        //get json file of this username
        var asyncTask = object : AsyncTask<String, String, String>() {

            override fun doInBackground(vararg p0: String?): String {
                val helper = HTTPHelper()
                return helper.getHTTPData(url +uname+".json")
            }

            override fun onPostExecute(result: String?) {
                if (result != "null") {
                    userprofile = Gson().fromJson(result, User::class.java)
                    //set header of navigation bar
                    username = findViewById(R.id.namenav)
                    type = findViewById(R.id.nameSurname_nav)
                    username!!.text = userprofile!!.username
                    type!!.text = userprofile!!.type

                    headnavIC.setImageResource(R.mipmap.ic_teacheravatar)



                    //set current user
                    currentuser = userprofile!!.username
                    appBar!!.text = "Timeline"
                    subappBar!!.text = userprofile!!.type

                }
            }

        }
        asyncTask.execute()

        //add listener to bottom navigation bar
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //add listener to navigation bar
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
//        val navigationViewItemInclass = findViewById<View>(R.id.teacher_studentinclass) as It
        navigationView.setNavigationItemSelectedListener(this)

        //set drawer
        drawer = findViewById<View>(R.id.drawyer_layout) as DrawerLayout

        //add listener of setting button to open navigation bar when clicked
        val setting = findViewById<ImageButton>(R.id.setting)
        setting.setOnClickListener {
            if (drawer!!.isDrawerOpen(GravityCompat.START)) {
                drawer!!.closeDrawer(GravityCompat.START)
            } else {
                drawer!!.openDrawer(GravityCompat.START)
            }
        }


        //add listener of search button to open navigation bar when clicked
        val search = findViewById<ImageButton>(R.id.search)
        search.setOnClickListener {
            val searchView:SearchView? = null
            SimpleSearchDialogCompat(
                this@BodyActivity, "Search", "What are you looking for...?", null, initData(),
                SearchResultListener {baseSearchDialogCompat, item, position ->
                    Toast.makeText(this, item.title,Toast.LENGTH_SHORT).show()
                    baseSearchDialogCompat.dismiss()
                }
            ).show()

        }

    }


    private fun initData(): ArrayList<SearchModel> {
        val items = ArrayList<SearchModel>()
        items.add(SearchModel("Thailand"))
        items.add(SearchModel("Japan"))
        items.add(SearchModel("Korea"))
        items.add(SearchModel("USA"))
        items.add(SearchModel("China"))
        items.add(SearchModel("Russia"))
        items.add(SearchModel("Finland"))

        return items

    }




    //function for replace fragment
    private fun replaceFragment(fragment: Fragment) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Handle navigation view item clicks here.
        val id = item.itemId

        //go to log in page when selected log out
        if (id == R.id.teacher_logout) {
            drawer!!.closeDrawer(GravityCompat.START)
            val intent = Intent(this@BodyActivity, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "log out!!!", Toast.LENGTH_SHORT).show()
            finish()
        }
        if (id == R.id.teacher_thiscourse) {
            drawer!!.closeDrawer(GravityCompat.START)
            replaceFragment(ThiscourseteacherFragment())
            appBar!!.text = "Course List"
            subappBar!!.text = "${userprofile!!.type}: ${userprofile!!.username}"
        }
        if (id == R.id.teacher_studentinclass) {
            drawer!!.closeDrawer(GravityCompat.START)
            replaceFragment(StudentinclassFragment())
            appBar!!.text = "Student List"
            subappBar!!.text = "Student List"
        }
        if (id == R.id.teacher_classmeterial) {
            drawer!!.closeDrawer(GravityCompat.START)
            replaceFragment(ClassmaterialFragment())
            appBar!!.text = "Class Materials"
            subappBar!!.text = "Course List"
        }

        return true
    }


    //sensor Accelerometer to go to scanner page
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event)
        }
    }
    private fun getAccelerometer(event: SensorEvent) {
        val values = event.values
        val x = values[0]
        val y = values[1]
        val z = values[2]

        val accel =
            (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)
        val actualTime = System.currentTimeMillis()
        if (accel > 20) {
            if (actualTime - lastUpdate < 200) {
                return
            }
            lastUpdate = actualTime
            replaceFragment(ScannerFragment())
            appBar!!.text = "Scanner"
            Toast.makeText(this, "Clear", Toast.LENGTH_SHORT).show()

        }
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)
    }





}