package ru.tatalaraydar.nmedia.activity

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.tatalaraydar.nmedia.adapter.FeedAdapter
import ru.tatalaraydar.nmedia.adapter.OnInteractionListener
import ru.tatalaraydar.nmedia.adapter.PagingLoadStateAdapter
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel


@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val viewModel: PostViewModel by viewModels()
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val interactionListener = object : OnInteractionListener {
        override fun onLike(post: Post) {viewModel.likeById(post.id)}
        override fun onShare(post: Post) = viewModel.share(post)
        override fun onRemove(post: Post) {viewModel.removeById(post.id)}
        override fun onViewPost(post: Post) {            findNavController().navigate(
                R.id.action_feedFragment_to_postFragment,
                Bundle().apply { putLong("postId", post.id) })        }
        override fun onImageClick(post: Post) {
            post.attachment?.url?.let { url ->
                findNavController().navigate(
                    R.id.action_feedFragment_to_fullScreenImageFragment,
                    Bundle().apply { putString("imageUrl", url) }
                )
            }
        }

        override fun onVideolink(post: Post) {
            // TODO
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFeedBinding.bind(view)
        setupRecyclerView()
        observePosts()
        setupSwipeRefresh()
        setupRetryButton()
        setupFloatingActionButton()
    }

    val adapter = FeedAdapter(interactionListener)

    private fun setupRecyclerView() {
        binding.container.layoutManager = LinearLayoutManager(requireContext())

        binding.container.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { adapter.retry() },
            footer = PagingLoadStateAdapter { adapter.retry() }
        )

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.apply {
                    swiperefresh.isRefreshing = loadStates.refresh is LoadState.Loading
                    errorGroup.isVisible = loadStates.refresh is LoadState.Error
                    emptyText.isVisible = adapter.itemCount == 0 &&
                            loadStates.refresh !is LoadState.Loading
                    progress.isVisible = loadStates.refresh is LoadState.Loading &&
                            adapter.itemCount == 0
                }
            }
        }
    }

    private fun observePosts() {
        lifecycleScope.launch {
            viewModel.posts.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
            adapter.retry()
        }
    }

    private fun setupFloatingActionButton() {
        binding.save.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
