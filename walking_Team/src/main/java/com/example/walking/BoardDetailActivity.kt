package com.example.walking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.walking.databinding.ActivityBoardDetailBinding

class BoardDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityBoardDetailBinding
//    lateinit var adapter : adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail)
    }
}