package ru.tatalaraydar.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.databinding.FragmentNewPostBinding
import ru.tatalaraydar.nmedia.util.AndroidUtils
import ru.tatalaraydar.nmedia.util.LongArg
import ru.tatalaraydar.nmedia.util.StringArg
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.postId: Long by LongArg
    }

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private var postId: Long = 0L

    private val pickPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    Snackbar.make(
                        binding.root,
                        ImagePicker.getError(it.data),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                Activity.RESULT_OK -> {
                    val uri: Uri? = it.data?.data
                    viewModel.changePhoto(uri, uri?.toFile())
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            postId = it.postId
            it.textArg?.let { content ->
                binding.edit.setText(content)
            }
        }

        setupMenu()
        setupPhotoHandling()
        observeViewModel()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
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
        val text = binding.edit.text.toString()
        if (text.isBlank()) return

        if (postId != 0L) {
            // Режим редактирования
            viewModel.edit(postId, text)
        } else {
            // Режим создания
            viewModel.changeContent(text)
            viewModel.save()
        }

        AndroidUtils.hideKeyboard(requireView())
        findNavController().navigateUp()
    }

    private fun setupPhotoHandling() {
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(arrayOf("image/png", "image/jpeg"))
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }
    }

    private fun observeViewModel() {
        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            if (photo.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(photo.uri)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}