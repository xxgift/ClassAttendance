package com.mahidol.classattendance.Models



class Course(
    var courseID: String,
    var joinID:String,
    var owner:String ,
    var courseStatus:String){
    constructor():this("","","","")
}
