package com.example.walking

//import com.android4.travel.model.TripListModel
//import com.android4.travel.model.User
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.walking.databinding.ActivityMainBinding
import com.example.walking.fragment.*
import com.example.walking.model.User


import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Callback
import retrofit2.Response

//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response

class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {


    lateinit var binding: ActivityMainBinding
    lateinit var email: String
    var nickname: String? = ""
    private var TAG: String = "MainActivity"
    var user: User? = null





    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)





        binding.profileImage.setOnClickListener { showPopup(binding.profileImage) }


        //스토리지 이미지 다운로드........................








        //SharedPreference를 이용하여 간단한 데이터들을 저장하고 불러올 수 있다.
        val pref = getSharedPreferences("inputPref", Context.MODE_PRIVATE)
        var check = pref.getInt("input", 0)

        if (check == 1) {
            pref.edit().run {
                putInt("input", 0)
                commit()
            }
        }
        val database = Firebase.database
        val myRef = database.getReference("username")

        myRef.get().addOnCompleteListener {
            email = it.result.value.toString()
            Log.d(TAG, "1=====main======파이어베이스 실시간 디비에 저장된 username : $email")

            //레트로핏 통신 객체
            val networkService = (applicationContext as MyApplication).networkService
            //디비에서 해당 유저 검색해서 가져오기
            var oneUserCall = networkService.doGetOneUser(email)
            oneUserCall.enqueue(object : Callback<User> {
                override fun onResponse(call: retrofit2.Call<User>, response: Response<User>) {
                    Log.d("test11","response : $response")
                    nickname = response.body()?.nickname
                    user = response.body()
                    var docId = response.body()?.profile_id



                    val imgRef = MyApplication.storage.reference.child("profiles/${docId}.jpg")
                    imgRef.downloadUrl.addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            Glide.with(this@MainActivity)
                                .load(task.result)
                                .into(binding.profileImage)
                        }
                    }

                    binding.nickname.text = nickname


                    Log.d(TAG, "2=====response.body().toString()========nickname: $nickname")
                }

                override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                    call.cancel()
                }

            })
        }

//        //파이어베이스 실시간 디비에 저장된 username 가져와서
//        val database = Firebase.database
//        val myRef = database.getReference("username")
//
//        myRef.get().addOnCompleteListener {
//            username = it.result.value.toString()
//            Log.d(TAG, "1=====main======파이어베이스 실시간 디비에 저장된 username : $username")
//
//            //레트로핏 통신 객체
//            val networkService = (applicationContext as MyApplication).networkService
//            //디비에서 해당 유저 검색해서 가져오기
//            var oneUserCall = networkService.doGetOneUser(username)
//            oneUserCall.enqueue(object : Callback<User> {
//                override fun onResponse(call: Call<User>, response: Response<User>) {
//                    nickname = response.body().toString()
//
//                    Log.d(TAG, "2=====response.body().toString()========nickname: $nickname")
//                }
//
//                override fun onFailure(call: Call<User>, t: Throwable) {
//                    call.cancel()
//                }
//
//            })
//        }
//플로팅 액션바 , 개인 , 팀 모임 만들기 가기
//        binding.fab.setOnClickListener {
//            val intent = Intent(this, InputActivity::class.java)
//
//            startActivity(intent)
//        }

// 탭 옵션
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //탭 선택되었을 때,
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //탭이 선택되지 않은 상태로 변경되었을 때,
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //이미 선택된 탭이 다시 선택되었을 때,
            }
        })
//viewPager - adapter 연결
        //189번 MainViewPagerAdapter
        binding.viewPager.adapter = MainViewPagerAdapter(this)
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "main"
                1 -> tab.text = "후기"
                2 -> tab.text = "모임"
                3 -> tab.text = "인생네컷"
                4 -> tab.text = "나의공간"
            }
        }.attach()

//toolbar binding
        //ActionBar 유틸리티 메서드를 사용하려면 활동의 getSupportActionBar() 메서드를 호출
        // supportActionBar 인스턴스로 옵션 사용함.
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("부산한걸음")



//        val networkService = (applicationContext as MyApplication).networkService
//        val tripListCall = networkService.doGetTripList()
//
//        tripListCall.enqueue(object : Callback<TripListModel> {
//            override fun onResponse(call: Call<TripListModel>, response: Response<TripListModel>) {
//                if (response.isSuccessful) {
////                    binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
////                    binding.recyclerView.adapter = MyAdapter(this@MainActivity, response.body()?.trips)
////                    binding.recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
//                }
//            }
//
//            override fun onFailure(call: Call<TripListModel>, t: Throwable) {
//                call.cancel()
//            }
//        })



    }


























//    // androidx.fragment.app.FragmentActivity
//    override fun onStart() {
//        super.onStart()
//
//        // onCreate 에도 있음
//        val pref = getSharedPreferences("inputPref", Context.MODE_PRIVATE)
//        var check = pref.getInt("input", 0)
//
//        Log.d(TAG, "$check==============")
//
//        if (check == 1) {
//            pref.edit().run {
//                putInt("input", 0)
//                commit()
//            }
//
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.linearTest, ListFragment())
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
//    }
    //툴바 메뉴 아이템 로그아웃 표시 속성 ifRoom : 공간있으면 표기,
    // never 3점으로 클릭시 표기.
    // menu/menu.xml 파일에
    // 로그 아웃 이후 로그인 창으로 가는 샘플 코드 잘 알아두기.
    //메뉴 클릭시 이 함수가 호출이 됨.
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_logout -> {
//                Toast.makeText(this@MainActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
////                Intent(this@MainActivity, LoginActivity::class.java).run {
////                    startActivity(this)
//                }
//            R.id.user_inf -> {
//                Log.d("walk", "회원정보수정")
//            }
//
//            }
//
////        }
//        return super.onOptionsItemSelected(item)
//    }

    // 샘플 코드 거의 똑같음.
    // 툴바 옵션 메뉴 예) 로그아웃.
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu, menu)
//        return super.onCreateOptionsMenu(menu)
//
//
//    }
    val requestLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        Log.d("ddd", "${it.data?.getStringExtra("updateprofileid")}")
        Glide
            .with(this@MainActivity)
            .load(Uri.parse(it.data?.getStringExtra("updateprofileid")))
            .apply(RequestOptions().override(250, 200))
            .centerCrop()
            .into(binding.profileImage)


        binding.nickname.text = it.data?.getStringExtra("updatenickname")
        user?.profile_id = it.data?.getStringExtra("updateprofiledocid")!!
        user?.nickname = it.data?.getStringExtra("updatenickname")!!
    }




    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
            R.id.menu_update ->
            {
                val intent = Intent(this@MainActivity, UpdateUserActivity::class.java)
                intent.putExtra("email", user?.email)
                intent.putExtra("nickname", user?.nickname)
                intent.putExtra("profile_id", user?.profile_id)
                intent.putExtra("password", user?.password)
                requestLauncher.launch(intent)
            }
            R.id.menu_logout ->
            {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_LONG).show()
            }
        }

        return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v) // PopupMenu 객체 선언
        popup.menuInflater.inflate(R.menu.profilemenu, popup.menu) // 메뉴 레이아웃 inflate
        popup.setOnMenuItemClickListener(this)
        popup.show() // 팝업 보여주기
    }

}

//viewpager adapter
class MainViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainFragment()
            1 -> BoardFragment()
            2 -> MeetingFragment()
            3 -> PhotoFragment()
            else -> PlanFragment()
        }
    }
}



