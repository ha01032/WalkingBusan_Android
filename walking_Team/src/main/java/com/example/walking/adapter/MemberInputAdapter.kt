package com.example.walking.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.walking.MyApplication
import com.example.walking.databinding.ChatmemberInputBinding
import com.example.walking.model.User


class InputHolder(val binding: ChatmemberInputBinding): RecyclerView.ViewHolder(binding.root)

class MemberInputAdapter(val memberList: List<User>?, val nickname: String?, val admin: String?, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    = InputHolder(ChatmemberInputBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as InputHolder).binding
        val member = memberList?.get(position)
        if(member?.nickname==nickname) {
            Log.d("test", "inputAdapter=====================$nickname")
            binding.chatmemberName.text = "나"
            if(member?.nickname == admin){binding.chatmemberName.text = "나 (방장)"}
        } else {
            binding.chatmemberName.text = member?.nickname
            if(member?.email == admin){binding.chatmemberName.text = "${member?.nickname} (방장)"}
        }




        val imgRef = MyApplication.storage.reference.child("profiles/${member?.profile_id}.jpg")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Glide.with(context)
                    .load(task.result)
                    .into(binding.memberProfile)
            }
        }





    }

    override fun getItemCount(): Int {
            return memberList?.size ?:0
    }

}