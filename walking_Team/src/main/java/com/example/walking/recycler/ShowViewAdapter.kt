package com.example.walking.recycler

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.walking.MyApplication
import com.example.walking.databinding.ShowfourcutItemBinding
import com.example.walking.model.FireStoreImg
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ShowViewHolder(val binding: ShowfourcutItemBinding):RecyclerView.ViewHolder(binding.root)

class ShowViewAdapter (val datas: MutableList<FireStoreImg>, val context: Context, val datas2: List<Uri>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ShowViewHolder(ShowfourcutItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ShowViewHolder).binding
        val imgUri = datas.get(position)
        val imageUri = datas2.get(position)

        val storage = MyApplication.storage
        val storageRef = storage.reference
        val pathRef = storageRef.child("images")

        val submitProfile = Firebase.storage.reference.child("images/$imgUri")
        Log.d("park", "submitProfile : $submitProfile")
        Log.d("park", "getdownloadUrl : ${submitProfile.downloadUrl}")
        Log.d("park", "imgUri : $imgUri")
        submitProfile.downloadUrl
            .addOnSuccessListener {
                Glide.with(context)
                    .load(it)
                    .override(200,200)
                    .into(binding.showImageItem)
            }
            .addOnFailureListener {
                Glide.with(context)
                    .load(imageUri)
                    .override(200,200)
                    .into(binding.showImageItem)
            }

    }



    override fun getItemCount(): Int {
        return datas.size ?:0
    }
}