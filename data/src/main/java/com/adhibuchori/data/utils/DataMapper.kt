package com.adhibuchori.data.utils

import com.adhibuchori.data.transaction.cart.entity.CartEntity
import com.adhibuchori.data.productDetail.response.Data
import com.adhibuchori.data.productDetail.response.DataItem
import com.adhibuchori.data.store.request.ProductsRequest
import com.adhibuchori.data.wishlist.entity.WishlistEntity
import com.adhibuchori.domain.repository.cart.CartModel
import com.adhibuchori.domain.repository.productDetail.ProductDetailModel
import com.adhibuchori.domain.repository.productDetail.ProductReviewModel
import com.adhibuchori.domain.repository.productDetail.ProductVariant
import com.adhibuchori.domain.repository.wishlist.WishlistModel

fun ProductsRequest.toQueryMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    search?.let { map["search"] = it }
    brand?.let { map["brand"] = it }
    lowest?.let { map["lowest"] = it.toString() }
    highest?.let { map["highest"] = it.toString() }
    sort?.let { map["sort"] = it }
    limit?.let { map["limit"] = it.toString() }
    page?.let { map["page"] = it.toString() }
    return map
}

fun Data.toProductDetailModel() = ProductDetailModel(
    productId = productId.orEmpty(),
    productName = productName.orEmpty(),
    productPrice = productPrice ?: 0,
    image = image?.map { it.orEmpty() } ?: emptyList(),
    brand = brand.orEmpty(),
    description = description.orEmpty(),
    store = store.orEmpty(),
    sale = sale ?: 0,
    stock = stock ?: 0,
    totalRating = totalRating ?: 0.0,
    totalReview = totalReview ?: 0,
    totalSatisfaction = totalSatisfaction ?: 0,
    productRating = productRating ?: 0.0,

    productVariant = productVariant?.map { variant ->
        ProductVariant(
            variantName = variant?.variantName.orEmpty(),
            variantPrice = variant?.variantPrice ?: 0
        )
    } ?: emptyList()
)

fun DataItem.toProductReviewModel() = ProductReviewModel(
    userName = userName.orEmpty(),
    userImage = userImage.orEmpty(),
    userRating = userRating ?: 0.0,
    userReview = userReview.orEmpty()
)

fun List<DataItem?>.toProductReviewList() =
    map { it?.toProductReviewModel() ?: ProductReviewModel() }.toList()

fun WishlistModel.toWishlistEntity() = WishlistEntity(
    wishlistId = wishlistId,
    productId = productId,
    productName = productName,
    productPrice = productPrice,
    productImage = productImage,
    productStock = productStock,
    productVariant = productVariant,
    productStore = productStore,
    productReview = productReview
)

fun WishlistEntity.toWishlistModel() = WishlistModel(
    wishlistId = wishlistId,
    productId = productId,
    productName = productName,
    productPrice = productPrice,
    productImage = productImage,
    productStock = productStock,
    productVariant = productVariant,
    productStore = productStore,
    productReview = productReview
)

fun List<WishlistEntity>.toWishlistProductList() = map { it.toWishlistModel() }.toList()

fun mapProductDetailToWishlistModel(
    productDetail: ProductDetailModel?,
    productVariant: ProductVariant?,
): WishlistModel {
    return productDetail?.let { productDetailItem ->
        WishlistModel(
            wishlistId = null,
            productId = productDetailItem.productId,
            productName = productDetailItem.productName,
            productPrice = productDetailItem.productPrice,
            productImage = productDetailItem.image.firstOrNull().orEmpty(),
            productStock = productDetailItem.stock,
            productVariant = productVariant?.variantName.orEmpty(),
            productStore = productDetailItem.store,
            productReview = productDetailItem.productRating
        )
    } ?: WishlistModel()
}

fun CartModel.toCartEntity() = CartEntity(
    cartId = cartId,
    productId = productId,
    productName = productName,
    productImage = productImage,
    productStock = productStock,
    productVariant = productVariant,
    productPrice = productPrice,
    productCount = productCount,
    isChecked = isChecked
)

fun CartEntity.toCartModel() = CartModel(
    cartId = cartId,
    productId = productId,
    productName = productName,
    productImage = productImage,
    productStock = productStock,
    productVariant = productVariant,
    productPrice = productPrice,
    productCount = productCount,
    isChecked = isChecked
)

fun List<CartEntity>.toCartProductList() = map { it.toCartModel() }.toList()

fun mapProductDetailToCartModel(
    productDetail: ProductDetailModel?,
    selectedVariant: ProductVariant?,
): CartModel {
    return productDetail?.let { productDetailItem ->
        CartModel(
            cartId = null,
            productId = productDetailItem.productId,
            productName = productDetailItem.productName,
            productImage = productDetailItem.image.firstOrNull().orEmpty(),
            productStock = productDetailItem.stock,
            productVariant = selectedVariant?.variantName.orEmpty(),
            productPrice = productDetailItem.productPrice
        )
    } ?: CartModel()
}

fun mapWishlistItemToCartModel(
    wishlistItem: WishlistModel,
): CartModel {
    return CartModel(
        productId = wishlistItem.productId,
        productName = wishlistItem.productName,
        productImage = wishlistItem.productImage,
        productStock = wishlistItem.productStock,
        productVariant = wishlistItem.productVariant,
        productPrice = wishlistItem.productPrice
    )
}