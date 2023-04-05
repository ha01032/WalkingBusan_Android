package com.example.walking.adapter


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.walking.ChatActivity
import com.example.walking.MyApplication
import com.example.walking.R
import com.example.walking.databinding.ItemMeetingListBinding
import com.example.walking.fragment.MeetingFragment
import com.example.walking.model.Meeting
import com.example.walking.model.User
import com.example.walking.model.Userinmeeting
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MeetingListViewHolder(val binding: ItemMeetingListBinding): RecyclerView.ViewHolder(binding.root)

class MeetingListAdapter (val context: MeetingFragment, val datas:List<Meeting>?, val nickname: String, val username: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var meetingList: ArrayList<Meeting>? = null
    private var num=datas?.size
    var chatmemberList: List<User>? = null
    var meeting_id: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = MeetingListViewHolder(ItemMeetingListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    //text 가져올 때,
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MeetingListViewHolder).binding
        //여행 정보 받아와야함

        var meeting = datas?.get(position)
        Log.d("ddddd", "${meeting?.meeting_id}")
        binding.meetingListTitle.text = meeting?.meeting_title
        if(username==meeting?.email){
            binding.admin.visibility = View.VISIBLE
            binding.meetingListTitle.setTextColor(ContextCompat.getColor(context.requireContext(), R.color.purple_200))
        }
        else{binding.admin.visibility = View.GONE}
//        binding.meetingListMem.text = meeting?.member
//        Log.d("test", "${meeting?.member}")
        binding.meetingListStartDate.text = meeting?.start_date
        binding.meetingListEndDate.text = meeting?.end_date

        Glide.with(context)
            .asBitmap()
            .load(meeting?.meeting_place_imgurl)
            .into(object : CustomTarget<Bitmap>(70, 70) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    binding.placeImage.setImageBitmap(resource)
                    Log.d("chc", "width : ${resource.width}, height: ${resource.height}")
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }
            })


        meeting_id = meeting!!.meeting_id
        val networkService = (context.context?.applicationContext as MyApplication).networkService
        var dialog_listener = DialogInterface.OnClickListener { dialog, which ->

            var userinmeeting = Userinmeeting(username, meeting_id)
            val userinmeetingInsertCall = networkService.doInsertUserinmeeting(userinmeeting)
            userinmeetingInsertCall.enqueue(object: Callback<Userinmeeting> {
                override fun onResponse(call: Call<Userinmeeting>, response: Response<Userinmeeting>) {
                    val userinmeeting = response.body()
                    Log.d("dialog", "$userinmeeting")

                    binding.chatentrybtn.visibility=View.VISIBLE

                    val meetingListCall = networkService.doGetChatMemberList(meeting?.meeting_id)
                    meetingListCall.enqueue(object: Callback<List<User>>{
                        override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                            chatmemberList = response.body()
                            binding.membercount.text = "${chatmemberList?.size}명 참여중"

                        }
                        override fun onFailure(call: Call<List<User>>, t: Throwable) {
                            Log.d("meetinglistadapter", "실패")
                        }
                    })



                }
                override fun onFailure(call: Call<Userinmeeting>, t: Throwable) {
                    Log.d("dialog", "실패")
                }
            })

        }

        var userinmeeting :Userinmeeting? = null
        var userinmeeting_val = meeting_id.toString().plus("-".plus(username))
        Log.d("chc", userinmeeting_val)
        val oneuserinmeetingCall = networkService.doGetOneUserinmeeting(userinmeeting_val)
        oneuserinmeetingCall.enqueue(object: Callback<Userinmeeting> {
            override fun onResponse(call: Call<Userinmeeting>, response: Response<Userinmeeting>) {
                userinmeeting = response.body()
                Log.d("chc2", "$userinmeeting")
                if (userinmeeting != null) {
                    binding.chatentrybtn.visibility=View.VISIBLE
                }


            }
            override fun onFailure(call: Call<Userinmeeting>, t: Throwable) {
                Log.d("dialog", "실패")
            }
        })







        //Item 누를때 조건문
        holder.binding.meetingListItem.setOnClickListener {
            //해당 유저가 해당 모임에 참가했는지 확인하기 위한 메서드
//                var userinmeeting_val = meeting_id.toString().plus("-".plus(username))
//                Log.d("chc", userinmeeting_val)
//                val oneuserinmeetingCall = networkService.doGetOneUserinmeeting(userinmeeting_val)
//                oneuserinmeetingCall.enqueue(object: Callback<Userinmeeting> {
//                    override fun onResponse(call: Call<Userinmeeting>, response: Response<Userinmeeting>) {
//                        userinmeeting = response.body()
//                        Log.d("dialog", "$userinmeeting")
//
//
//
//                    }
//                    override fun onFailure(call: Call<Userinmeeting>, t: Throwable) {
//                        Log.d("dialog", "실패")
//                    }
//                })
            Log.d("ddddd", "${userinmeeting}")
            if(  binding.chatentrybtn.visibility==View.VISIBLE){
                val intent = Intent(context.activity, ChatActivity::class.java) //fragment -> activity Intent
                intent.putExtra("title", datas?.get(position)?.meeting_title)
                intent.putExtra("nickname",nickname)
                intent.putExtra("username",username)
                context?.startActivity(intent)
            }
            else {
                AlertDialog.Builder(context.context).run {
                    setTitle("채팅방 참가")
                    setMessage(
                        """
채팅방에 참가하시겠습니까?

이름: ${meeting?.meeting_title}
내용: ${meeting?.meeting_content}
장소: ${meeting?.meeting_place_name}
기간: ${meeting.start_date}~${meeting.end_date}
"""
                    )
                    setPositiveButton("아니오", null)
                    setNegativeButton("네", dialog_listener)
                    show()
                }
            }
        }




        val meetingListCall = networkService.doGetChatMemberList(meeting?.meeting_id)
        meetingListCall.enqueue(object: Callback<List<User>>{
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                chatmemberList = response.body()
                binding.membercount.text = "${chatmemberList?.size}명 참여중"

                if(username==meeting?.email){
                    binding.admin.visibility = View.VISIBLE
                    binding.meetingListTitle.setTextColor(ContextCompat.getColor(context.requireContext(), R.color.purple_200))
                }

            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d("meetinglistadapter", "실패")
            }
        })







        //채팅방 입장 버튼 누를때 채팅방으로 이동
        holder.binding.chatentrybtn.setOnClickListener {






            val intent = Intent(context.activity, ChatActivity::class.java) //fragment -> activity Intent
            intent.putExtra("title", datas?.get(position)?.meeting_title)
            intent.putExtra("nickname",nickname)
            context?.startActivity(intent)
        }










    }
    override fun getItemCount(): Int {
        return num ?:0
    }


}
