package com.example.walking.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walking.*
import com.example.walking.adapter.BoardListAdapter
import com.example.walking.adapter.MeetingListAdapter
import com.example.walking.databinding.FragmentBoardBinding
import com.example.walking.model.Board
import com.example.walking.model.BoardListModel
import com.example.walking.model.MeetingListModel
import com.example.walking.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardFragment : Fragment() {
    // 전역 변수에다 뷰 바인딩을 새로 해줌 . -> lateinit var bindg 을 함.
    var username = ""
    var nickname = ""
    lateinit var adapter: BoardListAdapter
    lateinit var binding: FragmentBoardBinding
    lateinit var savedInstanceState: Bundle

    // *onStart 메서드에서 makeRecyclerView 메서드 호출.
    override fun onStart() {
        super.onStart()
        makeRecyclerView()
    }

    // *onCreateView 메서드에서 FragmentBoardBinding 클래스를 이용해 뷰 바인딩을 초기화하고 root 뷰를 반환
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }
    // 새로운 생명주기, 이미지버튼을 클릭하면 글 작성 으로 넘어감 .

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            // Fragment 에서는 (get생략해도됨) - context - (화면안에 들어있는 모든 내용) 을 사용해야함.
            // Fragment 에서는 Activity 에서 하는것처럼 this 를 사용할수 없다.
            // * fab 버튼을 클릭하면 WritingActivity 로 이동하는 코드 작성.
            val intent = Intent(context, WritingActivity::class.java)
            startActivity(intent)
        }
    }

    // *makeRecyclerView() 메서드에서 Firebase 데이터베이스에 접근하고,
    //  username 값을 가져와서 oneUserCall 변수를 이용해 nickname 값을 가져오고
    //  boardListCall 변수를 이용해 게시판 목록을 가져옴.
    fun makeRecyclerView() {
        val database = Firebase.database
        val myRef = database.getReference("username")

        myRef.get().addOnSuccessListener {
            username = it.value.toString()
        }
        val networkService = (context?.applicationContext as MyApplication).networkService
        var oneUserCall = networkService.doGetOneUser(username)
        oneUserCall.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                nickname = response.body()?.nickname.toString()

                val boardListCall = networkService.doGetBoardList()
                boardListCall.enqueue(object : Callback<BoardListModel> {
                    override fun onResponse(
                        call: Call<BoardListModel>,
                        response: Response<BoardListModel>
                    ) {
                        val boardList = response.body()
                        Log.d("song", "boardList : $boardList")
                        // *onResponse() 메서드에서 가져온 nickname 값을 이용해 BoardListAdapter 객체를 초기화하고,
                        //   recyclerView에 어댑터와 구분선을 추가함.
                        adapter = BoardListAdapter(
                            this@BoardFragment,
                            boardList?.boards,
                            nickname,
                            username
                        )
                        adapter.notifyDataSetChanged()


                        binding.recyclerView.adapter = adapter
                        binding.recyclerView.addItemDecoration(
                            DividerItemDecoration(
                                context,
                                LinearLayoutManager.VERTICAL
                            )
                        )
                        adapter.notifyDataSetChanged()


                    }

                    override fun onFailure(call: Call<BoardListModel>, t: Throwable) {
                        call.cancel()
                    }
                })


            }

            // * onFailure() 메서드에서 네트워크 통신 실패시 호출됨.
            override fun onFailure(call: Call<User>, t: Throwable) {
                call.cancel()
            }
        })
    }
}
