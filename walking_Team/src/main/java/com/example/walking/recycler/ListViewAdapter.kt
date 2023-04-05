package com.example.walking.recycler

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.walking.MyApplication
import com.example.walking.ShowFourCutActivity
import com.example.walking.databinding.ShowlistItemBinding
import com.example.walking.fragment.PhotoFragment
import com.example.walking.model.FireStoreImg


class ListViewHolder(val binding: ShowlistItemBinding): RecyclerView.ViewHolder(binding.root)


class ListViewAdapter(val datas: MutableList<FireStoreImg>, val context: PhotoFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ListViewHolder(ShowlistItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ListViewHolder).binding
        val imgUri = datas.get(position)
        MyApplication.storage.reference
            .child("images")
            .child(imgUri.img1.toString())
            .downloadUrl.addOnCompleteListener {
                Glide.with(context)
                    .load(it.result)
                    .into(binding.showListItem)
            }
        binding.showListItem.setOnClickListener {
            val intent = Intent(context.activity, ShowFourCutActivity::class.java)
            intent.putExtra("docId",imgUri.docId)
            intent.putExtra("img1",imgUri.img1)
            intent.putExtra("img2",imgUri.img2)
            intent.putExtra("img3",imgUri.img3)
            intent.putExtra("img4",imgUri.img4)
            intent.putExtra("tag1",imgUri.tag1)
            intent.putExtra("tag2",imgUri.tag2)
            intent.putExtra("tag3",imgUri.tag3)
            intent.putExtra("email",imgUri.email)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return datas?.size ?:0
    }

}