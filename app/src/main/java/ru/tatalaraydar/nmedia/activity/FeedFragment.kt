package ru.tatalaraydar.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.adapter.OnInteractionListener
import ru.tatalaraydar.nmedia.adapter.PostsAdapter
import ru.tatalaraydar.nmedia.databinding.FragmentFeedBinding
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val newPostLauncher = registerForActivityResult(NewPostFragment.NewPostContract) { result ->
            result ?: return@registerForActivityResult
            viewModel.сhangeContent(result)
            viewModel.save()
        }

        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onRemove(post: Post) {
                viewModel.remove(post.id)
            }

            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onEdit(post: Post) {
                val intent = Intent(this@FeedFragment.requireContext(), EditPostActivity::class.java).apply {
                    putExtra("post_id", post.id)
                    putExtra("post_content", post.content)
                }
                startActivityForResult(intent, EDIT_POST_REQUEST_CODE)
            }

            override fun onShare(post: Post) {
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
        })

        binding.container.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        binding.save.setOnClickListener {
            newPostLauncher.launch(Unit)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_POST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val updatedContent = data?.getStringExtra("updated_content") ?: return
            val postId = data.getLongExtra("post_id", 0L)

            val postToEdit = viewModel.findPostById(postId)
            if (postToEdit != null) {
                viewModel.startEditing(postToEdit)
                viewModel.сhangeContent(updatedContent)
                viewModel.save()
            }
        }
    }

    companion object {
        private const val EDIT_POST_REQUEST_CODE = 100
    }
}