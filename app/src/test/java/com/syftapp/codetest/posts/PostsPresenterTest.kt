package com.syftapp.codetest.posts

import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.rules.RxSchedulerRule
import com.syftapp.codetest.utils.PAGE_LIMIT
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verifyOrder
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostsPresenterTest {

    @get:Rule
    val rxRule = RxSchedulerRule()

    @MockK
    lateinit var getPostsUseCase: GetPostsUseCase

    @RelaxedMockK
    lateinit var view: PostsView

    private val anyPost = Post(1, 1, "title", "body")

    private val sut by lazy {
        PostsPresenter(getPostsUseCase)
    }

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `binding loads posts`() {
        every { getPostsUseCase.executePaged(1, PAGE_LIMIT) } returns Single.just(
            mutableListOf(
                anyPost
            )
        )
        sut.bind(view)

        verifyOrder {
            view.render(any<PostScreenState.Loading>())
            view.render(any<PostScreenState.DataAvailable>())
            view.render(any<PostScreenState.FinishedLoading>())
        }
    }

    @Test
    fun `error on binding shows error state after loading`() {
        every { getPostsUseCase.executePaged(1, PAGE_LIMIT) } returns Single.error(Throwable())

        sut.bind(view)

        verifyOrder {
            view.render(any<PostScreenState.Loading>())
            view.render(any<PostScreenState.FinishedLoading>())
            view.render(any<PostScreenState.Error>())
        }
    }

    @Test
    fun `updating delete post on success`() {
        every { getPostsUseCase.executePostDelete(1) } returns Completable.complete()

        sut.updateViewOnDelete(1)
        val testObserver = sut.isPostDeleted.test()
        testObserver.assertValue(true)
    }

    @Test
    fun `updating delete post on fail`() {
        every { getPostsUseCase.executePostDelete(1) } returns Completable.error(Throwable())

        sut.updateViewOnDelete(1)
        val testObserver = sut.isPostDeleted.test()
        testObserver.assertValue(false)
    }
}