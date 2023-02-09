package com.example.pagedlistcompose

import kotlinx.coroutines.delay


class Repository {
    private val remoteDataSource = (1..100).map {
        ListItem(
            title = "Item $it",
            description = "Descripiton $it"
        )
    }

    suspend fun getItems(page: Int,pageSize: Int): Result<List<ListItem>> {
        delay(2000L)
        val startPageIndex = page * pageSize
        return if (startPageIndex + pageSize <= remoteDataSource.size) {
            Result.success(
                remoteDataSource.slice(startPageIndex until startPageIndex + pageSize)
            )
        } else {
            Result.success(
                emptyList()
            )
        }
    }
}