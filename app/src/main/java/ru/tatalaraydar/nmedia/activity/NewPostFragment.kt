package ru.tatalaraydar.nmedia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.databinding.FragmentNewPostBinding
import ru.tatalaraydar.nmedia.util.AndroidUtils
import ru.tatalaraydar.nmedia.util.StringArg
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    private var fragmentBinding: FragmentNewPostBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        arguments?.textArg?.let(binding.edit::setText)

        val intent = Intent()
        fragmentBinding = binding

        val postText = intent.getStringExtra("text")
        if (postText == null) {

            binding.edit.setText(postText)

//            binding.ok.setOnClickListener {
//                val text = binding.edit.text.toString()
//                if (text.isNotBlank()) {
//                    viewModel.changeContent(text)
//                    viewModel.save()
//                }
//            }
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        fragmentBinding?.let {
                            viewModel.changeContent(it.edit.text.toString())
                            viewModel.save()
                            AndroidUtils.hideKeyboard(requireView())
                        }
                        true
                    }
                    else -> false
                }

        }, viewLifecycleOwner)


        return binding.root
    }





    companion object {
        var Bundle.textArg: String? by StringArg
    }


    object NewPostContract : ActivityResultContract<Unit, String?>() {
        override fun createIntent(context: Context, input: Unit) =
            Intent(context, NewPostFragment::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?) = intent?.getStringExtra("text")
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}
