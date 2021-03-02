package com.syftapp.codetest.data.model.mapper

import androidx.paging.PagedList
import com.syftapp.codetest.data.model.api.Post
import com.syftapp.codetest.utils.getPagedList

interface Mappable<Api, Domain> {
    fun map(api: Api): Domain
}

fun <Api, Domain> List<Api>.apiToDomain(model: Mappable<Api, Domain>): List<Domain> {
    return this.map { model.map(it) }
}

/*fun <Api, Domain> PagedList<Api>.apiToDomain(model: Mappable<Api, Domain>): PagedList<Domain> {
    return this.map { model.map(it) }
}

fun apiToPagedDomain(list: PagedList<Post>): PagedList<com.syftapp.codetest.data.model.domain.Post> {
    var _list = mutableListOf<com.syftapp.codetest.data.model.domain.Post>()
    list.forEach { _list.add(PostMapper.map(it)) }
    return getPagedList(_list)
}
*/