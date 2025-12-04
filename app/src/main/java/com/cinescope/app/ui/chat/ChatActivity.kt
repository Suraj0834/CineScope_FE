package com.cinescope.app.ui.chat

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cinescope.app.data.model.MovieChatMetadata
import com.cinescope.app.databinding.ActivityChatBinding
import com.cinescope.app.util.Constants
import com.cinescope.app.util.gone
import com.cinescope.app.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupInputField()
        observeViewModel()
        
        // Get movie metadata from intent
        val movieMetadata = createMovieMetadataFromIntent()
        if (movieMetadata != null) {
            viewModel.setMovieMetadata(movieMetadata)
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
        }
    }
    
    private fun setupInputField() {
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
        
        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }
    
    private fun sendMessage() {
        val message = binding.etMessage.text?.toString() ?: ""
        if (message.isNotBlank()) {
            viewModel.sendMessage(message)
            binding.etMessage.text?.clear()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                chatAdapter.submitList(messages) {
                    if (messages.isNotEmpty()) {
                        binding.rvChat.smoothScrollToPosition(messages.size - 1)
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.btnSend.isEnabled = !isLoading
                binding.etMessage.isEnabled = !isLoading
                if (isLoading) binding.progressBar.visible() else binding.progressBar.gone()
            }
        }
        
        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
    }
    
    private fun createMovieMetadataFromIntent(): MovieChatMetadata? {
        val title = intent.getStringExtra(Constants.EXTRA_MOVIE_TITLE) ?: return null
        
        // We only have title and ID now, which is enough for the AI context usually
        return MovieChatMetadata(
            title = title,
            year = "", // Could fetch if needed
            rated = "",
            runtime = "",
            genre = "",
            director = "",
            writer = "",
            actors = "",
            plot = "",
            language = "",
            country = "",
            awards = "",
            imdbRating = "",
            imdbVotes = "",
            boxOffice = ""
        )
    }
}
