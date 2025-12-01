package com.example.disasternet.ui.sos

import androidx.lifecycle.ViewModel
import com.example.disasternet.networking.BluetoothLeManager

class SosViewModel : ViewModel() {

    fun sendSos(lat: Double, lon: Double) {
        BluetoothLeManager.broadcastSos(lat, lon)
    }
}