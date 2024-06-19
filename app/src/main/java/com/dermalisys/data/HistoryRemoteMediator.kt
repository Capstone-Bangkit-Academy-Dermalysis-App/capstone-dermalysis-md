package com.dermalisys.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dermalisys.data.database.HistoryDatabase
import com.dermalisys.data.remote.response.getuserpredict.DataItem
import com.dermalisys.data.remote.retrofit.ApiConfig

@OptIn(ExperimentalPagingApi::class)
class HistoryRemoteMediator(
    private val database: HistoryDatabase,
    private val signature: String,
    val userId: String,
    val accessToken: String
): RemoteMediator<Int, DataItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DataItem>
    ): MediatorResult {
        val page = INITIAL_PAGE_INDEX
        try {
            val responseData = ApiConfig.getApiService(signature).getAllUserHistory(userId, accessToken, page, state.config.pageSize)
            val endOfPaginationReached = responseData.data.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.historyDao().deleteAll()
                }
                database.historyDao().insertHistory(responseData.data)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }
}