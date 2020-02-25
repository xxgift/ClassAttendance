package com.mahidol.classattendance.Adapter


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.mahidol.classattendance.Fragments.popup_postdetail_Fragment
import com.mahidol.classattendance.Models.*
import com.mahidol.classattendance.R


class ChatroomAdapter(
    val mContext: Context,
    val mActivity: FragmentActivity,
    val layoutResId: Int,
    val postList: ArrayList<Post>
) :
    ArrayAdapter<Post>(mContext, layoutResId, postList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)
        val username = view.findViewById<TextView>(R.id.userName)
        val username2 = view.findViewById<TextView>(R.id.userName2)
        val course1 = view.findViewById<TextView>(R.id.courseName)
        val course2 = view.findViewById<TextView>(R.id.courseName2)
        val content1 = view.findViewById<TextView>(R.id.postContent)
        val content2 = view.findViewById<TextView>(R.id.postContent2)
        val icon1 = view.findViewById<ImageView>(R.id.user_ic1)
        val icon2 = view.findViewById<ImageView>(R.id.user_ic2)
        val time1 = view.findViewById<TextView>(R.id.timePost)
        val time2 = view.findViewById<TextView>(R.id.timePost2)
        val bg1 = view.findViewById<ImageView>(R.id.post_bg)
        val bg2  = view.findViewById<ImageView>(R.id.post_bg2)
        val post = postList[position]
        var popup_user: String? = null
        var popup_content: String? = null
        var popup_course: String? = null
        var popup_date: String? = null

        if(currenttype == "Teacher"){
            if(post.type == "Teacher"){
                icon1.setImageResource(R.mipmap.ic_teacherchat)
                username2.visibility = View.INVISIBLE
                icon2.visibility = View.INVISIBLE
                time2.visibility = View.INVISIBLE
                course2.visibility = View.INVISIBLE
                content2.visibility = View.INVISIBLE
                bg2.visibility = View.INVISIBLE

            }else {
                icon2.setImageResource(R.mipmap.ic_studentchat)
                username.visibility = View.INVISIBLE
                icon1.visibility = View.INVISIBLE
                time1.visibility = View.INVISIBLE
                course1.visibility = View.INVISIBLE
                content1.visibility = View.INVISIBLE
                bg1.visibility = View.INVISIBLE
                course2.setTextColor(Color.parseColor("#A254F2"))
            }
        }
        else{
            if(post.type == "Teacher"){
                icon1.setImageResource(R.mipmap.ic_teacherchat)
                username2.visibility = View.INVISIBLE
                icon2.visibility = View.INVISIBLE
                time2.visibility = View.INVISIBLE
                course2.visibility = View.INVISIBLE
                content2.visibility = View.INVISIBLE
                bg2.visibility = View.INVISIBLE
                course1.setTextColor(Color.parseColor("#004cc3"))
            }else{
                icon2.setImageResource(R.mipmap.ic_studentchat)
                username.visibility = View.INVISIBLE
                icon1.visibility = View.INVISIBLE
                time1.visibility = View.INVISIBLE
                course1.visibility = View.INVISIBLE
                content1.visibility = View.INVISIBLE
                bg1.visibility = View.INVISIBLE
            }
        }

        popup_user = post.username
        popup_content = post.content
        popup_date = post.date
        popup_course = post.course


        username.text = "${post.username}"
        username2.text = "${post.username}"
        course1.text = "${post.course}"
        content1.text = "${post.content}"
        time1.text = "${post.date}"
        course2.text = "${post.course}"
        content2.text = "${post.content}"
        time2.text = "${post.date}"


        view.setOnClickListener {
            showDialog(view,popup_user,popup_content,popup_course,popup_date)

        }

        return view
    }

    private fun showDialog(view: View,user:String,content:String,course: String,date:String) {
        val applypopup = popup_postdetail_Fragment(view,user,content,course,date)
        applypopup.show(mActivity!!.supportFragmentManager, "exampleBottomSheet")
    }
}