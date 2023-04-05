package com.example.walking

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.walking.retrofit.NetworkService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication2 : Application() {


        companion object {

            //add....................................
            var networkService: NetworkService

            val retrofit: Retrofit
                get() = Retrofit.Builder()
                    .baseUrl("https://apis.data.go.kr/6260000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            init {
                networkService = retrofit.create(NetworkService::class.java)
            }
        }

}