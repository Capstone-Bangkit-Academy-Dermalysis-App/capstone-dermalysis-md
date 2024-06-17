package com.dermalisys.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dermalisys.data.remote.response.getuserpredict.DataItem
import com.dermalisys.data.remote.retrofit.ApiConfig
import com.dermalisys.data.remote.retrofit.ApiService

class HistoryPagingSource(private val signature: String, private val userId: String, private val accessToken: String) : PagingSource<Int, DataItem>() {
    override fun getRefreshKey(state: PagingState<Int, DataItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = ApiConfig.getApiService(signature).getAllUserHistory(userId, accessToken, position, params.loadSize)
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}