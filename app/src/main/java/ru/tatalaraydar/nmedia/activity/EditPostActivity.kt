package ru.tatalaraydar.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tatalaraydar.nmedia.databinding.ActivityEditPostBinding

class EditPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getLongExtra("post_id", 0L)
        val postContent = intent.getStringExtra("post_content")

        binding.editPostContent.setText(postContent)

        binding.saveButton.setOnClickListener {
            val updatedContent = binding.editPostContent.text.toString()
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("updated_content", updatedContent)
                putExtra("post_id", postId)
            })
            finish()
        }
    }
}