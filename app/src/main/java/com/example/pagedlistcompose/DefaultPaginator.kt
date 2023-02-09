package com.example.pagedlistcompose

class DefaultPaginator<Key, Item>(
    private val initialKey: Key,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key) -> Result<List<Item>>,
    private inline val getNextKey: suspend (List<Item>) -> Key,
    private inline val onError: suspend (Throwable?) -> Unit,
    private inline val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit
) : Paginator<Key, Item> {

    private var currentKey = initialKey

    // that will just be true as long as we make a request to our api or data base
    private var isMakingRequest = false

    //just to prevent a little issue of loading two pages at once if we
    //quickly call this function after another which sometime happen
    override suspend fun loadNextItems() {
        /*
        * if we are currently making the request then we just dont want to
        * load the next items and we just want to ignore it and wait to until
        * the next load next items call
        * */
        if (isMakingRequest){
            return
        }
        /*
        * if we are not we can say is making a request is true
        * because now we are making request
        * */
        isMakingRequest = true
        /*
        * we want to say we update the loading status to true
        * so we now start loading
        * */
        onLoadUpdated(true)
        /*
        * and then we can get the result using our own request function
        * where we can simply provide our current key as the next key
        * that we want to load
        * */
        val result = onRequest(currentKey)
        /*
        * after that we can set is making request to false
        * */
        isMakingRequest = false
        /*
        * so we actually get the items here from this so if every thing well
        * this get or else function will just return the list of items
        * if it did not run well then we get the throwable here so
        * the issue that actually happen in this case we call error
        * function and set onLoadingUpdated false and show error
        * */
        val items = result.getOrElse{
            onError(it)
            onLoadUpdated(false)
            return
        }
        /*
        * if we dont have a error we have a list fo item type so if that was
        * successfull we want to update our current key with the next key
        * with the function that we provided passing our items list
        * */
        currentKey = getNextKey(items)

        /*
        * and we want to say on success with our items list and our current key
        * */
        onSuccess(items,currentKey)

        /*
        * after successfull we set onLoadingUpdated to false
        * */
        onLoadUpdated(false)
    }

    override fun reset() {
        /*
        * it use for first start page or use as refresh
        * */
        currentKey = initialKey
    }
}