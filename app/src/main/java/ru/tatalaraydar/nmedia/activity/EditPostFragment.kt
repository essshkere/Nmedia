package ru.tatalaraydar.nmedia.activity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tatalaraydar.nmedia.databinding.FragmentEditPostBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel


class EditPostFragment : Fragment() {
    private var postId: Long = 0L
    val viewModel: PostViewModel by viewModels( ownerProducer = ::requireParentFragment)

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
            viewModel.changeContent(binding.editPostContent.text.toString())
            viewModel.save()
            findNavController().navigateUp()

        }

        return binding.root
    }
}