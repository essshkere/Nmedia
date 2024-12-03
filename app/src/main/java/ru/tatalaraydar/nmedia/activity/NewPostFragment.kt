package ru.tatalaraydar.nmedia.activity

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import ru.tatalaraydar.nmedia.databinding.FragmentNewPostBinding

class NewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        val intent = Intent()
        val postText = intent.getStringExtra("text")
        if (postText == null) {

            binding.edit.setText(postText)

            binding.edit.requestFocus()

            binding.ok.setOnClickListener {
                val text = binding.edit.text.toString()
                if (text.isBlank()) {
                    activity?.setResult(RESULT_CANCELED)
                } else {
                    activity?.setResult(RESULT_OK, Intent().apply { putExtra("text", text) })
                }
                activity?.finish()
            }
        }
        return binding.root
    }


    object NewPostContract : ActivityResultContract<Unit, String?>() {
        override fun createIntent(context: Context, input: Unit) = Intent(context, NewPostFragment::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?) = intent?.getStringExtra("text")
    }
}
