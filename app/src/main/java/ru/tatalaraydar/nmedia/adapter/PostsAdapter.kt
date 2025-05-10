package ru.tatalaraydar.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.databinding.CardPostBinding
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.entity.AttachmentType
import ru.tatalaraydar.nmedia.repository.PostRepositoryImpl.Companion.formatCount

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onVideolink(post: Post) {}
    fun onViewPost(post: Post) {}
    fun onImageClick(post: Post) {}
}

class PostsAdapter (
    val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)?: return
        holder.bind(post)
    }

    companion object {
        val PostDiffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            menu.isVisible = post.ownedByMe
            author.text = post.author
            published.text = post.published
            content.text = post.content
            buttonLikes.text = formatCount(post.likes)
            buttonShare.text = formatCount(post.share)
            viewsPost.text = formatCount(post.views_post)
            buttonLikes.isChecked = post.likedByMe


            buttonShare.setOnClickListener { onInteractionListener.onShare(post) }
            buttonLikes.setOnClickListener { onInteractionListener.onLike(post) }
            content.setOnClickListener { onInteractionListener.onViewPost(post) }


            post.attachment?.takeIf { it.type == AttachmentType.IMAGE }?.let {
                val url = "http://10.0.2.2:9999/media/${it.url}"
                Glide.with(postImageView)
                    .load(url)
                    .timeout(10_000)
                    .into(postImageView)
                postImageView.visibility = View.VISIBLE
                postImageView.setOnClickListener {
                    onInteractionListener.onImageClick(post)
                }
            } ?: run {
                postImageView.visibility = View.GONE
            }


            if (!post.videoURL.isNullOrEmpty()) {
                videoLink.visibility = View.VISIBLE
                videoPic.visibility = View.VISIBLE
                videoLink.text = "Смотреть видео"
                videoLink.setOnClickListener {
                    onInteractionListener.onVideolink(post)
                }
            } else {
                videoLink.visibility = View.GONE
                videoPic.visibility = View.GONE
            }
        }
    }
}