package com.syftapp.codetest.utils

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import com.syftapp.codetest.data.model.domain.Post
import java.util.concurrent.Executor

private val configPagedList = PagedList.Config.Builder()
    .setEnablePlaceholders(false)
    .setPageSize(PAGE_ITEMS_SIZE)
    .build()

class PagedListUtils<T>(private val items: List<T>) : PositionalDataSource<T>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        callback.onResult(items, 0, items.size)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        val start = params.startPosition
        val end = params.startPosition + params.loadSize
        callback.onResult(items.subList(start, end))
    }
}

class UiThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
        handler.post(command)
    }
}

//List<Domain.Post> -> PagedList<Domain.Post>
fun getPagedList(list: List<Post>): PagedList<Post> {
    return PagedList.Builder(PagedListUtils(list), configPagedList)
        .setNotifyExecutor(UiThreadExecutor())
        .setFetchExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        .build()

}