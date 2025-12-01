package com.example.disasternet

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.disasternet.databinding.ActivityMainBinding
import com.example.disasternet.networking.BluetoothLeManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                startBleServices()
            } else {
                Toast.makeText(this, "All permissions are required for this app.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                checkAndRequestPermissions()
            } else {
                Toast.makeText(this, "Bluetooth must be enabled to use this app.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_shoutbox, R.id.navigation_nearby, R.id.navigation_sos))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        checkBluetoothAndPermissions()
    }

    private fun checkBluetoothAndPermissions() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "This device does not support Bluetooth", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            checkAndRequestPermissions()
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startBleServices()
        }
    }

    private fun startBleServices() {
        Toast.makeText(this, "Starting services...", Toast.LENGTH_SHORT).show()
        bluetoothAdapter?.let {
            BluetoothLeManager.initialize(it)
            // **THE FIX IS HERE:** The startBroadcasting() line is removed.
            // The manager now handles its own state. We only need to start scanning.
            BluetoothLeManager.startScanning()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BluetoothLeManager.stop()
    }
}