package com.example.disasternet.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.disasternet.data.DisasterMessage
import com.example.disasternet.networking.BluetoothLeManager

// This ViewModel is the single source of truth for all fragments
class SharedViewModel : ViewModel() {

    // It gets its data directly from the networking layer
    val messages: LiveData<MutableList<DisasterMessage>> = BluetoothLeManager.messages
    val discoveredDevices: LiveData<List<Pair<String, String>>> = BluetoothLeManager.devices

    // It provides a clean way for the UI to send messages
    fun broadcastMessage(message: String) {
        BluetoothLeManager.broadcastMessage(message)
    }
}