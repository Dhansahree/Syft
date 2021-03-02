package com.syftapp.codetest.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.syftapp.codetest.R
import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.posts.PostsActivity.Companion.selectedPostId
import kotlinx.android.synthetic.main.view_post_list_item.view.*

class PostListAdapter(
    private val presenter: PostsPresenter
) : PagedListAdapter<Post, PostViewHolder>(PostsDiffCallback) {

    private val adapterList = mutableListOf<Post>()

    companion object {
        val PostsDiffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_post_list_item, parent, false)

        return PostViewHolder(view = view, presenter = presenter)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let { post ->
            holder.bind(post)
            holder.itemView.animation =
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_list_item)

            holder.itemView.setTag(R.id.postTitle, adapterList[position].id)
            holder.itemView.tag = adapterList[position].id

            holder.itemView.postDelete.setOnClickListener {
                holder.itemView.animation =
                    AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_slide_right)
                selectedPostId = holder.itemView.tag as Int
                presenter.updateViewOnDelete(holder.itemView.tag as Int)
            }
        }
    }

    override fun getItemCount(): Int {
        return adapterList.size
    }

    override fun getItem(position: Int): Post? {
        return adapterList[position]
    }

    fun setList(list: List<Post>) {
        adapterList.addAll(list)
        notifyDataSetChanged()
    }

    fun addAll(newList: List<Post>) {
        val lastIndex: Int = adapterList.count().minus(1)
        adapterList.addAll(newList)
        notifyItemRangeInserted(lastIndex, newList.size)
    }

    fun deletePost(postId: Int) {
        val itemPost = adapterList.find { it.id == postId }
        val index = adapterList.indexOf(itemPost)
        index.let {
            adapterList.removeAt(it)
            notifyItemRemoved(index)
        }
    }
}

class PostViewHolder(private val view: View, private val presenter: PostsPresenter) :
    RecyclerView.ViewHolder(view) {
    fun bind(item: Post) {
        view.postTitle.text = item.title
        view.bodyPreview.text = item.body
        view.postTitle.setOnClickListener { presenter.showDetails(item) }
    }
}