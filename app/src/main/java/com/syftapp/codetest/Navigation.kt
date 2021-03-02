package com.syftapp.codetest

import android.app.Activity
import com.syftapp.codetest.postdetail.PostDetailActivity
import com.syftapp.codetest.utils.INTENT_DETAIL_POST

class Navigation(private val context: Activity) {

    fun navigateToPostDetail(postId: Int) {
        context.startActivityForResult(PostDetailActivity.getActivityIntent(context, postId), INTENT_DETAIL_POST)
    }

}

