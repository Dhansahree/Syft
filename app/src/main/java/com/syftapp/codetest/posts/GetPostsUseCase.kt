package com.syftapp.codetest.posts

import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.data.repository.BlogRepository
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent

class GetPostsUseCase(private val repository: BlogRepository) : KoinComponent {

    fun executePaged(pageNum: Int, pageLimit: Int): Single<List<Post>> {
        // users must be available for the blog posts
        return repository.getUsers()
            .ignoreElement()
            .andThen(repository.getPosts(pageNum = pageNum, pageLimit = pageLimit))
    }

    fun executePostDelete(postId: Int): Completable {
        return repository.deletePost(id = postId)
    }
}