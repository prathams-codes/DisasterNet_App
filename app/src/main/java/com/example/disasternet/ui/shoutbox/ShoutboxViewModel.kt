package com.example.disasternet.ui.shoutbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.disasternet.data.DisasterMessage
import com.example.disasternet.networking.BluetoothLeManager

class ShoutboxViewModel : ViewModel() {

    val messages: LiveData<MutableList<DisasterMessage>> = BluetoothLeManager.messages

    fun sendMessage(message: String) {
        if (message.isNotBlank()) {
            BluetoothLeManager.broadcastMessage(message)
        }
    }
}