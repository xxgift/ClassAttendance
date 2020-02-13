package com.mahidol.classattendance.Models


class Checkin(
    var username: String,
    var type: String,
    var beacon: String,
    var content: String,
    var date: String,
    var timestamp: String
) {
    constructor() : this(
        "", "", "","","","")
}
