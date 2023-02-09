package com.example.pagedlistcompose

//what one paginator should be able to do
interface Paginator<Key,Item> {
    suspend fun loadNextItems()
    fun reset()
}