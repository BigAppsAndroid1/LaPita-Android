package il.co.lapita.toppings

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import il.co.lapita.data.Cart
import il.co.lapita.data.Level
import il.co.lapita.data.ShopProduct

class ToppingsPizzaPagerAdapter(
    parent: ToppingsFragment,
    val product: ShopProduct,
    val cartItem: Cart.Item?,
    val state: ToppingsState,
    val level: Level? = null
) : FragmentStateAdapter(parent.childFragmentManager, parent.lifecycle) {
    override fun getItemCount(): Int = state.count + 1
    override fun createFragment(position: Int): Fragment =
        if (!product.isToppings)
            BaseToppingsFragment.create(position, product, cartItem, state, level)
        else
            ToppingPizzaSelectionFragment.create(position, product, cartItem, state, level)

}