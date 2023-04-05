package com.example.walking.recycler

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.walking.databinding.ActivityAddFourCutBinding
import com.example.walking.databinding.AddfourcutItemBinding


class MultiImageViewHolder(val binding: AddfourcutItemBinding): RecyclerView.ViewHolder(binding.root) {


}

class MultiImageAdapter(val datas: List<Uri>, val context: Context, val bindingAdd: ActivityAddFourCutBinding): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MultiImageViewHolder(AddfourcutItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MultiImageViewHolder).binding
        val image_uri = datas.get(position)
        Log.d("park","$image_uri")
        Glide.with(context)
            .load(image_uri)
            .into(binding.addImageItem)

        binding.addImageItem.setOnClickListener {
            bindingAdd.imgPick.setImageURI(image_uri)
        }

    }

    override fun getItemCount(): Int {
        return datas.size ?:0
    }

}