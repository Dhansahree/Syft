package com.syftapp.codetest.data.dao

import androidx.room.*
import com.syftapp.codetest.data.model.domain.Post
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface PostDao {

    @Query("SELECT * FROM post WHERE id = :postId")
    fun get(postId: Int): Maybe<Post>

    @Query("SELECT * FROM post")
    fun getAll(): Single<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg posts: Post): Completable

    @Query("DELETE FROM post WHERE id = :postId")
    fun delete(postId: Int): Completable
}