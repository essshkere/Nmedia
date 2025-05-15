package ru.tatalaraydar.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.tatalaraydar.nmedia.dto.Ad
import ru.tatalaraydar.nmedia.dto.FeedItem
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.entity.AttachmentType
import ru.tatalaraydar.nmedia.repository.PostRepositoryImpl.Companion.formatCount
import ru.tatalaraydar.nmedia.view.load


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onVideolink(post: Post) {}
    fun onViewPost(post: Post) {}
    fun onImageClick(post: Post) {}
    fun onAdClick(ad: Ad) {}
}

class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {
    private val typeAd = 0
    private val typePost = 1

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Ad -> typeAd
            is Post -> typePost
            null -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeAd -> AdViewHolder(
                CardAdBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            typePost -> PostViewHolder(
                CardPostBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // FIXME: students will do in HW
        getItem(position)?.let {
            when (it) {
                is Post -> (holder as? PostViewHolder)?.bind(it)
                is Ad -> (holder as? AdViewHolder)?.bind(it)
            }
        }
    }

    companion object {
        class AdViewHolder(
            private val binding: CardAdBinding,
            private val onInteractionListener: OnInteractionListener,
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(ad: Ad) {
                binding.apply {
                    image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
                    image.setOnClickListener {
                        onInteractionListener.onAdClick(ad)
                    }
                }
            }
        }

        class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
            override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
                if (oldItem::class != newItem::class) {
                    return false
                }

                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
                return oldItem == newItem
            }
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
            published.text = post.published.toString()
            content.text = post.content
            buttonLikes.text = formatCount(post.likes)
//            buttonShare.text = formatCount(post.share)
//            viewsPost.text = formatCount(post.views_post)
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

//
//            if (!post.videoURL.isNullOrEmpty()) {
//                videoLink.visibility = View.VISIBLE
//                videoPic.visibility = View.VISIBLE
//                videoLink.text = "Смотреть видео"
//                videoLink.setOnClickListener {
//                    onInteractionListener.onVideolink(post)
//                }
//            } else {
//                videoLink.visibility = View.GONE
//                videoPic.visibility = View.GONE
//            }
        }
    }
}