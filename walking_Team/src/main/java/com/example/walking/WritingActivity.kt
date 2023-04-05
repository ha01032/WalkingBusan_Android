package com.example.walking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walking.databinding.ActivityWritingBinding
import com.example.walking.databinding.FragmentBoardBinding
import com.example.walking.fragment.BoardFragment
import com.example.walking.model.Board
import com.example.walking.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.security.auth.callback.Callback


class WritingActivity : AppCompatActivity() {

    var boardTitle:String =""
    var boardContent:String =""


    lateinit var username: String
    lateinit var nickname: String
    lateinit var binding: ActivityWritingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 바인딩을 하려면 binding = 사용할 뷰 바인딩 .inflate(layoutInflater)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        //  root는 binding 요소들의 최상위 객체이다. binding 을 사용시 binding
        setContentView(binding.root);
        //-- 여기까지가 view binding
        // 입력한 글 제목 / 글내용 을 받아오는 로직 필요
        // ㄴ> 글 제목과 글 내용을 담을 그릇이 필요.
        // ㄴ> 그릇 = 변수 이다 글 제목,내용을 할당할 변수 선언이 필요하다
        // 받아온 데이터를(글제목,내용) 목록에 뿌려준다(작성페이지에서 목록페이지로 넘어간다.)-[intent] 목록은 fragment 이다.
        // ㄴ>
        // ㄴ>데이터를 담아서 할당할 변수 선언 필요.
        // intent 는 모든 작업이 다끝난 후 다른 화면으로 넘어갈때 사용한다.

        // 변수는 독립적으로 존재할 수 없다.
        // 어떤 변수인지 알려주는 문구가 필요함.
        // title 이라는 변수를 만들려면
        // string 으로 변수 의 형태를 지정해주고
        // = binding 으로 요소의 id값을 가져온다
        // id 값 이후에 text 메서드를 이용해서 타이틀에 적힌 값을 가지고온다
        // toString() 형태를 string 으로 바꿔준다. stirng 으로 시작을 했기때문에 string 으로 끝내야한다.
//        var title: String = binding.titleEt.text.toString()
//        var contents: String = binding.contentText.text.toString()

        // 스프링 이용시 통신객체(call),맵핑주소, 모델이 필요하다.

        // 새로운 생명주기  버튼을 누르면 글 목록으로 넘어감
        // Fragment 에서는 context( 화면안에 들어있는 내용 )가 들어와야함.
        // Activity 에서는 this 로 Fragment 에서의 context 역할을 한다,
//        binding.btnSave.setOnClickListener {
//            val intent = Intent(this, BoardFragment::class.java)
//            startActivity(intent)
//        }

        val database = Firebase.database
        val myRef = database.getReference( "username")
        nickname =""
        myRef.get().addOnCompleteListener{
            username = it.result.value.toString()

            val networkService = (applicationContext as MyApplication).networkService
            var oneUserCall = networkService.doGetOneUser(username)
            oneUserCall.enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    nickname = response.body()?.nickname.toString()
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }

            })
            binding.btnSave.setOnClickListener{
                // 제목 / 내용을 텍스트로 입력한 정보를 가져온다.
                boardTitle = binding.titleEt.text.toString()
                boardContent = binding.contentText.text.toString()

                var board = Board(0, boardTitle, boardContent,username)
                // 네트워크 서비스를 들고온다 .
                val networkService = (applicationContext as MyApplication).networkService
                // 위의 보드타이틀 / 보트 컨텐츠를 보드라는 모델 객체에 넣어줌
                val boardInsetCall = networkService.doInsertBoard(board)
                boardInsetCall.enqueue(object: retrofit2.Callback<Board>{
                    override fun onResponse(call: Call<Board>, response: Response<Board>){
                        if(response.isSuccessful) {
                            Log.d("song", "성공하였습니다")
                        }
                    }
                    override fun onFailure(call: Call<Board>, t: Throwable) {
                        call.cancel()
                    }

                })
                finish()

            }

        }

    }
}