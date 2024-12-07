package ru.tatalaraydar.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.activity.NewPostFragment.Companion.textArg
import ru.tatalaraydar.nmedia.adapter.OnInteractionListener
import ru.tatalaraydar.nmedia.adapter.PostsAdapter
import ru.tatalaraydar.nmedia.databinding.FragmentFeedBinding
import ru.tatalaraydar.nmedia.databinding.FragmentPostBinding
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {
    private var postId: Long = 0L

    val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(inflater, container, false)

        arguments?.let {
            viewModel.postId = it.getLong("post_id")
        }

        viewModel.findPostById(postId).observe(viewLifecycleOwner) { post ->
            binding.author.text = post?.author ?: "Автор не указан"
            binding.content.text = post?.content ?: "Содержимое отсутствует"
            binding.published.text = post?.published ?: "Дата публикации не указана"
            binding.viewsPost.text = post?.views_post.toString() ?: "0 просмотров"
        }

            val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onRemove(post: Post) {
                viewModel.remove(post.id)
            }

            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.startEditing(post)
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



        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id != 0L) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    Bundle().apply { textArg = post.content })
            }
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
}