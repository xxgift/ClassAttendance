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
        val course = view.findViewById<TextView>(R.id.courseName)
        val content = view.findViewById<TextView>(R.id.postContent)
        val icon1 = view.findViewById<ImageView>(R.id.user_ic1)
        val icon2 = view.findViewById<ImageView>(R.id.user_ic2)
        val time = view.findViewById<TextView>(R.id.timePost)
        val bg = view.findViewById<ImageView>(R.id.post_bg)
        val post = postList[position]
        var popup_user: String? = null
        var popup_content: String? = null
        var popup_course: String? = null
        var popup_date: String? = null

        if(currenttype == "Teacher"){
            if(post.type == "Teacher"){
                icon2.setImageResource(R.mipmap.ic_teacheravatar)
                icon1.visibility = View.INVISIBLE
                bg.setImageResource(R.drawable.rectangleteacher)
            }else {
                icon2.setImageResource(R.mipmap.ic_studentavatar)
                icon1.visibility = View.INVISIBLE
            }
        }
        else{
            if (post.type == "Teacher"){
                icon2.setImageResource(R.mipmap.ic_teacheravatar)
                icon1.visibility = View.INVISIBLE
                bg.setImageResource(R.drawable.rectanglestudent)
                course.setTextColor(Color.parseColor("#004cc3"))
            }else{
                icon2.setImageResource(R.mipmap.ic_studentavatar)
                icon1.visibility = View.INVISIBLE
                course.setTextColor(Color.parseColor("#004cc3"))
            }
        }

//        if (post.type == "Teacher") {
//                icon2.setImageResource(R.mipmap.ic_teacheravatar)
//                icon1.visibility = View.INVISIBLE
//                bg.setImageResource(R.drawable.rectangleteacher)
//        } else {
//            icon2.setImageResource(R.mipmap.ic_studentavatar)
//            icon1.visibility = View.INVISIBLE
////            bg.setImageResource(R.drawable.rectanglestudent)
//        }

        popup_user = post.username
        popup_content = post.content
        popup_date = post.date
        popup_course = post.course


        username.text = "${post.username}"
        course.text = "${post.course}"
        content.text = "${post.content}"
        time.text = "${post.date}"


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