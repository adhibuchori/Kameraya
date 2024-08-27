package com.adhibuchori.data.utils

import com.adhibuchori.data.payment.cart.entity.CartEntity
import com.adhibuchori.data.productDetail.response.Data
import com.adhibuchori.data.productDetail.response.DataItem
import com.adhibuchori.data.store.request.ProductsRequest
import com.adhibuchori.data.payment.fullfilment.request.FulfillmentItemRequest
import com.adhibuchori.data.payment.fullfilment.request.FulfillmentRequest
import com.adhibuchori.data.payment.fullfilment.request.RatingRequest
import com.adhibuchori.data.payment.fullfilment.response.FulfillmentResponseData
import com.adhibuchori.data.payment.fullfilment.response.TransactionResponseData
import com.adhibuchori.data.payment.fullfilment.response.TransactionResponseItem
import com.adhibuchori.data.payment.paymentMethod.response.PaymentMethod
import com.adhibuchori.data.payment.paymentMethod.response.PaymentMethodItem
import com.adhibuchori.data.store.response.ProductItems
import com.adhibuchori.data.utils.extension.orZero
import com.adhibuchori.data.wishlist.entity.WishlistEntity
import com.adhibuchori.domain.notification.NotificationModel
import com.adhibuchori.domain.payment.cart.CartModel
import com.adhibuchori.domain.productDetail.ProductDetailModel
import com.adhibuchori.domain.productDetail.ProductReviewModel
import com.adhibuchori.domain.productDetail.ProductVariant
import com.adhibuchori.domain.payment.checkout.CheckoutModel
import com.adhibuchori.domain.payment.fulfillment.FulfillmentItemParameter
import com.adhibuchori.domain.payment.fulfillment.FulfillmentModel
import com.adhibuchori.domain.payment.fulfillment.FulfillmentParameter
import com.adhibuchori.domain.payment.paymentMethod.PaymentMethodItemModel
import com.adhibuchori.domain.payment.paymentMethod.PaymentMethodModel
import com.adhibuchori.domain.payment.rating.RatingParameter
import com.adhibuchori.domain.payment.transaction.TransactionModel
import com.adhibuchori.domain.payment.transaction.TransactionModelItem
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.domain.store.ProductsParameter
import com.adhibuchori.domain.wishlist.WishlistModel

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
    productPrice = productPrice.orZero(),
    image = image?.map { it.orEmpty() } ?: emptyList(),
    brand = brand.orEmpty(),
    description = description.orEmpty(),
    store = store.orEmpty(),
    sale = sale.orZero(),
    stock = stock.orZero(),
    totalRating = totalRating.orZero(),
    totalReview = totalReview.orZero(),
    totalSatisfaction = totalSatisfaction.orZero(),
    productRating = productRating.orZero(),

    productVariant = productVariant?.map { variant ->
        ProductVariant(
            variantName = variant?.variantName.orEmpty(),
            variantPrice = variant?.variantPrice.orZero()
        )
    } ?: emptyList()
)

