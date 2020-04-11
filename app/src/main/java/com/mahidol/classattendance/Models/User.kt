package com.mahidol.classattendance.Models

class User(var username: String, var password: String,
           var type: String, var courselist: HashMap<String, Course>,
           var imei: String) {
    constructor() : this("", "", "", HashMap<String, Course>(), "null")
}
