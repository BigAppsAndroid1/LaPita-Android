package il.co.lapita.product_list

import il.co.lapita.data.ShopProduct
import il.co.lapita.infrastructure.RecyclerAdapter

data class ProductAdapterItem(
    val product: ShopProduct
) : RecyclerAdapter.Identity<Int> {
    override val id = product.id
    override fun compareTo(other: Int) = id.compareTo(other)
}