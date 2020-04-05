package com.mahidol.classattendance


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.mahidol.classattendance.Fragments.AttendanceFragment
import com.mahidol.classattendance.Fragments.ChatroomFragment
import com.mahidol.classattendance.Fragments.MycourseFragment
import com.mahidol.classattendance.Fragments.ScannerFragment
import com.mahidol.classattendance.Helper.HTTPHelper
import com.mahidol.classattendance.Models.*
import kotlinx.android.synthetic.main.activity_body.*
import kotlinx.android.synthetic.main.header_nav.*

class BodyActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SensorEventListener {

    lateinit var beaconList: ArrayList<IBeacon>
    lateinit var statusList: ArrayList<Status>

    lateinit var dataReference: DatabaseReference


    lateinit var fragmentTransaction: FragmentTransaction

    private var sensorManager: SensorManager? = null
    private var drawer: DrawerLayout? = null
    private var appBar: TextView? = null
    var subappBar: TextView? = null
    var username: TextView? = null
    private var type: TextView? = null
    private var settingBtn: ImageButton? = null

    private var lastUpdate = 0L

    var url = "https://studenttracking-47241.firebaseio.com/UserProfile/"
    var uname: String? = null
    var name: String? = null
    var userprofile: User? = null





    //set listener for selected item from bottom navigation bar to go to that fragment
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_chatroom -> {
                    drawer!!.closeDrawer(GravityCompat.START)
                    appBar!!.text = "Chat Room"
                    subappBar!!.text = "${userprofile!!.type} : ${userprofile!!.username}"
                    replaceFragment(ChatroomFragment())

                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_mycourse -> {
                    drawer!!.closeDrawer(GravityCompat.START)
                    appBar!!.text = "My Course"
                    subappBar!!.text = "${userprofile!!.type} : ${userprofile!!.username}"
                    replaceFragment(MycourseFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_attendance -> {
                    drawer!!.closeDrawer(GravityCompat.START)
                    appBar!!.text = "Attendance"
                    subappBar!!.text = "${userprofile!!.type} : ${userprofile!!.username}"
                    replaceFragment(AttendanceFragment())

                    return@OnNavigationItemSelectedListener true
                }


            };false

        }

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body)
        startService(Intent(this, NotificationService::class.java))
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        bluetoothAdapter?.takeIf { !it.isEnabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 2)
        }

        val permissionCall = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        )
        val permissionLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val permissionBluetooth = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH
        )

        if (permissionCall != PackageManager.PERMISSION_GRANTED || permissionLocation != PackageManager.PERMISSION_GRANTED || permissionBluetooth != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH
                ), 1
            )
        }

        //set title page
        appBar = findViewById(R.id.titleText)
        subappBar = findViewById(R.id.subtitleText)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var token = getSharedPreferences("uname", Context.MODE_PRIVATE)

        dataReference = FirebaseDatabase.getInstance().getReference("UserProfile")


        replaceFragment(ChatroomFragment())
        Toast.makeText(this, "loading...", Toast.LENGTH_SHORT).show()

        //sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lastUpdate = System.currentTimeMillis()


        //get username from transfer data of LoginActivity
//        uname = intent.getStringExtra("uname")
        uname = token.getString("loginusername", " ")

        //get json file of this username
        var asyncTask = object : AsyncTask<String, String, String>() {

            override fun doInBackground(vararg p0: String?): String {
                val helper = HTTPHelper()
                return helper.getHTTPData(url + uname + ".json")
            }

            override fun onPostExecute(result: String?) {
                if (result != "null") {
                    userprofile = Gson().fromJson(result, User::class.java)
                    //set header of navigation bar
                    username = findViewById(R.id.namenav)
                    type = findViewById(R.id.nameSurname_nav)
                    username!!.text = userprofile!!.username
                    courselistdetail = userprofile!!.courselist
                    courselistdetail.forEach{
                        courseList.add(it.value)
                    }

                    type!!.text = userprofile!!.type

                    if (userprofile!!.type == "Student") {
                        bottomNavigation.setBackgroundColor(getColor(R.color.studentcolor))
                        settingBtn = findViewById(R.id.setting)
                        subappBar!!.setTextColor(getColor(R.color.studenttextcolor))
                        appBar!!.setBackgroundColor(getColor(R.color.studentcolor))
                        subappBar!!.setBackgroundColor(getColor(R.color.studentsubcolor))
                        settingBtn!!.setBackgroundTintList(
                            ContextCompat.getColorStateList(
                                getApplicationContext(),
                                R.color.studentcolor
                            )
                        )
                        layout_nav.setBackgroundColor(getColor(R.color.studentcolor))
                        headnavIC.setImageResource(R.mipmap.ic_studentavatar)
                    }

                    //set current user
                    currentuser = userprofile!!.username
                    currenttype = userprofile!!.type
                    appBar!!.text = "Chat Room"
                    subappBar!!.text = "${userprofile!!.type} : ${userprofile!!.username}"

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
        var token = getSharedPreferences("uname", Context.MODE_PRIVATE)


        //go to log in page when selected log out
        if (id == R.id.menu_logout) {
            drawer!!.closeDrawer(GravityCompat.START)
            var editor = token.edit()
            editor.putString("loginusername", " ")
            editor.commit()

            val userData = User(userprofile!!.username,userprofile!!.password , userprofile!!.type,
                courselistdetail,"")
            dataReference.child(userprofile!!.username).setValue(userData)
            courseList.clear()

            val intent = Intent(this@BodyActivity, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "log out!!!", Toast.LENGTH_SHORT).show()
            finish()
        }

        if (id == R.id.menu_scanner) {
            drawer!!.closeDrawer(GravityCompat.START)
            replaceFragment(ScannerFragment())
            appBar!!.text = "Scanner"
            subappBar!!.text = "${userprofile!!.type} : ${userprofile!!.username}"
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
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onStop() {
        super.onStop()
        startService(Intent(this, NotificationService::class.java))
    }

//    fun closeApp(view: View?) {
//        finish()
//    }



}