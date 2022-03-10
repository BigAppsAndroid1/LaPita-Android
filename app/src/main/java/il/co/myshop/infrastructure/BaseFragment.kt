package il.co.lapita.infrastructure

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.dm6801.framework.infrastructure.AbstractFragment
import com.dm6801.framework.infrastructure.foregroundActivity
import il.co.lapita.R
import il.co.lapita.search.SearchFragment
import il.co.lapita.widgets.MenuBar
import il.co.lapita.widgets.CategoriesBar
import com.dm6801.framework.ui.*
import com.dm6801.framework.utilities.background
import com.dm6801.framework.utilities.withMain
import com.google.android.material.snackbar.Snackbar
import il.co.lapita.MainActivity
import il.co.lapita.data.Shop
import il.co.lapita.product_list.PromotedProductsFragment
import il.co.lapita.infrastructure.Locator.database
import il.co.lapita.remote.Remote
import kotlinx.android.synthetic.main.dialog_deliver_price.*

abstract class BaseFragment : AbstractFragment() {

    override val activity: MainActivity?
        get() = super.activity as? MainActivity

    protected open val themeBackground: Drawable? = Shop.getBackground()?.let { getDrawable(it) } ?: getDrawable(R.drawable.bg_3)
    val menuBar: MenuBar? get() = view?.findViewById(R.id.menu_bar)
    val categoriesBar: CategoriesBar? get() = view?.findViewById(R.id.categories_bar)

    protected open val isMenuBar: Boolean = true
    protected open val isSwitchBar: Boolean = true
    protected open val isCheckoutBar: Boolean = true
    protected open val isSearch: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.background = themeBackground
        menuBar?.isVisible = isMenuBar
        categoriesBar?.isVisible = isSwitchBar && (database.shop?.categories?.size ?: 0) > 1
        if (isMenuBar) {
            menuBar?.toggleSearch(isSearch, false)
        }
    }

    override fun onBackPressed(): Boolean {
        if (menuBar?.isSearchMode == true && this !is SearchFragment) {
            menuBar?.exitSearch()
            return true
        }
        return super.onBackPressed()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean? {
        if (menuBar?.searchEdit?.hasFocus() == true &&
            ev?.let { !menuBar?.searchEdit.wasClicked(it) } == true &&
            menuBar?.searchEdit?.text.isNullOrBlank()
        ) menuBar?.exitSearch()
        return super.dispatchTouchEvent(ev)
    }

    fun showSnackBar(text: String){
        val snack: Snackbar = Snackbar.make(view ?: return, text, Snackbar.LENGTH_LONG)
        snack.setTextColor(getColor(R.color.grey) ?: Color.GRAY)
        snack.animationMode = Snackbar.ANIMATION_MODE_FADE
        snack.duration = 600
        val snackView = snack.view
        val mTextView = snackView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        mTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackView.setBackgroundColor(Color.WHITE)
        val params = snackView.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackView.layoutParams = params
        snackView.updateMargins(top = 48.dpToPx)
        snack.show()
    }

    fun openPromotedProducts() {
        val dialog = PromotedProductsFragment()
        dialog.onCancel = {
            foregroundActivity?.onBackPressed()
        }
        foregroundActivity?.open(dialog)
//        background {
//         val list =   PromotedProductsFragment.cart.products.values.mapNotNull { it.product?.shopProductsAssociatedIds }
//             .flatten().distinctBy { it }.filter { it !in PromotedProductsFragment.cart.products.values.map { prod-> prod.productId } }
//            list.takeIf { it.isNotEmpty() }?.let {
//                val products = Remote.getCartProducts(it)
//                withMain {
//                    val dialog = PromotedProductsFragment()
//                    dialog.onCancel = {
//                        foregroundActivity?.onBackPressed()
//                    }
//                    dialog.cartProducts = products
//                    foregroundActivity?.open(dialog)
//                }
//            } ?: return@background
//        }
    }

}