package com.pos.booklibrary.api.requests

data class StockUpdateRequest(var items: List<StockUpdateEntry>)