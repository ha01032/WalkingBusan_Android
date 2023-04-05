package com.example.walking.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.walking.R
import com.example.walking.databinding.FragmentMainBinding
import com.example.walking.databinding.ItemMainBinding
import com.example.walking.model.LikeDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class LikeViewHolder(val binding : ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class LikeAdapter(var context : Context, val likes : MutableList<LikeDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var binding : FragmentMainBinding
    lateinit var adapter : LikeAdapter
    lateinit var auth : FirebaseAuth
    var firestore: FirebaseFirestore? = null


    val likeDTOs: ArrayList<LikeDTO>
    val likeUidList: ArrayList<String>

    init {
        likeDTOs = ArrayList()
        likeUidList = ArrayList()
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        firestore?.collection("users")?.document(uid!!)?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var likeDTO = task.result.toObject(LikeDTO::class.java)
                if (likeDTO?.likes != null) {
//                    getCotents(likeDTO?.likes)
                }
            }
        }
    }
//    fun getCotents(likes: MutableMap<String, Boolean>?) {
//        imagesSnapshot = firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//            likeDTOs.clear()
//            likeUidList.clear()
//            if (querySnapshot == null) return@addSnapshotListener
//            for (snapshot in querySnapshot!!.documents) {
//                var item = snapshot.toObject(LikeDTO::class.java)!!
//                println(item.uid)
//                if (likes?.keys?.contains(item.uid)!!) {
//                    likeDTOs.add(item)
//                    likeUidList.add(snapshot.id)
//                }
//            }
//            notifyDataSetChanged()
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LikeViewHolder(
            ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)) }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val binding = (holder as LikeViewHolder).binding

        val model = likes!![position]
//        val date: String = SimpleDateFormat("yyyy.MM.dd").format(model.createDate)
        val username: String
        auth = Firebase.auth
        val loginUser = auth.currentUser?.displayName

//        // 유저 아이디
//        binding.username.text = likeDTOs[position].email
//
//        // 좋아요 이벤트
//        binding.likeTest.setOnClickListener { likeEvent(position) }

//        //좋아요 버튼 설정
//        if (likeDTOs[position].likes.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {
//            binding.likeTest.setImageResource(R.drawable.like_fill)
//
//        } else {
//
//            binding.likeTest.setImageResource(R.drawable.like)
//        }
//        //좋아요 카운터 설정
//        binding.likeCount.text = "좋아요 " + likeDTOs[position].likeCount

//        binding.likeTest.setOnClickListener {
//            val intent = Intent(LikeAdapter, MainFragment::class.java)
//            intent.putExtra("likeUid", likeUidList[position])
//            intent.putExtra("destinationUid", likeDTOs[position].uid)
//            startActivity(intent)
//        }

    }


//좋아요 이벤트 기능
private fun likeEvent(position: Int) {
    var tsDoc = firestore?.collection("likes")?.document(likeUidList[position])
    firestore?.runTransaction { transaction ->

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val likeDTO = transaction.get(tsDoc!!).toObject(LikeDTO::class.java)

        if (likeDTO!!.likes.containsKey(uid)) {
            // Unstar the post and remove self from stars
            likeDTO?.likeCount = likeDTO?.likeCount!! - 1
            likeDTO?.likes?.remove(uid)

        } else {
            // Star the post and add self to stars
            likeDTO?.likeCount = likeDTO?.likeCount!! + 1
            likeDTO?.likes?.set(uid, true)
//            favoriteAlarm(contentDTOs[position].uid!!)
        }
        transaction.set(tsDoc, likeDTO)
       return@runTransaction
    }

 }



    override fun getItemCount(): Int {
        return likes.size
    }
inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

