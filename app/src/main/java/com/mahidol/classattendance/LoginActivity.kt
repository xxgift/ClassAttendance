package com.mahidol.classattendance

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.gson.Gson
import com.mahidol.classattendance.Helper.HTTPHelper
import kotlinx.android.synthetic.main.login.*
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.mahidol.classattendance.Models.*


class LoginActivity : AppCompatActivity() {
    //firebase database URL
    var url = "https://studenttracking-47241.firebaseio.com/UserProfile/"
    var uname: String? = null
    var pname: String? = null
    var userprofile: User? = null
    lateinit var imei: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var token = getSharedPreferences("uname", Context.MODE_PRIVATE)

//        val manager = getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val permission = ContextCompat.checkSelfPermission(this,
//            Manifest.permission.ACCESS_WIFI_STATE)
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_WIFI_STATE), 1)
//        }
//        var mac = manager.connectionInfo.macAddress


        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val permissionCall = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_PHONE_STATE)
        val permissionLocation = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionBluetooth = ContextCompat.checkSelfPermission(this,
            Manifest.permission.BLUETOOTH)

        if (permissionCall != PackageManager.PERMISSION_GRANTED || permissionLocation != PackageManager.PERMISSION_GRANTED || permissionBluetooth != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH), 1)
        }

        if (permissionCall == PackageManager.PERMISSION_GRANTED && permissionLocation == PackageManager.PERMISSION_GRANTED) {
            setContentView(R.layout.login)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            imei = telephonyManager.imei
            println("${token.getString("loginusername", " ")}  fffffffffffffffffffffffffff")
            if (token.getString("loginusername", " ") != " ") {
                val intent = Intent(this@LoginActivity, BodyActivity::class.java)
                startActivity(intent)
                intent.putExtra("uname", token.getString("uname", " "))
                finish()
                println("222222222222222222222222222222222222")
            }

            //if user have an account already then go to activity_body page (Home page)
            login_btn.setOnClickListener {

                uname = login_username.text.toString()
                pname = login_password.text.toString()

                //check username and password must not be null
                if (uname.isNullOrEmpty()) {
                    login_username.error = "Please enter a message"
                }
                if (pname.isNullOrEmpty()) {
                    login_password.error = "Please enter a message"
                }
                if (!uname.isNullOrEmpty() && !pname.isNullOrEmpty()) {
                    var asyncTask = object : AsyncTask<String, String, String>() {

                        override fun onPreExecute() {
                            currenttype = null
                            Toast.makeText(this@LoginActivity, "Please wait...", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun doInBackground(vararg p0: String?): String {
                            val helper = HTTPHelper()
                            return helper.getHTTPData(url + uname + ".json")
                        }

                        override fun onPostExecute(result: String?) {
                            if (result != "null") {
                                userprofile = Gson().fromJson(result, User::class.java)
                                currentuser = userprofile!!.username
                                currentImei = imei

                                //check username and password is matched
                                if (userprofile!!.password == pname) {

                                    if (userprofile!!.imei == "") {
                                        FirebaseDatabase.getInstance().getReference("UserProfile").child(userprofile!!.username).setValue(User(userprofile!!.username, userprofile!!.password, userprofile!!.type, userprofile!!.courselist, imei))
                                        currenttype = userprofile!!.type
                                        val intent = Intent(this@LoginActivity, BodyActivity::class.java)
                                        //transfer value of username to scan
                                        intent.putExtra("uname", uname)

                                        var editor = token.edit()
                                        editor.putString("loginusername", uname)
                                        editor.commit()
                                        applicationContext.startActivity(intent)
                                    } else {
                                        if (userprofile!!.imei == imei) {
                                            currenttype = userprofile!!.type
                                            val intent = Intent(this@LoginActivity, BodyActivity::class.java)
                                            //transfer value of username to scan
                                            intent.putExtra("uname", uname)


                                            var editor = token.edit()
                                            editor.putString("loginusername", uname)
                                            editor.commit()



                                            applicationContext.startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "This account is logged in on another device ",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Username or Password is not matched",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } else {
                                login_username.error = "Username not found"
                            }
                            login_username.text = null
                            login_password.text = null
                        }

                    }
                    asyncTask.execute()


//                Toast.makeText(this@LoginActivity,">>>>>IMEI:$imei",Toast.LENGTH_SHORT).show()
                }
            }

            //if user doesn't have an account yet then go to register page
            creatnewacc_btn.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                login_username.text = null
                login_password.text = null
            }

        }else{
            Toast.makeText(applicationContext,"You need to allow PERMISSION_CALL and LOCATION",Toast.LENGTH_LONG).show()
            finish()
        }
    }

}