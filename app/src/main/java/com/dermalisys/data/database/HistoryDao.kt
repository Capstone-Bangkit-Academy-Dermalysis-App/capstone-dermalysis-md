package com.dermalisys.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dermalisys.data.remote.response.getuserpredict.DataItem

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(quote: List<DataItem>)

    @Query("SELECT * FROM history ORDER BY createdAt DESC")
    fun getAllHistory(): PagingSource<Int, DataItem>

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}