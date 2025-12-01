package com.example.disasternet.ui.sos

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.disasternet.R
import com.example.disasternet.databinding.FragmentSosBinding
import com.example.disasternet.networking.BluetoothLeManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class SosFragment : Fragment() {

    private var _binding: FragmentSosBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // This is the action that will be performed after the 3-second hold
    private val sosRunnable = Runnable { sendSosSignal() }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSosBinding.inflate(inflater, container, false)

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Set up the press-and-hold listener for the SOS button
        binding.buttonSos.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // User's finger is on the button
                    // Provide visual feedback by changing the color to a darker red
                    binding.buttonSos.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.emergency_red_dark))
                    // Start the 3-second timer
                    handler.postDelayed(sosRunnable, 3000)
                    true // We've handled this event
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // User's finger has lifted, or the touch was cancelled
                    // Reset the button color to the original red
                    binding.buttonSos.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.emergency_red))
                    // CRITICAL: Cancel the timer to prevent the SOS from being sent
                    handler.removeCallbacks(sosRunnable)
                    true // We've handled this event
                }
                else -> false // Let the system handle other events
            }
        }
        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun sendSosSignal() {
        // This function is called ONLY if the button is held for the full 3 seconds
        binding.textGpsStatus.text = getString(R.string.gps_status_getting_location)

        // Request the current, most accurate location
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude

                    // Format the coordinates to 4 decimal places for display
                    val formattedLat = String.format(Locale.US, "%.4f", lat)
                    val formattedLon = String.format(Locale.US, "%.4f", lon)

                    // Display the coordinates on your screen for confirmation
                    binding.textGpsStatus.text = getString(R.string.gps_status_sent, formattedLat, formattedLon)

                    // Call the networking manager to broadcast the SOS with the precise coordinates
                    BluetoothLeManager.broadcastSos(lat, lon)
                    Toast.makeText(context, getString(R.string.toast_sos_sent), Toast.LENGTH_SHORT).show()
                } else {
                    // Handle the case where location could not be determined
                    binding.textGpsStatus.text = getString(R.string.gps_status_failed)
                    Toast.makeText(context, getString(R.string.toast_sos_not_sent), Toast.LENGTH_SHORT).show()
                }
                // Reset the button color after the action is complete
                binding.buttonSos.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.emergency_red))
            }
            .addOnFailureListener {
                // Handle errors during location fetching
                binding.textGpsStatus.text = getString(R.string.gps_status_error)
                Toast.makeText(context, getString(R.string.toast_gps_error, it.message), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Prevent memory leaks
        _binding = null
    }
}