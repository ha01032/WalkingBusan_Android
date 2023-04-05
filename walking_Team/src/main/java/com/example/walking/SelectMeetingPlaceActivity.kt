package com.example.walking

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walking.adapter.MyAdapter
import com.example.walking.databinding.ActivitySelectMeetingPlaceBinding
import com.example.walking.model.Walking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectMeetingPlaceActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelectMeetingPlaceBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectMeetingPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val serviceKey = "ySW6Ei6Oncs0opwRU/DIeWdmLYyXDK7LXEybY0cGhExFc10LY76oPuVHH5nXFB0FM7A3I1QKOvuiIbtAkIirhw=="

        val userListCall = MyApplication2.networkService.getList(serviceKey, 1, 46, "json")

        Log.d("chc", "url:" + userListCall.request().url().toString())

        userListCall.enqueue(object : Callback<Walking> {

            override fun onResponse(call: Call<Walking>, response: Response<Walking>) {


                val userList = response.body()
                Log.d("chc", "userList data 값 : ${userList?.getWalkingKr?.item}")
                //.......................................
                binding.recyclerView.adapter = MyAdapter(this@SelectMeetingPlaceActivity, userList?.getWalkingKr?.item, this@SelectMeetingPlaceActivity)

                binding.recyclerView.addItemDecoration(
                    DividerItemDecoration(this@SelectMeetingPlaceActivity, LinearLayoutManager.VERTICAL)
                )
            }

            override fun onFailure(call: Call<Walking>, t: Throwable) {
                call.cancel()
            }
        })













    }
}