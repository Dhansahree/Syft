package com.syftapp.codetest.data.api

import com.syftapp.codetest.data.model.domain.Comment
import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.data.model.domain.User
import io.mockk.spyk
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Test

class BlogApiTest {

    private val blogService = spyk<StubBlogService>()
    private val sut = BlogApi(blogService)

    @Test
    fun `get users contains correct domain models`() {
        val apiUser = rxValue(blogService.getUsers())[0]
        val users = rxValue(sut.getUsers())

        assertThat(users)
            .hasSize(2)
            .contains(
                User(
                    id = apiUser.id,
                    name = apiUser.name,
                    username = apiUser.username,
                    email = apiUser.email
                )
            )
    }

    @Test
    fun `get posts contains correct domain models`() {
        val apiPost = rxValue(blogService.getPosts(1, 20))[0]
        val posts = rxValue(sut.getPosts(1, 20))

        assertThat(posts)
            .hasSize(5)
            .contains(
                Post(
                    id = apiPost.id,
                    userId = apiPost.userId,
                    title = apiPost.title,
                    body = apiPost.body
                )
            )
    }

    @Test
    fun `get comments contains correct domain models`() {
        val apiComment = rxValue(blogService.getComments())[0]
        val comments = rxValue(sut.getComments())

        assertThat(comments)
            .hasSize(3)
            .contains(
                Comment(
                    id = apiComment.id,
                    postId = apiComment.postId,
                    name = apiComment.name,
                    email = apiComment.email,
                    body = apiComment.body
                )
            )
    }

    @Test
    fun `delete post using id return complete signal on success`() {
        val api = blogService.deletePost(1)
        val comments = sut.deletePost(1)

        Assert.assertEquals(comments, api)
    }

    /*@Test
    fun `delete post using id return error signal on fail`() {
        val api = blogService.deletePost(-1)
        val comments = sut.deletePost(-1)

        Assert.assertSame(comments, api)
    }*/

    private fun <T> rxValue(apiItem: Single<T>): T = apiItem.test().values()[0]
}