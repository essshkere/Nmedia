package ru.tatalaraydar.nmedia.activity

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.tatalaraydar.nmedia.adapter.OnInteractionListener
import ru.tatalaraydar.nmedia.adapter.PostsAdapter
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel


@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val viewModel: PostViewModel by activityViewModels()
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val interactionListener = object : OnInteractionListener {
        override fun onLike(post: Post) = viewModel.likeById(post.id)
        override fun onShare(post: Post) = viewModel.share(post)
        override fun onRemove(post: Post) = viewModel.removeById(post.id)

        override fun onViewPost(post: Post) {
            findNavController().navigate(
                R.id.action_feedFragment_to_postFragment,
                Bundle().apply { putLong("postId", post.id) }
            )
        }

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

        val adapter = PostsAdapter(interactionListener)
        binding.container.adapter = adapter

        binding.apply {
            container.adapter = adapter
            retryButton.setOnClickListener { adapter.retry() }
            save.setOnClickListener {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
            swiperefresh.setOnRefreshListener { adapter.refresh() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.posts.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { state ->
                binding.apply {
                    errorGroup.isVisible = state.refresh is LoadState.Error
                    emptyText.isVisible = adapter.itemCount == 0 &&
                            state.refresh !is LoadState.Loading
                    progress.isVisible = state.refresh is LoadState.Loading &&
                            adapter.itemCount == 0
                    swiperefresh.isRefreshing = state.refresh is LoadState.Loading
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}