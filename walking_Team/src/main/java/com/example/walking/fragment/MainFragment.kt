package com.example.walking.fragment


import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walking.MyApplication2
import com.example.walking.databinding.FragmentMainBinding
import com.example.walking.model.Walking
import com.example.walking.recycler.MyAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainFragment : Fragment() {


//    var firestore : FirebaseFirestore? = null
//            override fun onCreateView(
//            inflater: LayoutInflater, container: ViewGroup?,
//            savedInstanceState: Bundle?
//        ): View? {
//
//          var view = layoutInflater.from(activity).inflate(R.layout.fragment_main,container,false)
//                firestore = FirebaseFirestore.getInstance()
//                view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
//                view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManger(activity)
//                return view
//            }
//    inner class DetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    }
//
//    val likeDTOs: ArrayList<LikeDTO> = arrayListOf()
//    val likeUidList: ArrayList<String> = arrayListOf()
//
//    init {
//
//        //db 접근해서 데이터 받아올 수 있게 쿼리 만들기
//        firestore?.collection("imges")?.orderBy("timstamp")?.addSnapshotListener {querySnapshot, _ ->
//            likeDTOs = ArrayList() //데이터베이스 초기화
//            likeUidList = ArrayList() // 초기화2
//            for (snapshot in querySnapshot!!.documents) //넘어오는 데이터들 하나하나 받아들이기 시작
//
//            {
//                var item = snapshot.toObject(likeDTO::class.java)
//                likeDTOs.add(item!!) // 데이터베이스에 담아줌
//                likeDTOs.add(snapshot.id)
//            }
//            notifyDataSetChanged() //새로고침 해줌
//        }
//    }
//    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
//        var view = LayoutInflater.from(p0.context).inflate(R.layout.item_main,p0,false)
//        return CustomViewHolder(view)
//    }
//
//    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)
//
////recyclerview 넘겨주기
//
//    override fun getItemCount(): Int {
//        return likeDTOs.size
//    }
//    //서버에서 넘어오는 데이터를 맵핑시키는 부분
//    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
//        var viewholder = (p0 as CustomViewHolder).itemView
////유저아이디 맵핑
//        viewholder.like_test.text = likeDTOs!![p1].email
//////이미지 맵핑
////        Glide.with(p0.itemView.context).load(likeDTOs!![p1].imageUrl).into(viewholder.detailviewitem_imageview_content)
////좋아요 카운터 맵핑
//        viewHolder.likeCount.text = "좋아요 " + likeDTOs!![p1].likeCount
//       }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        var recyclerViewState: Parcelable?
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        val serviceKey =
            "ySW6Ei6Oncs0opwRU/DIeWdmLYyXDK7LXEybY0cGhExFc10LY76oPuVHH5nXFB0FM7A3I1QKOvuiIbtAkIirhw=="
//            itemBinding=ItemMainBinding.inflate(layoutInflater)
//            itemBinding.maincontent.text

        val userListCall = MyApplication2.networkService.getList(serviceKey, 1, 46, "json")
        Log.d("chc", "url:" + userListCall.request().url().toString())

        userListCall.enqueue(object : Callback<Walking> {

            override fun onResponse(call: Call<Walking>, response: Response<Walking>) {

                recyclerViewState = binding.recyclerView.layoutManager?.onSaveInstanceState()
                val userList = response.body()
//                    Log.d("chc", "userList data 값 : ${userList?.getWalkingKr?.item}")
                //.......................................
                binding.recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                binding.recyclerView.adapter =
                    MyAdapter(activity as Context, userList?.getWalkingKr?.item)

                binding.recyclerView.addItemDecoration(
                    DividerItemDecoration(activity as Context, LinearLayoutManager.VERTICAL)
                )
            }

            override fun onFailure(call: Call<Walking>, t: Throwable) {
                call.cancel()
            }
        })

        return binding.root
    }

}



