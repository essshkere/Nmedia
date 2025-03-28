package ru.tatalaraydar.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.activity.NewPostFragment.Companion.textArg
import ru.tatalaraydar.nmedia.adapter.OnInteractionListener
import ru.tatalaraydar.nmedia.adapter.PostsAdapter
import ru.tatalaraydar.nmedia.databinding.FragmentFeedBinding
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {


    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)

            }

            override fun onShare(post: Post) {
                viewModel.share(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)

            }



            override fun onVideolink(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoURL))
                startActivity(intent)
            }

            override fun onViewPost(post: Post) {
                val bundle = Bundle().apply { putLong("postId", post.id) }
                findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
            }

            override fun onImageClick(post: Post) {
                val bundle = Bundle().apply {
                    putString("imageUrl", post.getFullImageUrl())
                }
                findNavController().navigate(R.id.action_feedFragment_to_fullScreenImageFragment, bundle)
            }
        })

        binding.container.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }



        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.save.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            viewModel.loadPosts()
        }

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id != 0L) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    Bundle().apply { textArg = post.content })
            }
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        viewModel.newerCount.observe(viewLifecycleOwner) { newPostCount ->
            if (newPostCount > 0) {

                showNewPostsBanner(newPostCount)
            }
        }



        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }



        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                val updatedContent =
                    data.getStringExtra("updated_content") ?: return@registerForActivityResult
                val postId = data.getLongExtra("post_id", 0L)
                viewModel.updatePost(postId, updatedContent)
            }
        }
        return binding.root

    }


    private fun showNewPostsBanner(newPostCount: Int) {
        Snackbar.make(binding.root, "Новые посты: $newPostCount", Snackbar.LENGTH_INDEFINITE)
            .setAction("Показать") {

                viewModel.makeAllPostsVisible()
                binding.container.smoothScrollToPosition(0)
            }
            .setAnchorView(binding.save)
            .show()
    }


}