fun DataItem.toProductReviewModel() = ProductReviewModel(
    userName = userName.orEmpty(),
    userImage = userImage.orEmpty(),
    userRating = userRating.orZero(),
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
            productPrice = productDetailItem.productPrice.plus(productVariant?.variantPrice.orZero()),
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
            productPrice = productDetailItem.productPrice.plus(selectedVariant?.variantPrice.orZero())
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

fun List<PaymentMethodItem?>.toPaymentMethodItemList() =
    map { it?.toPaymentMethodItem() ?: PaymentMethodItemModel() }.toList()

fun PaymentMethodItem.toPaymentMethodItem() = PaymentMethodItemModel(
    label = label.orEmpty(),
    image = image.orEmpty(),
    status = status == true
)

fun List<PaymentMethod?>.toPaymentSectionList() =
    map { it?.toPaymentSectionModel() ?: PaymentMethodModel() }.toList()

fun PaymentMethod.toPaymentSectionModel() = PaymentMethodModel(
    title = title.orEmpty(),
    item = item?.toPaymentMethodItemList().orEmpty()
)

fun CartModel.toCheckoutModel() = CheckoutModel(
    productId = productId,
    productName = productName,
    productImage = productImage,
    productStock = productStock,
    productVariant = productVariant,
    productPrice = productPrice,
    productCount = productCount
)

fun List<FulfillmentItemParameter?>.toFulfillmentItemParameterList() =
    map { it?.toFulfillmentItemParameter() ?: FulfillmentItemRequest() }.toList()

fun FulfillmentItemParameter.toFulfillmentItemParameter() = FulfillmentItemRequest(
    productId = productId.orEmpty(),
    variantName = variantName.orEmpty(),
    quantity = quantity.orZero()
)

fun FulfillmentParameter.toFulfillmentRequest() = FulfillmentRequest(
    payment = payment.orEmpty(),
    items = items?.toFulfillmentItemParameterList()
)

fun FulfillmentResponseData?.toFulfillmentModel() = this?.let {
    FulfillmentModel(
        date = it.date,
        total = it.total,
        invoiceId = it.invoiceId,
        payment = it.payment,
        time = it.time,
        status = it.status
    )
} ?: FulfillmentModel()

fun CartModel.toFulfillmentItemParameter() = FulfillmentItemParameter(
    productId = productId,
    quantity = productCount,
    variantName = productVariant
)

fun RatingParameter.toRatingRequest() = RatingRequest(
    invoiceId = invoiceId,
    rating = rating,
    review = review
)

fun TransactionResponseItem.toTransactionModelItem() = TransactionModelItem(
    quantity = quantity,
    productId = productId,
    variantName = variantName
)

fun List<TransactionResponseItem?>.toTransactionModelItemList() =
    map { it?.toTransactionModelItem() ?: TransactionModelItem() }.toList()

fun TransactionResponseData?.toTransactionModel() = this?.let {
    TransactionModel(
        date = it.date,
        image = it.image,
        total = it.total,
        review = it.review,
        rating = it.rating,
        name = it.name,
        invoiceId = it.invoiceId,
        payment = it.payment,
        time = it.time,
        items = it.items?.toTransactionModelItemList(),
        status = it.status
    )
} ?: TransactionModel()

fun TransactionModel?.toFulfillmentModel() = this?.let {
    FulfillmentModel(
        date = it.date,
        total = it.total,
        invoiceId = it.invoiceId,
        payment = it.payment,
        time = it.time,
        status = it.status,
        rating = it.rating,
        review = it.review
    )
} ?: FulfillmentModel()

fun TransactionModel?.toNotificationModel(
    paymentSuccessfulText: String,
    paymentFailedText: String,
    paymentSuccessfulDescriptionText: String,
    paymentStatusNotAvailableText: String,
): NotificationModel? {
    return this?.let {
        NotificationModel(
            notificationStatus = if (it.status == true)
                paymentSuccessfulText
            else
                paymentFailedText,
            notificationDescription = it.invoiceId?.let { id ->
                paymentSuccessfulDescriptionText.format(id)
            } ?: paymentStatusNotAvailableText
        )
    }
}

fun ProductsParameter.toRequest(loadSize: Int, page: Int): ProductsRequest {
    return ProductsRequest(
        search = this.search,
        brand = this.brand,
        lowest = this.lowest,
        highest = this.highest,
        sort = this.sort,
        limit = loadSize,
        page = page
    )
}

fun List<ProductItems?>?.toProductsModelList(): List<ProductsModel> {
    return this?.mapNotNull { item ->
        item?.let {
            ProductsModel(
                productId = it.productId.orEmpty(),
                productName = it.productName,
                productPrice = it.productPrice,
                image = it.image,
                brand = it.brand,
                store = it.store,
                sale = it.sale?.toFloat(),
                productRating = it.productRating
            )
        }
    } ?: emptyList()
}