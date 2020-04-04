package com.mahidol.classattendance.Models

class Attendance(var username: String, var type:String, var coursename:String, var date:String, var starttime:String, var durationtime:String, var attendance:String){
    constructor():this("", "","","","","","")
}