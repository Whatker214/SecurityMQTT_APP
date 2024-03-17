package com.example.mqtt_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.example.mqtt_app.databinding.ActivitySignInBinding

class Sign_in : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tvForgetPasswd.movementMethod = LinkMovementMethod.getInstance()

        binding.tvSignUp2.setOnClickListener{
            Intent(this, Sign_up::class.java).apply {
//                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
        }
    }

}