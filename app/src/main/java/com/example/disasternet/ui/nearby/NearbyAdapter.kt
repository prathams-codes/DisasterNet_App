package com.example.disasternet.ui.nearby

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.disasternet.PrivateChatActivity
import com.example.disasternet.R

class NearbyAdapter(private val devices: List<Pair<String, String>>) :
    RecyclerView.Adapter<NearbyAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.text_device_name)
        val deviceAddress: TextView = itemView.findViewById(R.id.text_device_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nearby_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.deviceAddress.text = device.first
        holder.deviceName.text = device.second

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PrivateChatActivity::class.java).apply {
                putExtra("DEVICE_NAME", device.second)
                putExtra("DEVICE_ADDRESS", device.first)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = devices.size
}