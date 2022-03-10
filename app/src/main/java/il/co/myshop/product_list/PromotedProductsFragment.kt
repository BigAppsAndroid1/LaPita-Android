package il.co.lapita.product_list

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import il.co.lapita.R
import il.co.lapita.data.Database
import il.co.lapita.data.ProductType
import il.co.lapita.data.ShopProduct
import il.co.lapita.infrastructure.BaseFragment
import il.co.lapita.infrastructure.Locator
import il.co.lapita.product_list.*
import il.co.lapita.product_list.ProductViewHolder.Companion.openMeal
import il.co.lapita.toppings.ToppingsFragment
import il.co.lapita.utilities.mainColor
import il.co.lapita.utilities.onClick
import kotlinx.android.synthetic.main.fragment_network.*
import kotlinx.android.synthetic.main.fragment_promoted_products.*

class PromotedProductsFragment : BaseFragment() {

    override val layout: Int get() = R.layout.fragment_promoted_products


    private val listProducts: RecyclerView? get() = list_products
    private var promotedProductsListAdapter: PromotedProductsListAdapter? = null

    companion object{
         val cart get() = Locator.database.cart
    }


    private val promotedProductTitle: TextView? get() = promoted_product_title
    private val buttonNext: TextView? get() = button_next

    var onCancel:(()->Unit)? = null
    val cartProducts: List<ShopProduct>? get()  {
      return  cart.products.values.mapNotNull { it.product?.shopProductsAssociatedIds }
            .flatten().distinctBy { it }.filter { it !in cart.products.values.map { prod-> prod.productId } }
            .mapNotNull { cart.livePromotedProducts.value?.firstOrNull { product -> product.product.id == it } }.toList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setObservers()
        initRecyclerView()
        buttonNext?.backgroundTintList = mainColor
        promotedProductTitle?.text = Locator.database.shop?.associatedDescription
        buttonNext?.onClick {
            onCancel?.invoke()
        }
    }


    private fun initRecyclerView() {
        promotedProductsListAdapter = PromotedProductsListAdapter (
            chooseProduct = { isChecked, choosedProduct ->

            when {
                choosedProduct.product.productType == ProductType.PACK -> {
                    openMeal(choosedProduct)
                }
                choosedProduct.isToppings == true ||  choosedProduct.isBaseToppings == true -> {
                    ToppingsFragment.open(
                        choosedProduct.product.productType == ProductType.PIZZA,
                        choosedProduct,
                        cart[choosedProduct.id],
                        level = null
                    ){ toppings, options, baseToppings, _ ->
                        cart.set(
                            choosedProduct.id,
                            choosedProduct.product.category,
                            choosedProduct.unitType.type,
                            toppings?.size?.toFloat() ?: return@open,
                            comment = null,
                            isChecked = true,
                            toppings = toppings,
                            options = options,
                            baseToppings = baseToppings
                        )
                    }
                }
                else -> {
                    choosedProduct.id?.let { productId ->
                        Database.cart[productId]?.let { existProduct ->
                            cart.set(
                                productId,
                                existProduct.category,
                                existProduct.unitType?.type ?: return@let,
                                existProduct.amount + (existProduct.unitType?.multiplier ?: 0f),
                                comment = null,
                                isChecked = true,
                                toppings = null,
                                options = null,
                                baseToppings = null
                            )
                        } ?: cart.set(
                            productId,
                            choosedProduct.product.category,
                           choosedProduct.unitType.type,
                           choosedProduct.unitType.multiplier,
                            comment = null,
                            isChecked = true,
                            toppings = null,
                            options = null,
                            baseToppings = null
                        )
                    }
                }
            }
        } )
        listProducts?.adapter = promotedProductsListAdapter
        promotedProductsListAdapter?.submitList(cartProducts)

    }

//    private fun setObservers() {
//        cart.livePromotedProducts.observe(viewLifecycleOwner) {
//            promotedProductsListAdapter?.submitList(cartProducts)
//        }
//
//    }


    override fun onForeground() {
        super.onForeground()
        promotedProductsListAdapter?.notifyDataSetChanged()
    }
}