package ru.tatalaraydar.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.tatalaraydar.nmedia.databinding.CardPostBinding
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.repository.PostRepositoryImpl.Companion.formatCount
import android.util.Log

import com.bumptech.glide.Glide
import ru.tatalaraydar.nmedia.entity.AttachmentType

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onVideolink(post: Post) {}
    fun onViewPost(post: Post) {}
    fun onImageClick(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            content.text = post.content
            published.text = post.published
            author.text = post.author
            viewsPost.text = formatCount(post.views_post)
            buttonLikes.isChecked = post.likedByMe
            buttonLikes.text = formatCount(post.likes)
            buttonShare.text = formatCount(post.share)

            buttonShare.setOnClickListener { onInteractionListener.onShare(post) }
            buttonLikes.setOnClickListener { onInteractionListener.onLike(post) }
            content.setOnClickListener {
                onInteractionListener.onViewPost(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            if (!post.videoURL.isNullOrEmpty()) {
                videoLink.text = post.videoURL
                videoLink.visibility = View.VISIBLE
                videoPic.visibility = View.VISIBLE
                videoLink.text = "Смотреть новое видео на канале!"
                videoLink.setOnClickListener {                }
            } else {
                videoPic.visibility = View.GONE
                videoLink.visibility = View.GONE
            }


            post.attachment?.takeIf { it.type == AttachmentType.IMAGE }?.let {
                val fullImageUrl = post.getFullImageUrl()
                if (fullImageUrl != null) {
                    Glide.with(postImageView)
                        .load(fullImageUrl)
                        .timeout(10_000)
                        .into(postImageView)
                    postImageView.visibility = View.VISIBLE
                    postImageView.setOnClickListener {
                        onInteractionListener.onImageClick(post)
                    }
                } else {
                    postImageView.visibility = View.GONE
                }
            } ?: run {
                postImageView.visibility = View.GONE
            }

            val avatarUrl = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
            Glide.with(avatar)
                .load(avatarUrl)
                .circleCrop()
                .placeholder(R.drawable.baseline_visibility_24)
                .error(R.drawable.ic_cancel_48)
                .timeout(10_000)
                .into(avatar)
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}