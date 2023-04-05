package com.example.walking

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.walking.AddFourCutActivity.Companion.docId
import com.example.walking.MyApplication2.Companion.networkService
import com.example.walking.RegisterActivity.Companion.imgUri
import com.example.walking.databinding.ActivityMainBinding
import com.example.walking.databinding.ActivityUpdateUserBinding
import com.example.walking.model.User
import com.example.walking.model.Userinmeeting
import com.example.walking.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UpdateUserActivity : AppCompatActivity() {

    companion object{
    var imgUri: Uri? = null}
    lateinit var filePath: String
    lateinit var binding: ActivityUpdateUserBinding
    var imgStatus = 0
    private  var TAG : String = "UpdateUserActivity"
    var beforeprofile_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //기존 정보
        var email = intent.getStringExtra("email")
        var nickname = intent.getStringExtra("nickname")
        beforeprofile_id = intent.getStringExtra("profile_id")



        binding.editUsername.setText(email)
        binding.editNickname.setText(nickname)

        val imgRef = MyApplication.storage.reference.child("profiles/${beforeprofile_id}.jpg")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Glide.with(this@UpdateUserActivity)
                    .load(task.result)
                    .into(binding.profileImage)
            }
        }




        //갤러리 사진 변경
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


//                val timeStamp: String =
//                    SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//                val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//                val file = File.createTempFile(
//                    "CHC_${timeStamp}_",
//                    ".jpg",
//                    storageDir
//                )
//                filePath = file.absolutePath
//                imgUri = FileProvider.getUriForFile(
//                    this@UpdateUserActivity,
//                    "com.example.walking.fileprovider",
//                    file
//                )





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



        //수정버튼 클릭
        binding.updateBtn.setOnClickListener {
            if (imgStatus == 0) {
                var user = User(
                    binding.editUsername.text.toString(),
                    binding.editPassword.text.toString(),
                    binding.editNickname.text.toString(),
                    "unchanged"
                )
                Log.d(TAG, "1=========================registerBtn==========$user")

                //스프링에 회원 가입 전달, mysql 저장 후 리턴.
                val networkService = (applicationContext as MyApplication).networkService
                var userUpdateCall = networkService.doUpdateUser(user)
                userUpdateCall.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        Log.d("test11", "$response")
                        if (response.isSuccessful) {
                            var user = response.body()

                            Log.d(
                                TAG,
                                "2===response.isSuccessful=====response.body()===========================$user"
                            )

                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        call.cancel()
                    }

                })
            }


    //수정시 여기서 부터 시작.
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
                        updateImage(this@UpdateUserActivity, it.id, filePath)

                        deleteStore(beforeprofile_id!!)
                        deleteImage(beforeprofile_id!!)

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
                        var userUpdateCall = networkService.doUpdateUser(user)
                        userUpdateCall.enqueue(object : Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                Log.d("test11", "$response")
                                if (response.isSuccessful) {
                                    var user = response.body()

                                    Log.d(
                                        TAG,
                                        "2===response.isSuccessful=====response.body()===========================$user"
                                    )
                                    Log.d("ddddd", imgUri.toString())
                                    intent.putExtra("updateprofileid", imgUri.toString())
                                    intent.putExtra("updatenickname", user?.nickname)
                                    intent.putExtra("updateprofiledocid", docId)
                                    setResult(RESULT_OK, intent)
                                    finish()
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


        //메인으로 돌아가기 버튼 클릭
        binding.tvBackMain.setOnClickListener {
            finish()
        }



        //회원탈퇴 버튼 클릭

        var dialog_listener = DialogInterface.OnClickListener { dialog, which ->
            val networkService = (applicationContext as MyApplication).networkService
            val userDeleteCall = networkService.doDeleteUser(binding.editUsername.text.toString())
            userDeleteCall.enqueue(object: Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    deleteStore(intent.getStringExtra("profile_id")!!)
                    deleteImage(intent.getStringExtra("profile_id")!!)
                    Toast.makeText(this@UpdateUserActivity, "회원탈퇴하셨습니다.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@UpdateUserActivity, LoginActivity::class.java)
                    startActivity(intent)


                }
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.d("dialog", "실패")
                }
            })

        }



        binding.OutBtn.setOnClickListener {
            AlertDialog.Builder(this@UpdateUserActivity).run {
                setTitle("회원탈퇴")
                setMessage("정말 회원 탈퇴 하시겠습니까?")
                setPositiveButton("아니오", null)
                setNegativeButton("네", dialog_listener)
                show()
            }
        }





    }
}