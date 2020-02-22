package com.mahidol.classattendance

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.mahidol.classattendance.Helper.HTTPHelper
import com.mahidol.classattendance.Models.User
import com.mahidol.classattendance.Models.courselistdetail
import com.mahidol.classattendance.Models.currenttype
import kotlinx.android.synthetic.main.login.*

class LoginActivity : AppCompatActivity() {
    //firebase database URL
    var url = "https://studenttracking-47241.firebaseio.com/UserProfile/"
    var uname: String? = null
    var pname: String? = null
    var userprofile: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

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
                            courselistdetail = userprofile!!.courselist

                            //check username and password is matched
                            if (userprofile!!.password == pname) {
                                val intent = Intent(this@LoginActivity, BodyActivity::class.java)
                                //transfer value of username to scan
                                intent.putExtra("uname", uname)
                                applicationContext.startActivity(intent)
                                currenttype = userprofile!!.type

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


            }
        }

        //if user doesn't have an account yet then go to register page
        creatnewacc_btn.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            login_username.text = null
            login_password.text = null
        }

    }
}