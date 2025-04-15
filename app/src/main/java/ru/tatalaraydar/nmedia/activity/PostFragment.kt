package ru.tatalaraydar.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.tatalaraydar.nmedia.repository.PostRepositoryImpl.Companion.formatCount
import ru.tatalaraydar.nmedia.util.StringArg
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class PostFragment : Fragment(R.layout.fragment_post) {
    var Bundle.textArg: String? by StringArg

    private val viewModel: PostViewModel by viewModels()
    private val postId by lazy {
        arguments?.getLong("postId") ?: throw IllegalStateException("postId argument is required")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPostBinding.bind(view)

        viewModel.data.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach { state ->
            state.posts.find { it.id == postId }?.let { post ->
                with(binding) {
                    author.text = post.author
                    content.text = post.content
                    published.text = post.published
                    buttonLikes.text = formatCount(post.likes)
                    buttonShare.text = formatCount(post.share)
                    viewsPost.text = formatCount(post.views_post)
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
                                                textArg = post.content
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