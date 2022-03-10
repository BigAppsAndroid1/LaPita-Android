package il.co.lapita.product_list

import androidx.recyclerview.widget.RecyclerView
import il.co.lapita.data.ShopProduct
import il.co.lapita.infrastructure.LayoutManager

interface ProductsAdapter {
    var recyclerView: RecyclerView?
    val layoutManager: LayoutManager?
    var focusedProductId: Int?
    var focusedPosition: Int?
    var canMealProductButtonAction: Boolean?
    fun submitList(list: List<ShopProduct>)
}