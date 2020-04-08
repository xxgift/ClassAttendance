package com.mahidol.classattendance.Models


class Course(var courseID: String, var joinID: String, var owner: String, var courseStatus: String, var whoEnroll: ArrayList<String>, var material: ArrayList<Material>) {
    constructor() : this("", "", "", "Offline", ArrayList<String>(), ArrayList<Material>())
}
