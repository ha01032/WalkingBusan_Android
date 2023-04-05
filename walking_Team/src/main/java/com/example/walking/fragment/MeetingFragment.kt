package com.example.walking.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walking.InputMeetingActivity
import com.example.walking.MyApplication
import com.example.walking.adapter.MeetingListAdapter
import com.example.walking.databinding.FragmentMeetingBinding
import com.example.walking.model.MeetingListModel
import com.example.walking.model.User

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeetingFragment : Fragment() {
    lateinit var binding: FragmentMeetingBinding
    lateinit var adapter: MeetingListAdapter
    val TAG : String = "MeetingFragment"
    var username=""
    var nickname=""

    lateinit var inflater: LayoutInflater
    lateinit var container: ViewGroup
    lateinit var savedInstanceState: Bundle

    companion object { //Java Static 유사. 멤버 변수나 함수를 클래스 이름으로 접근
        fun newInstance(): MeetingFragment {
            return MeetingFragment()
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d("lsy","호출 여부 확인")
        makeRecylerView()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMeetingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addmeetingfab.setOnClickListener {
            val intent = Intent(context, InputMeetingActivity::class.java)

            startActivity(intent)
        }

    }

    fun makeRecylerView() {
        val database = Firebase.database
        val myRef = database.getReference("username")

        myRef.get().addOnSuccessListener {
            username=it.value.toString()
            Log.d(TAG,"firebase===========================${it.value.toString()}")
            val networkService = (context?.applicationContext as MyApplication).networkService

            var oneUserCall = networkService.doGetOneUser(username)
            oneUserCall.enqueue(object: Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    nickname = response.body()?.nickname.toString()
                    Log.d(TAG, "===================nickname: $nickname")

                    val meetingListCall = networkService.doGetMeetingList()
                    meetingListCall.enqueue(object: Callback<MeetingListModel>{
                        override fun onResponse(call: Call<MeetingListModel>, response: Response<MeetingListModel>) {
                            val meetingList = response.body()
                            Log.d(TAG, "$meetingList")

                            adapter = MeetingListAdapter(this@MeetingFragment, meetingList?.meetings, nickname, username)
                            adapter.notifyDataSetChanged()


                            binding.meetingListRecyclerView.adapter = adapter
                            binding.meetingListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                            adapter.notifyDataSetChanged()


                        }
                        override fun onFailure(call: Call<MeetingListModel>, t: Throwable) {
                            Log.d(TAG, "실패")
                        }
                    })


                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }
            })



        }
    }




}