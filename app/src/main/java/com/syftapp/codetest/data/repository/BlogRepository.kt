package com.syftapp.codetest.data.repository

import com.syftapp.codetest.data.api.BlogApi
import com.syftapp.codetest.data.dao.CommentDao
import com.syftapp.codetest.data.dao.PostDao
import com.syftapp.codetest.data.dao.UserDao
import com.syftapp.codetest.data.model.domain.Comment
import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.data.model.domain.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.koin.core.KoinComponent

class BlogRepository(
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val userDao: UserDao,
    private val blogApi: BlogApi
) : KoinComponent, BlogDataProvider {

    override fun getUsers(): Single<List<User>> {
        return fetchData(
            local = { userDao.getAll() },
            remote = { blogApi.getUsers() },
            insert = { value -> userDao.insertAll(*value.toTypedArray()) }
        )
    }

    override fun getComments(): Single<List<Comment>> {
        return fetchData(
            local = { commentDao.getAll() },
            remote = { blogApi.getComments() },
            insert = { value -> commentDao.insertAll(*value.toTypedArray()) }
        )
    }

    override fun getPosts(
        pageNum: Int,
        pageLimit: Int
    ): Single<List<Post>> {
        return fetchData(
            local = { postDao.getAll() },
            remote = { blogApi.getPosts(pageNum, pageLimit) },
            insert = { value -> postDao.insertAll(*value.toTypedArray()) }
        )
    }

    /**
     * On post item delete needs to be fixed for deleting the post item using rest calls
     * */
    override fun deletePost(id: Int): Completable {
        return deletePostItem(
            local = { postDao.delete(id) }
            /*,remote = { blogApi.deletePost(id) }*/
        )
    }

    fun getPost(postId: Int): Maybe<Post> {
        return postDao.get(postId)
    }

    private fun deletePostItem(
        local: () -> Completable/*,
        remote: () -> Single<Int>*/
    ): Completable {
        return local.invoke()
    }

    private fun <T> fetchData(
        local: () -> Single<List<T>>,
        remote: () -> Single<List<T>>,
        insert: (insertValue: List<T>) -> Completable
    ): Single<List<T>> {

        return local.invoke()
            .flatMap {
                if (it.isNotEmpty()) {
                    Single.just(it)
                } else {
                    remote.invoke()
                        .map { value ->
                            insert.invoke(value).subscribe();
                            value
                        }
                }
            }
    }
}
