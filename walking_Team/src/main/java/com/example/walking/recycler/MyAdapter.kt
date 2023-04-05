package com.example.walking.recycler

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.walking.databinding.ItemMainBinding
import com.example.walking.model.Item
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MyViewHolder(val binding: ItemMainBinding): RecyclerView.ViewHolder(binding.root) {
    var username=""

//    var mainView: View? = null

    init {
        val database = Firebase.database
        val myRef = database.getReference("username")


        myRef.get().addOnSuccessListener {
            username=it.value.toString()
        }

        binding.likebtn.setOnClickListener{
            binding.likebtn.visibility= View.GONE
            binding.likefill.visibility=View.VISIBLE
        }
        binding.likefill.setOnClickListener{
            binding.likefill.visibility= View.GONE
            binding.likebtn.visibility=View.VISIBLE


        }
    }

}
class MyAdapter(val context: Context, val datas: List<Item>?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int{
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = MyViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as MyViewHolder).binding


        //add......................................
        val user = datas?.get(position)
        binding.username.text = user?.MAIN_TITLE
        binding.maincontent.text = user?.ITEMCNTNTS

        binding.mapbtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.kr/maps/search/${user?.LAT}, ${user?.LNG}"))

            context.startActivity(intent)
        }

        val like = datas?.get(position)
//        binding.likebtn.text = like?.UC_SEQ.toString()

//        binding.likeCount.text = "좋아요" + like?.UC_SEQ.toString()

//
//        // 좋아요 이벤트
//        MyViewHolder.likebtn.setOnClickListener { likeEvent(position) }
//
//        //좋아요 버튼 설정
//        if (likeDTOs[position].likes.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {
//
//            MyViewHolder.likebtn.setImageResource(R.drawable.like_fill)
//
//        } else {
//
//            MyViewHolder.likebtn.setImageResource(R.drawable.like)
//        }
//        //좋아요 카운터 설정
//        MyViewHolder.likeCount.text = "좋아요 " + likeDTOs[position].favoriteCount
//
//
//    }
//    //좋아요 이벤트 기능
//    private fun likeEvent(position: Int) {
//        var auth : FirebaseAuth? = null
//        var firestore : FirebaseFirestore? = null
//
//        auth = FirebaseAuth.getInstance()
//        firestore = FirebaseFirestore.getInstance()
//
//        var tsDoc = firestore?.collection("images")?.document(likeUidList[position])
//        firestore?.runTransaction { transaction ->
//
//            val uid = FirebaseAuth.getInstance().currentUser!!.uid
//            val likeDTO = transaction.get(tsDoc!!).toObject(LikeDTO::class.java)
//
//            if (likeDTO!!.likes.containsKey(uid)) {
//                // Unstar the post and remove self from stars
//                likeDTO?.likeCount = likeDTO?.likeCount!! - 1
//                likeDTO?.likes.remove(uid)
//
//            } else {
//                // Star the post and add self to stars
//                likeDTO?.likeCount = likeDTO?.likeCount!! + 1
//                likeDTO?.likes[uid] = true
////                favoriteAlarm(contentDTOs[position].uid!!)
//            }
//            transaction.set(tsDoc, likeDTO)
//        }
//    }
//}


        //본문글씨중에 html 태그문이 포함되어 있는걸 안보이도록 제거하는 코드 : HtmlCompat.FROM_HTML_MODE_COMPACT
        var ITEMCNTNTS:String = HtmlCompat.fromHtml(user!!.ITEMCNTNTS, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
        holder.binding.maincontent.text = ITEMCNTNTS


      //이미지 크기, 화질조정
        val urlImg = user?.MAIN_IMG_THUMB
        Glide.with(context)
            .asBitmap()
            .load(urlImg)
            .into(object : CustomTarget<Bitmap>(400, 350) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    binding.mainImg.setImageBitmap(resource)
                    Log.d("chc", "width : ${resource.width}, height: ${resource.height}")
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }
            })
    }



    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}