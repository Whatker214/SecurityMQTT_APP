package com.example.mqtt_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mqtt_app.databinding.ActivitySignUpBinding

class Sign_up : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tvSignIn2.setOnClickListener{
            Intent(this, Sign_in::class.java).apply {
                startActivity(this)
            }
        }
    }
}