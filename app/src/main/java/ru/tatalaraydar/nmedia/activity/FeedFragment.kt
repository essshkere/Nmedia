package ru.tatalaraydar.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val interactionListener = object : OnInteractionListener {
        override fun onLike(post: Post) { viewModel.likeById(post.id) }
        override fun onShare(post: Post) = viewModel.share(post)
        override fun onRemove(post: Post) { viewModel.removeById(post.id) }

        override fun onViewPost(post: Post) {
            val bundle = Bundle().apply { putLong("postId", post.id) }
            findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
        }

        override fun onImageClick(post: Post) {
            post.attachment?.url?.let { url ->
                val bundle = Bundle().apply { putString("imageUrl", url) }
                findNavController().navigate(
                    R.id.action_feedFragment_to_fullScreenImageFragment,
                    bundle
                )
            }
        }

        override fun onVideolink(post: Post) {
            // todo
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostsAdapter(interactionListener)
        _binding?.container?.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { loadState ->
                    binding.swiperefresh.isRefreshing = loadState.refresh is LoadState.Loading
                    binding.progress.isVisible = loadState.refresh is LoadState.Loading
                    binding.errorGroup.isVisible = loadState.refresh is LoadState.Error
                    binding.emptyText.isVisible = adapter.itemCount == 0
                }
            }
        }

        _binding?.apply {
            retryButton.setOnClickListener { viewModel.loadPosts() }
            save.setOnClickListener {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
            swiperefresh.setOnRefreshListener {
                viewModel.refreshPosts()
                swiperefresh.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}