package ru.tatalaraydar.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.tatalaraydar.nmedia.activity.NewPostFragment.Companion.textArg

import ru.tatalaraydar.nmedia.databinding.FragmentNewPostBinding
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

class EditPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewPostBinding.inflate(inflater,container,false)
        val viewModel: PostViewModel by viewModels( ownerProducer = ::requireParentFragment)
        arguments?.textArg?.let(binding.edit::setText)
        val intent = Intent()
//        setContentView(binding.root)

        val postId = intent.getLongExtra("post_id", 0L)
        val postContent = intent.getStringExtra("post_content")

        binding.edit.setText(postContent)

        binding.ok.setOnClickListener {
            val updatedContent = binding.edit.text.toString()
            requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("updated_content", updatedContent)
                putExtra("post_id", postId)
            })
            findNavController().navigateUp()
        }
        return binding.root
    }
}