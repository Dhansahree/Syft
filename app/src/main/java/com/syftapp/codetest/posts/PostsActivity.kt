package com.syftapp.codetest.posts

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.syftapp.codetest.Navigation
import com.syftapp.codetest.R
import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.utils.INTENT_DETAIL_POST
import com.syftapp.codetest.utils.PAGE_LIMIT
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_posts.*
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

class PostsActivity : AppCompatActivity(), PostsView, KoinComponent {

    private val presenter: PostsPresenter by inject()
    private val bag = CompositeDisposable()

    private lateinit var navigation: Navigation
    private lateinit var adapter: PostListAdapter

    companion object {
        var isFirstPage = true
        var selectedPostId = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        navigation = Navigation(this)

        val separator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        listOfPosts.addItemDecoration(separator)

        initViews()
    }

    private fun initViews() {
        presenter.isPostDeleted
            .subscribe {
                if (it) onDeleteSuccess()
                else onDeleteFail()
            }
            .addTo(bag)

        listOfPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                /**
                 * Pagination logic -
                 * 1. Increment the current page number by 1 when the last item is visible
                 * */
                val lastItemPos =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (presenter.nextPageNumber * PAGE_LIMIT == lastItemPos+1) {
                    presenter.nextPageNumber += 1
                    loadMoreItems(false)
                }
            }
        })

        // load the first page
        loadMoreItems(true)
    }

    private fun onDeleteFail() {
        showMsg(false)
    }

    private fun onDeleteSuccess() {
        adapter.deletePost(selectedPostId)
        showMsg(true)
    }

    private fun showMsg(flag: Boolean) {
        Snackbar.make(
            findViewById(android.R.id.content),
            if (flag) this.resources.getString(R.string.delete_success) else this.resources.getString(
                R.string.delete_fail
            ),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun loadMoreItems(isFirstPage: Boolean) {
        Companion.isFirstPage = isFirstPage
        presenter.bind(this)
    }

    override fun onDestroy() {
        presenter.unbind()
        bag.clear()
        super.onDestroy()
    }

    override fun render(state: PostScreenState) {
        when (state) {
            is PostScreenState.Loading -> showLoading()
            is PostScreenState.DataAvailable -> showPosts(state.posts)
            is PostScreenState.Error -> showError(getString(R.string.load_posts_error_message))
            is PostScreenState.FinishedLoading -> hideLoading()
            is PostScreenState.PostSelected -> {
                selectedPostId = state.post.id
                navigation.navigateToPostDetail(state.post.id)
            }
        }
    }

    private fun showLoading() {
        error.visibility = View.GONE
        listOfPosts.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
    }

    private fun showPosts(posts: List<Post>) {
        if (isFirstPage) {
            adapter = PostListAdapter(presenter)
            adapter.setList(posts)
            listOfPosts.adapter = adapter
        } else {
            adapter.addAll(posts)
        }
        listOfPosts.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        error.visibility = View.VISIBLE
        error.text = message
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_DETAIL_POST) {
            presenter.updateViewOnDelete(selectedPostId)
        }
    }
}
