package com.mahidol.classattendance.Models

class User( var username: String, var password:String,var type: String, var courselist:ArrayList<Course>?){
    constructor():this("", "","", null)
}
