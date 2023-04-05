package com.example.walking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.example.walking.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this).load(R.raw.stepjjal).into(binding.stepjjalGifImageView)

        /*    GIF 를 작동 시키기 위해서는 res 폴더에 raw 폴더를 생성 해 주어야 하고 ,
                생성해준 gif 이미지를 위한 이미지 뷰를 XML 에 추가 해줘야 한다
                추가 한 후
                Glide.with(this).load(R.raw.stepjjal).into(binding.stepjjalGifImageView) */
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3200)


    }
}
