package com.example.disasternet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.disasternet.databinding.ActivityPrivateChatBinding

class PrivateChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivateChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val deviceName = intent.getStringExtra("DEVICE_NAME") ?: "Unknown Device"
        supportActionBar?.title = "Chat with $deviceName"

        binding.textPrivateChatHeader.text = "This will be a private chat with $deviceName.\n\nFile sharing will be implemented here."
    }
}