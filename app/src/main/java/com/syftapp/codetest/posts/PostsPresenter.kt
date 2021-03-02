package com.syftapp.codetest.posts

import com.syftapp.codetest.utils.PAGE_LIMIT
import com.syftapp.codetest.data.model.domain.Post
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.koin.core.KoinComponent

class PostsPresenter(private val getPostsUseCase: GetPostsUseCase) : KoinComponent {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: PostsView
    var nextPageNumber: Int = 1

    private val postDeleted = BehaviorSubject.create<Boolean>()
    val isPostDeleted: Observable<Boolean> = postDeleted

    fun bind(view: PostsView) {
        this.view = view
        compositeDisposable.add(loadPosts(nextPageNumber))
    }

    fun updateViewOnDelete(postId: Int) {
        compositeDisposable.add(deletePost(postId = postId))
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    fun showDetails(post: Post) {
        view.render(PostScreenState.PostSelected(post))
    }

    private fun loadPosts(pageNumber: Int) =
        getPostsUseCase.executePaged(pageNum = pageNumber, pageLimit = PAGE_LIMIT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view.render(PostScreenState.Loading) }
            .doAfterTerminate { view.render(PostScreenState.FinishedLoading) }
            .subscribe(
                {
                    view.render(PostScreenState.DataAvailable(it))
                },
                {
                    view.render(PostScreenState.Error(it))
                }
            )

    private fun deletePost(postId: Int) =
        getPostsUseCase.executePostDelete(postId = postId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                postDeleted.onNext(true)
            }, {
                postDeleted.onNext(false)
            })

}