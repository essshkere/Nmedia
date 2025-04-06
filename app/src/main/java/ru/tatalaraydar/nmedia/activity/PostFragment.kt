package ru.tatalaraydar.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class PostFragment : Fragment(R.layout.fragment_post) {

    private val viewModel: PostViewModel by viewModels()
    private val args: PostFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPostBinding.bind(view)

        viewModel.data.observe(viewLifecycleOwner) { state ->
            state.posts.find { it.id == args.postId }?.let { post ->
                with(binding) {
                    author.text = post.author
                    content.text = post.content
                    published.text = post.published
                    buttonLikes.text = viewModel.formatCount(post.likes)
                    buttonShare.text = viewModel.formatCount(post.share)
                    viewsPost.text = viewModel.formatCount(post.views_post)
                    buttonLikes.isChecked = post.likedByMe

                    buttonLikes.setOnClickListener { viewModel.likeById(post.id) }
                    buttonShare.setOnClickListener { viewModel.share(post.id) }

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
                                            PostFragmentDirections.actionPostFragmentToNewPostFragment(
                                                postId = post.id,
                                                textArg = post.content
                                            )
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