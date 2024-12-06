package ru.tatalaraydar.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.tatalaraydar.nmedia.databinding.FragmentEditPostBinding
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

class EditPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditPostBinding.inflate(inflater, container, false)

        arguments?.let {
            viewModel.postId = it.getLong("post_id")
            val postContent = it.getString("textArg") ?: ""
            binding.editPostContent.setText(postContent)
        }

        binding.saveButton.setOnClickListener {
            viewModel.updatedContent = binding.editPostContent.text.toString()

            viewModel.updatePost(viewModel.postId, viewModel.updatedContent ?: "")

            requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("updated_content", viewModel.updatedContent)
                putExtra("post_id", viewModel.postId)
            })
            findNavController().navigateUp()
        }

        return binding.root
    }
}