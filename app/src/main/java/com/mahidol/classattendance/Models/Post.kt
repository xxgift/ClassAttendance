package com.mahidol.classattendance.Models


class Post(
    var username: String,
    var type: String,
    var course: String,
    var content: String,
    var date: String,
    var timestamp: String
) {
    constructor() : this(
        "", "", "","","","")
}
