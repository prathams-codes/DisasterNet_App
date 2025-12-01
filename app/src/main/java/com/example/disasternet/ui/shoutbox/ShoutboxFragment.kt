package com.example.disasternet.ui.shoutbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disasternet.databinding.FragmentShoutboxBinding
import com.example.disasternet.ui.SharedViewModel

class ShoutboxFragment : Fragment() {

    private var _binding: FragmentShoutboxBinding? = null
    private val binding get() = _binding!!

    // This correctly gets the SharedViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var shoutboxAdapter: ShoutboxAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoutboxBinding.inflate(inflater, container, false)

        setupRecyclerView()

        // This observes the messages from the ViewModel
        sharedViewModel.messages.observe(viewLifecycleOwner) { messages ->
            // This correctly calls submitList on our new adapter
            shoutboxAdapter.submitList(messages.toList())
            if (messages.isNotEmpty()) {
                binding.recyclerViewShoutbox.scrollToPosition(messages.size - 1)
            }
        }

        binding.buttonSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString()
            if (message.isNotBlank()) {
                sharedViewModel.broadcastMessage(message)
                binding.editTextMessage.text.clear()
            }
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        // This correctly initializes our adapter
        shoutboxAdapter = ShoutboxAdapter()
        binding.recyclerViewShoutbox.apply {
            adapter = shoutboxAdapter
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}