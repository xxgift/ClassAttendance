package com.mahidol.classattendance.Models


//current data
var currentuser: String? = null
var currentcourse: String? = null
var currenttype: String? = null
var currentstatus: String = ""
var currentImei: String = ""
var currentdurationtime: Long = 0
var courselistdetail: HashMap<String, Course> = HashMap<String, Course>()
var courseList: ArrayList<Course> = ArrayList<Course>()

var isScanning:Boolean = false