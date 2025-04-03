package ru.tatalaraydar.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.adapter.OnInteractionListener
import ru.tatalaraydar.nmedia.adapter.PostsAdapter
import ru.tatalaraydar.nmedia.databinding.FragmentPostBinding
import ru.tatalaraydar.nmedia.dto.Post

import ru.tatalaraydar.nmedia.viewmodel.PostViewModel
import ru.tatalaraydar.nmedia.repository.PostRepositoryImpl.Companion.formatCount


class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private var postId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getLong("postId") ?: 0L

        viewModel.data.observe(viewLifecycleOwner) { state ->
            state.posts.find { it.id == postId }?.let { post ->
                bindPost(post)
            }
        }
    }

    private fun bindPost(post: Post) {
        binding.apply {
            author.text = post.author ?: "Автор не указан"
            content.text = post.content ?: "Содержимое отсутствует"
            published.text = post.published ?: "Дата публикации не указана"
            viewsPost.text = post.views_post?.let { formatCount(it) } ?: "0 просмотров"
            buttonLikes.text = post.likes?.let { formatCount(it) } ?: "0 лайков"
            buttonShare.text = post.share?.let { formatCount(it) } ?: "0 поделились"
            buttonLikes.isChecked = post.likedByMe == true

            buttonLikes.setOnClickListener { viewModel.likeById(post.id) }
            buttonShare.setOnClickListener { sharePost(post) }

            menu.setOnClickListener { showPopupMenu(it, post) }
        }
    }

    private fun showPopupMenu(view: View, post: Post) {
        PopupMenu(view.context, view).apply {
            inflate(R.menu.options_post)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove -> {
                        viewModel.removeById(post.id)
                        findNavController().navigateUp()
                        true
                    }
                    R.id.edit -> {
                        navigateToEditPost(post)
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }

    private fun navigateToEditPost(post: Post) {
        findNavController().navigate(
            R.id.action_postFragment_to_newPostFragment,
            Bundle().apply {
                putLong("post_id", post.id)
                putString("textArg", post.content)
            }
        )
    }

    private fun sharePost(post: Post) {
        viewModel.share(post.id)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, getString(R.string.chooser_share_post)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}