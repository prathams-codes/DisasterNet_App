package com.example.disasternet.ui.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.disasternet.networking.BluetoothLeManager

class NearbyViewModel : ViewModel() {
    // **THE FIX IS HERE: Correctly referencing the property from the new BluetoothLeManager**
    val discoveredDevices: LiveData<List<Pair<String, String>>> = BluetoothLeManager.devices
}