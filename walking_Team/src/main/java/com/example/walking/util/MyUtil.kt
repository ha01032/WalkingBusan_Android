package com.example.walking.util

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walking.MyApplication
import com.example.walking.RegisterActivity.Companion.imgUri
import com.example.walking.UpdateUserActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*



fun dateToString(date: Date): String {
    val format = SimpleDateFormat("yyyy-MM-dd")
    return format.format(date)
}

//파이어 스토어 수정, 삭제 등
fun deleteStore(docId: String){
    //delete............................
    MyApplication.db.collection("profiles")
        .document(docId)
        .delete()

}

fun deleteImage(docId: String) {
    //add............................
    val storage = MyApplication.storage
    val storageRef = storage.reference
    val imgRef = storageRef.child("profiles/${docId}.jpg")
    imgRef.delete()
}


fun uploadImage(activity: AppCompatActivity,docId: String,filePath:String){
    //add............................
    val storage = MyApplication.storage
    val storageRef = storage.reference
    //새로 생성된 파일명.
    val imgRef = storageRef.child("profiles/${docId}.jpg")
    Log.d("chc1", "profiles/${docId}.jpg")
    Log.d("chc2", filePath)

    val file = Uri.fromFile(File(filePath))
    Log.d("chc3","file $file")
    imgUri?.let {
        Log.d("chc4", "저장됐나")
        imgRef.putFile(it)
            .addOnSuccessListener {
                Toast.makeText(activity, "save ok..", Toast.LENGTH_SHORT).show()
                activity.finish()
            }
            .addOnFailureListener{
                Log.d("chc5", "저장실패???")
                Log.d("lsy", "file save error", it)
            }
    }

}

fun updateImage(activity: AppCompatActivity,docId: String,filePath:String){
    //add............................
    val storage = MyApplication.storage
    val storageRef = storage.reference
    //새로 생성된 파일명.
    val imgRef = storageRef.child("profiles/${docId}.jpg")
    Log.d("chc1", "profiles/${docId}.jpg")
    Log.d("chc2", filePath)

    val file = Uri.fromFile(File(filePath))
    Log.d("chc3","file $file")
    UpdateUserActivity.imgUri?.let {
        Log.d("chc4", "저장됐나")
        imgRef.putFile(it)
            .addOnSuccessListener {
                Toast.makeText(activity, "save ok..", Toast.LENGTH_SHORT).show()
                activity.finish()
            }
            .addOnFailureListener{
                Log.d("chc5", "저장실패???")
                Log.d("lsy", "file save error", it)
            }
    }

}





fun updateStore(docId: String){
    //delete............................
    MyApplication.db.collection("profiles")
        .document(docId)
        .delete()

}