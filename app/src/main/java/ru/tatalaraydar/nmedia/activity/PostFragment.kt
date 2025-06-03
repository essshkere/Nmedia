package ru.tatalaraydar.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class PostFragment : Fragment(R.layout.fragment_post) {
    private val viewModel: PostViewModel by viewModels()
    private val postId by lazy {
        arguments?.getLong("postId") ?: throw IllegalStateException("postId argument is required")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPostBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collect { pagingData ->
                    pagingData.map { post ->
                        if (post.id == postId && post is Post) {
                            with(binding) {
                                author.text = post.author
                                content.text = post.content
                                published.text = post.published.toString()
                                buttonLikes.text = post.likes.toString()
                                buttonShare.text = post.share.toString()
                                viewsPost.text = post.views_post.toString()
                                buttonLikes.isChecked = post.likedByMe

                                buttonLikes.setOnClickListener { viewModel.likeById(post.id) }
                                buttonShare.setOnClickListener { viewModel.share(post) }

                                menu.setOnClickListener {
                                    PopupMenu(requireContext(), it).apply {
                                        inflate(R.menu.options_post)
                                        setOnMenuItemClickListener { item ->
                                            when (item.itemId) {
                                                R.id.remove -> {
                                                    viewModel.removeById(post.id)
                                                    findNavController().navigateUp()
                                                    true
                                                }
                                                R.id.edit -> {
                                                    findNavController().navigate(
                                                        R.id.action_postFragment_to_newPostFragment,
                                                        Bundle().apply {
                                                            putString("content", post.content)
                                                        }
                                                    )
                                                    true
                                                }
                                                else -> false
                                            }
                                        }
                                    }.show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}