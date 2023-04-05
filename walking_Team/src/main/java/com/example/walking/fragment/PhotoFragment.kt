package com.example.walking.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.walking.AddFourCutActivity
import com.example.walking.MyApplication
import com.example.walking.databinding.FragmentPhotoBinding
import com.example.walking.model.FireStoreImg
import com.example.walking.recycler.ListViewAdapter
import com.google.firebase.firestore.Query

class PhotoFragment : Fragment() {
    lateinit var binding: FragmentPhotoBinding
    var MainUriList: MutableList<Uri?> = ArrayList()
    var recyclerView: RecyclerView? = null
    var adapter: ListViewAdapter? = null
    val db = MyApplication.db
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPhotoBinding.inflate(inflater,container, false)
        binding.fab.setOnClickListener {
            Log.d("test","실행")
            addFourCut()
        }
//        MainUriList.add(AddFourCutActivity.uriList[0])


//        db.collection("images")
//            .get()
//            .addOnSuccessListener { result ->
//            MainUriList.clear()
//            for (document in result) {
//                val item = document["uri"] as Uri
//                MainUriList.add(item)
//            }
//            adapter!!.notifyDataSetChanged()
//        }
//            .addOnFailureListener { exception ->
//                Log.d("park","error 발생 $exception")
//            }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        val db = MyApplication.db
//        db.collection("Fourcut")
//            .get()
//            .addOnSuccessListener {result ->
//
//            }

//        adapter = ListViewAdapter()

    }

    override fun onStart() {
        super.onStart()
        MyApplication.db.collection("testFourCut").orderBy("img1", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                val itemList: MutableList<FireStoreImg> = mutableListOf()
                itemList.clear()
                for (item in querySnapshot!!.documents) {
                    val item2 = item.toObject(FireStoreImg::class.java)
                    item2?.docId = item.id
                    item2?.img1 = item.get("img1") as String
                    Log.d("park","document.id ${item.id}")
                    Log.d("park","document.get : ${item.get("img1") as String}")
                    itemList.add(item2!!)
                }

                adapter = context?.let {ListViewAdapter(itemList,this@PhotoFragment)}
                binding.gridfragmentRecyclerview.layoutManager = GridLayoutManager(activity,3)
                binding.gridfragmentRecyclerview.adapter = adapter
                binding.gridfragmentRecyclerview
                adapter!!.notifyDataSetChanged()
            }
    }
    private fun addFourCut() {
        val intent = Intent(context,AddFourCutActivity::class.java)
        startActivity(intent)
    }
}

