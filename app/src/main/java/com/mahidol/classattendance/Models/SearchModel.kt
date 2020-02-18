package com.mahidol.classattendance.Models
import ir.mirrajabi.searchdialog.core.Searchable
class SearchModel (private val mTitle:String):Searchable{
    override fun getTitle():String{
        return mTitle
    }
}
