package com.adhibuchori.data.store.response

import com.google.gson.annotations.SerializedName

data class ProductsResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ItemsItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("sale")
	val sale: Int? = null,

	@field:SerializedName("productId")
	val productId: String? = null,

	@field:SerializedName("store")
	val store: String? = null,

	@field:SerializedName("productRating")
	val productRating: Float? = null,

	@field:SerializedName("brand")
	val brand: String? = null,

	@field:SerializedName("productName")
	val productName: String? = null,

	@field:SerializedName("productPrice")
	val productPrice: Int? = null
)

data class Data(

	@field:SerializedName("pageIndex")
	val pageIndex: Int? = null,

	@field:SerializedName("itemsPerPage")
	val itemsPerPage: Int? = null,

	@field:SerializedName("currentItemCount")
	val currentItemCount: Int? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("items")
	val items: List<ItemsItem?>? = null
)
