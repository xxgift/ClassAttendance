package com.mahidol.classattendance.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mahidol.classattendance.Models.User
import com.mahidol.classattendance.R


class UserAdapter(val mContext: Context, val layoutResId: Int, val courselist_courselistView: List<User>):
    ArrayAdapter<User>(mContext, layoutResId, courselist_courselistView) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)
        val username = view.findViewById<TextView>(R.id.msgView)
        val user = courselist_courselistView[position]

        username.text = "Username: ${user.username} Type:${user.type}"

        return view
    }
}