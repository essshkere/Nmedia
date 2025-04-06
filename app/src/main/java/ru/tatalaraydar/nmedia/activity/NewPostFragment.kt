package ru.tatalaraydar.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding

import ru.tatalaraydar.nmedia.util.AndroidUtils
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel
import java.io.File

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels()
    private var _binding: FragmentNewPostBinding? = null

    private val pickPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    Snackbar.make(
                        requireView(),
                        ImagePicker.getError(it.data),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                Activity.RESULT_OK -> {
                    val uri: Uri? = it.data?.data
                    viewModel.changePhoto(uri, uri?.let { File(it.path!!) })
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.textArg?.let {
            _binding?.edit?.setText(it)
        }

        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            if (photo.uri == null) {
                _binding?.photoContainer?.visibility = View.GONE
                return@observe
            }

            with(_binding!!) {
                photoContainer.visibility = View.VISIBLE
                photo.setImageURI(photo.uri)
            }
        }

        setupMenu()
        setupPhotoHandlers()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.save -> {
                        savePost()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun savePost() {
        _binding?.edit?.text?.toString()?.let { text ->
            if (text.isBlank()) return@let

            viewModel.changeContent(text)
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
    }

    private fun setupPhotoHandlers() {
        _binding?.apply {
            pickPhoto.setOnClickListener {
                ImagePicker.with(this@NewPostFragment)
                    .crop()
                    .compress(2048)
                    .galleryMimeTypes(arrayOf("image/png", "image/jpeg"))
                    .createIntent(pickPhotoLauncher::launch)
            }

            takePhoto.setOnClickListener {
                ImagePicker.with(this@NewPostFragment)
                    .crop()
                    .compress(2048)
                    .createIntent(pickPhotoLauncher::launch)
            }

            removePhoto.setOnClickListener {
                viewModel.changePhoto(null, null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}