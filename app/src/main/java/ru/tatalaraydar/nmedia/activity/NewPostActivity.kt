package ru.tatalaraydar.nmedia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import ru.tatalaraydar.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postText = intent.getStringExtra("text")
        if (postText == null) {
            binding.edit.setText(postText)

            binding.edit.requestFocus()


            binding.ok.setOnClickListener {
                val text = binding.edit.text.toString()
                if (text.isBlank()) {
                    setResult(RESULT_CANCELED)
                } else {
                    setResult(RESULT_OK, Intent().apply { putExtra("text", text) })
                }
                finish()
            }
        }
    }


    object NewPostContract : ActivityResultContract<Unit, String?>() {
        override fun createIntent(context: Context, input: Unit) = Intent(context, NewPostActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?) = intent?.getStringExtra("text")
    }
}
