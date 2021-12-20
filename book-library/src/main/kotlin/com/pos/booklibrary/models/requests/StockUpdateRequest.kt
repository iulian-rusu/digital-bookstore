package com.pos.booklibrary.models.requests

data class StockUpdateRequest(var items: List<StockUpdateEntry>)