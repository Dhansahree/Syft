package com.syftapp.codetest.data.api

import com.syftapp.codetest.data.model.mapper.*
import com.syftapp.codetest.data.repository.BlogDataProvider
import io.reactivex.Completable

class BlogApi(private val blogService: BlogService) : BlogDataProvider {

    override fun getUsers() = blogService.getUsers().map { it.apiToDomain(UserMapper) }

    override fun getComments() = blogService.getComments().map { it.apiToDomain(CommentMapper) }

    override fun getPosts(
        pageNum: Int,
        pageLimit: Int
    ) = blogService.getPosts(pageNum, pageLimit).map {
        it.apiToDomain(PostMapper)
    }

    /**
     * RestApi issue found during pagination - The rest api call always returns data for page=1
     * eg - https://jsonplaceholder.typicode.com/posts?_page=2&_limit=10
     * In this case _page is 2 still data of page 1 is displayed need to troubleshoot and verify
     *
     * There are various ways to achieving pagination using - Android Paging Runtime, Runtime Kotlin Extensions, Common etc libs
     * which currently have beta versions out. However I tried to create custom pagination
     *
     * */
    /*override fun getPagedPosts(
        pageNum: Int,
        pageLimit: Int
    ): Single<PagedList<com.syftapp.codetest.data.model.domain.Post>> = blogService.getPosts(pageNum, pageLimit).map { apiToPagedDomain(it) }*/

    override fun deletePost(id: Int): Completable = blogService.deletePost(id)
}