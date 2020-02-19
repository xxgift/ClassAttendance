package com.mahidol.classattendance.Adapter



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R


class ChatroomAdapter(
    val mContext: Context,
    val layoutResId: Int,
    val postList: ArrayList<Post>
) :
    ArrayAdapter<Post>(mContext, layoutResId, postList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)
        val username = view.findViewById<TextView>(R.id.userName)
        val course = view.findViewById<TextView>(R.id.courseName)
        val content = view.findViewById<TextView>(R.id.postContent)
        val icon1 = view.findViewById<ImageView>(R.id.user_ic1)
        val icon2 = view.findViewById<ImageView>(R.id.user_ic2)
        val time = view.findViewById<TextView>(R.id.timePost)
        val post = postList[position]

        if (post.type == "Teacher") {
            icon2.setImageResource(R.mipmap.ic_teacheravatar)
            icon1.visibility = View.INVISIBLE
        } else {
            icon2.visibility = View.INVISIBLE
            icon1.setImageResource(R.mipmap.ic_studentavatar)
        }

        username.text = "${post.username}"
        course.text = "${post.course}"
        content.text = "${post.content}"
        time.text = "${post.date}"


        return view
    }
}