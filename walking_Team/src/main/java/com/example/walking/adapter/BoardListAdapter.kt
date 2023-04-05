package com.example.walking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.walking.MyApplication
import com.example.walking.databinding.ItemBoardBinding
import com.example.walking.fragment.BoardFragment
import com.example.walking.model.Board
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardListViewHolder (val binding : ItemBoardBinding): RecyclerView.ViewHolder(binding.root)


class BoardListAdapter(val context: BoardFragment, val datas:List<Board>?, val nickname: String, val username: String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    = BoardListViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as BoardListViewHolder).binding
        var board = datas?.get(position)
        binding.postsTitle.text = board?.board_title
        binding.Writer.text = board?.email
    }

    override fun getItemCount(): Int {
        return datas?.size ?:0
    }
}