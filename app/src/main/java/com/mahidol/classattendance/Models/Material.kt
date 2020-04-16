package com.mahidol.classattendance.Models

class Material(var courseID: String,var joinID: String, var materialID: String, var date: String, var link: String, var timestamp: String) {
    constructor() : this("","", "", "", "", "")

}