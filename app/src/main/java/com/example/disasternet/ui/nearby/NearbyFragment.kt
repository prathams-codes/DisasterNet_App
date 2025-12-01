package com.example.disasternet.ui.nearby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disasternet.databinding.FragmentNearbyBinding
import com.example.disasternet.ui.SharedViewModel

class NearbyFragment : Fragment() {

    private var _binding: FragmentNearbyBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearbyBinding.inflate(inflater, container, false)
        binding.recyclerViewNearby.layoutManager = LinearLayoutManager(context)

        sharedViewModel.discoveredDevices.observe(viewLifecycleOwner) { devices ->
            binding.recyclerViewNearby.adapter = NearbyAdapter(devices)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}