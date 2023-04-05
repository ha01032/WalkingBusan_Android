package com.example.walking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.walking.databinding.ActivityRegisterBinding
import com.example.walking.model.User
import com.example.walking.util.dateToString
import com.example.walking.util.uploadImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RegisterActivity : AppCompatActivity() {
    //회원 가입시 백 서버에 디비에 저장
    companion object {
        var imgUri: Uri? = null

    }
    lateinit var binding : ActivityRegisterBinding
    private  var TAG : String = "RegisterActivity"
    lateinit var filePath: String
    var imgStatus = 0
    var defaultdocId: String = "profile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
            if(it.resultCode === android.app.Activity.RESULT_OK){
                imgUri = it.data?.data
                imgStatus = 1
                Glide
                    .with(getApplicationContext())
                    .load(it.data?.data)
                    .apply(RequestOptions().override(250, 200))
                    .centerCrop()
                    .into(binding.profileImage)


                val cursor = contentResolver.query(it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
                cursor?.moveToFirst().let {
                    filePath=cursor?.getString(0) as String
                }
            }
        }



        binding.galleryAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)
        }







        binding.registerBtn.setOnClickListener {










            if (imgStatus == 0) {
                var user = User(
                    binding.editUsername.text.toString(),
                    binding.editPassword.text.toString(),
                    binding.editNickname.text.toString(),
                    defaultdocId
                )
                Log.d(TAG, "1=========================registerBtn==========$user")
                val networkService = (applicationContext as MyApplication).networkService
                //스프링에 회원 가입 전달, mysql 저장 후 리턴.
                var userInsertCall = networkService.doInsertUser(user)
                userInsertCall.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        Log.d("test11", "$response")
                        if (response.isSuccessful) {
                            var user = response.body()

                            Log.d(
                                TAG,
                                "2===response.isSuccessful=====response.body()===========================$user"
                            )

                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)

                            startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        call.cancel()
                    }

                })
            }



            //이미지 저장
            if (imgStatus == 1) {
                Log.d("chcchc", "imgStatus ==1 성공")
                //add............................
                val data = mapOf(
                    "email" to binding.editUsername.text.toString(),
                    "date" to dateToString(Date())
                )

                MyApplication.db.collection("profiles")
                    .add(data)
                    .addOnSuccessListener {
                        Log.d("test", "성공?")
                        uploadImage(this@RegisterActivity, it.id, filePath)
                        var docId = it.id
                        Log.d("storeid", it.id)


                        var user = User(
                            binding.editUsername.text.toString(),
                            binding.editPassword.text.toString(),
                            binding.editNickname.text.toString(),
                            docId
                        )
                        Log.d(TAG, "1=========================registerBtn==========$user")
                        val networkService = (applicationContext as MyApplication).networkService
                        //스프링에 회원 가입 전달, mysql 저장 후 리턴.
                        var userInsertCall = networkService.doInsertUser(user)
                        userInsertCall.enqueue(object : Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                Log.d("test11", "$response")
                                if (response.isSuccessful) {
                                    var user = response.body()

                                    Log.d(
                                        TAG,
                                        "2===response.isSuccessful=====response.body()===========================$user"
                                    )

                                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)

                                    startActivity(intent)
                                }
                            }

                            override fun onFailure(call: Call<User>, t: Throwable) {
                                call.cancel()
                            }

                        })



                    }
                    .addOnFailureListener {
                        Log.d("test", "실패?")
                        Log.d("chc", "data save error", it)
                    }




            }


        }










        // 앱 시작시 회원가입 창에서 back to Login  버튼 클릭시 해당 로그인 뷰로 이동
        binding.tvBackLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }

    }
}