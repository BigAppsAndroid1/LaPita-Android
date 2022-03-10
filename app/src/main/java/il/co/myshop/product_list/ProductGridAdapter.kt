package il.co.lapita.product_list

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import il.co.lapita.R
import il.co.lapita.data.Level
import il.co.lapita.data.ShopProduct
import il.co.lapita.infrastructure.LayoutManager
import il.co.lapita.infrastructure.RecyclerAdapter

class ProductGridAdapter(
    val mealCallback: ((
        product: ShopProduct,
        amount: Float?,
        baseToppings: MutableList<MutableList<Pair<Int, List<Int>>>>?,
        toppings: MutableList<MutableList<Pair<Int, List<Int>>>>?,
        options: MutableList<MutableList<Pair<Int, List<Int>>>>?
    )->Unit)? = null,
    var level: Level? = null,
    val preloadThreshold: Int = 10,
    val preload: ((end: Boolean) -> Unit)? = null
) : RecyclerAdapter<ProductViewHolder, ProductAdapterItem>(), ProductsAdapter {

    override val layout = R.layout.item_product
    override val adapterClasses = listOf(ProductsAdapter::class.java)
    override val viewHolderClass = ProductViewHolder::class.java
    override val layoutManager: LayoutManager? get() = recyclerView?.layoutManager as? LayoutManager
    override var focusedProductId: Int? = null
    override var focusedPosition: Int? = null
    override var canMealProductButtonAction: Boolean? = true

    init {
        setHasStableIds(true)
    }

    override fun submitList(list: List<ShopProduct>) {
        submitList(list.map { ProductAdapterItem(it) })
    }

    @Suppress("UNUSED_PARAMETER")
    fun submitList(list: List<ShopProduct>, a: Unit = Unit, callback: () -> Unit) {
        submitList(list.map { ProductAdapterItem(it) }, callback)
    }

    override fun internalOnBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        super.internalOnBindViewHolder(holder, position, payloads)
        holder.mealCallback = mealCallback
        holder.level = level
        val countFromEnd = ((currentList ?: emptyList()).size - position).coerceAtLeast(0)
        if (countFromEnd <= preloadThreshold && (currentList?.size ?:0 ) >= 20)
            preload?.invoke(false)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.layoutManager = LayoutManager(recyclerView.context, 2)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    preload?.invoke(true)
                }
            }
        })
        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

}