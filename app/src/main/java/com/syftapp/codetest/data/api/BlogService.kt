package com.syftapp.codetest.data.api

import com.syftapp.codetest.data.model.api.Comment
import com.syftapp.codetest.data.model.api.Post
import com.syftapp.codetest.data.model.api.User
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogService {

    @GET("/users")
    fun getUsers(): Single<List<User>>

    @GET("/comments")
    fun getComments(): Single<List<Comment>>

    @GET("/posts")
    fun getPosts(
        @Query("_page") pageNum: Int,
        @Query("_limit") pageLimit: Int
    ): Single<List<Post>>

    @DELETE("/posts")
    fun deletePost(@Query("id") id: Int): Completable

    companion object {
        fun createService(retrofit: Retrofit): BlogService = retrofit.create(BlogService::class.java)
    }

}