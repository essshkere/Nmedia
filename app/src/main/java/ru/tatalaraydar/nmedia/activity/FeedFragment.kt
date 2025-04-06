package ru.tatalaraydar.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.tatalaraydar.nmedia.adapter.PostsAdapter

import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    @Inject lateinit var postsAdapter: PostsAdapter

    private val viewModel: PostViewModel by viewModels()
    private var _binding: FragmentFeedBinding? = null

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

        initAdapter()
        observeViewModel()
        setupListeners()
    }

    private fun initAdapter() {
        _binding?.container?.adapter = postsAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.data.collect { state ->
                postsAdapter.submitList(state.posts)
                _binding?.apply {
                    progress.isVisible = state.loading
                    errorGroup.isVisible = state.error
                    emptyText.isVisible = state.empty
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.newerCount.collect { count ->
                if (count > 0) showNewPostsBanner(count)
            }
        }
    }

    private fun setupListeners() {
        _binding?.apply {
            retryButton.setOnClickListener { viewModel.loadPosts() }
            save.setOnClickListener {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
            swiperefresh.setOnRefreshListener {
                viewModel.refreshPosts()
            }
        }
    }

    private fun showNewPostsBanner(count: Int) {
        _binding?.let { binding ->
            Snackbar.make(binding.root, "Новые посты: $count", Snackbar.LENGTH_INDEFINITE)
                .setAction("Показать") {
                    viewModel.makeAllPostsVisible()
                    binding.container.smoothScrollToPosition(0)
                }
                .setAnchorView(binding.save)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val interactionListener = object : OnInteractionListener {
        override fun onLike(post: Post) = viewModel.likeById(post.id)
        override fun onShare(post: Post) = viewModel.share(post.id)
        override fun onRemove(post: Post) = viewModel.removeById(post.id)
        override fun onEdit(post: Post) = viewModel.edit(post.id, post.content)
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

    init {
        postsAdapter.onInteractionListener = interactionListener
    }
}