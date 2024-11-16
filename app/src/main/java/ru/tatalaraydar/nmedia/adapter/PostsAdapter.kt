package ru.tatalaraydar.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.tatalaraydar.nmedia.databinding.CardPostBinding
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.repository.PostRepositoryInMemory


class PostsAdapter(private val onLikeListener:(Post,) -> Unit,
private val onShareListener: (Post) -> Unit)
: ListAdapter<Post, PostViewHolder>(PostDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding,onLikeListener, onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }


}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener:(Post) -> Unit,
    private val onShareListener: (Post) -> Unit

) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            content.text = post.content
            published.text = post.published
            author.text = post.author
            likes.text = PostRepositoryInMemory.formatCount(post.likes)
            viewsPost.text = PostRepositoryInMemory.formatCount(post.views_post)
            share.text = PostRepositoryInMemory.formatCount(post.share)

            buttonShare.setOnClickListener {
                onShareListener(post)
                // viewModel.share(post.id)
            }

            buttonLikes.setOnClickListener {
                onLikeListener(post)
                //viewModel.like(post.id)
            }
            buttonLikes.setImageResource(
                if (post.likedByMe) R.drawable.ic_like_24 else R.drawable.ic_likent_24
            )
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback <Post> (){
    override fun areItemsTheSame(oldItem: Post, newItem: Post)=oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post)  =oldItem == newItem

}