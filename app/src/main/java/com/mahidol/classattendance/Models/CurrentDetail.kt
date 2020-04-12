package com.mahidol.classattendance.Models


//current data
var currentuser: String? = null
var currentcourse: String? = null
var currentcontent: String? = null
var currenttime: String? = null
var currenttype: String? = null
var currentstatus: String = ""
var currenttotaltime: String? = null
var courselistdetail: HashMap<String, Course> = HashMap<String, Course>()
var courseList: ArrayList<Course> = ArrayList<Course>()
var onlinecourse: ArrayList<Course> = ArrayList<Course>()
var currentImei: String = ""
var isScanning:Boolean = false