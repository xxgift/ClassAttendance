package com.mahidol.classattendance.Adapter



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mahidol.classattendance.Models.Checkin
import com.mahidol.classattendance.R


class HomeAdapter(
    val mContext: Context,
    val layoutResId: Int,
    val checkinList: ArrayList<Checkin>
) :
    ArrayAdapter<Checkin>(mContext, layoutResId, checkinList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)
        val username = view.findViewById<TextView>(R.id.userName)
        val placename = view.findViewById<TextView>(R.id.beaconPlace)
        val content = view.findViewById<TextView>(R.id.checkinContent)
        val icon = view.findViewById<ImageView>(R.id.user_ic)
        val time = view.findViewById<TextView>(R.id.timeCheckin)
        val user = checkinList[position]

        if (user.type == "Teacher") {
            icon.setImageResource(R.mipmap.ic_teacherlove)
        }else{
            icon.setImageResource(R.mipmap.ic_studentlove)
        }

        username.text = "${user.username}"
        placename.text = "${user.beacon}"
        content.text = "${user.content}"
        time.text = "${user.date}"


        return view
    }
}