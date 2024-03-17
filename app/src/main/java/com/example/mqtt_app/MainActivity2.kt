package com.example.mqtt_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mqtt_app.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding2: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding2 = ActivityMain2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding2.root)

        binding2.bottomNavigation.setOnItemSelectedListener{item ->
            when(item.itemId){
                R.id.na_home -> {
                    binding2.imageView.setImageDrawable(resources.getDrawable(R.drawable.baseline_android_24))
                }
                R.id.na_add ->{
                    binding2.imageView.setImageDrawable(resources.getDrawable(R.drawable.baseline_wifi_24))
                }
                R.id.na_settings ->{
                    binding2.imageView.setImageDrawable(resources.getDrawable(R.drawable.baseline_account_circle_24))
                }
            }
            true
        }
    }
}