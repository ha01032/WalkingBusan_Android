package com.example.walking.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.walking.databinding.ItemMeetingplaceBinding
import com.example.walking.model.Item


class MyViewHolder(val binding: ItemMeetingplaceBinding): RecyclerView.ViewHolder(binding.root) {}




class MyAdapter(val context: Context, val datas: List<Item>?, val activity: AppCompatActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MyViewHolder(
            ItemMeetingplaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding

//        var item: Item = datas?[position] //배열처럼 쓰는걸 권장
//        //본문글씨중에 html 태그문이 포함되어 있는걸 안보이도록 제거 : HtmlCompat.FROM_HTML_MODE_COMPACT
//        var ITEMCNTNTS:String = HtmlCompat.fromHtml(item.ITEMCNTNTS, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
//        holder.binding.maincontent.text = ITEMCNTNTS


        //add......................................
        val user = datas?.get(position)
        binding.placeName.text = user?.MAIN_TITLE
        binding.placeSpot.text = "${user?.LAT},${user?.LNG}"


        val urlImg = user?.MAIN_IMG_THUMB
        Glide.with(context)
            .asBitmap()
            .load(urlImg)
            .into(object : CustomTarget<Bitmap>(200, 200) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    binding.placeImage.setImageBitmap(resource)
                    Log.d("chc", "width : ${resource.width}, height: ${resource.height}")
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })


        binding.selectPlace.setOnClickListener {

            activity.intent.putExtra("placeName", "${binding.placeName.text}")
            activity.intent.putExtra("placeSpot", "${binding.placeSpot.text}")
            activity.intent.putExtra("urlImg", urlImg)
            activity.setResult(AppCompatActivity.RESULT_OK, activity.intent)
            activity.finish()
        }


    }
}
