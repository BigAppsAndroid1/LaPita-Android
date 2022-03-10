package il.co.lapita.toppings

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dm6801.framework.infrastructure.AbstractDialog
import com.dm6801.framework.infrastructure.foregroundActivity
import il.co.lapita.R
import il.co.lapita.data.*
import il.co.lapita.infrastructure.BaseDialog
import il.co.lapita.utilities.glide
import il.co.lapita.utilities.mainColor
import il.co.lapita.utilities.onClick
import il.co.lapita.utilities.setThemeColor
import kotlinx.android.synthetic.main.dialog_base_toppings.*
import kotlinx.android.synthetic.main.item_base_topping.view.*

class BaseToppingsDialog : BaseDialog() {

    companion object : AbstractDialog.Comp<BaseToppingsDialog>() {

        private var selectedBaseToppings: List<Int>? = null
        private var shopProduct: ShopProduct? = null
        private lateinit var onFinish: (List<ShopTopping>?) -> Unit

        fun open(shopProduct: ShopProduct?, selectedBaseToppings: List<ShopTopping>? = null, onFinish: (List<ShopTopping>?) -> Unit) {
            Companion.shopProduct = shopProduct
            Companion.selectedBaseToppings = selectedBaseToppings?.map { it.id }
            Companion.onFinish = onFinish
            BaseToppingsDialog.open()
        }

    }

    override val layout: Int get() = R.layout.dialog_base_toppings
    override val closeWithActivity: Boolean get() = false
    override val gravity: Int get() = Gravity.CENTER
    override val isCancelable: Boolean get() = false
    override val isBackgroundDim: Boolean get() = true
    override val heightFactor: Float get() = 0.8f
    override val widthFactor: Float get() = 0.9f

    private val back: ImageView? get() = base_toppings_back
    private val dynamicTitle: TextView? get() = base_toppings_dynamic_title
    private val image: ImageView? get() = base_toppings_image
    private val nameTextView: TextView? get() = base_toppings_name
    private val recycler: RecyclerView? get() = base_toppings_recycler
    private val submit: TextView? get() = base_toppings_submit
    private val adapter: BaseToppingsSelectionAdapter? get() = recycler?.adapter as? BaseToppingsSelectionAdapter

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        back?.onClick {
            if (selectedBaseToppings == null)
                foregroundActivity?.navigateBack()
            dismiss()
        }
        dynamicTitle?.text = shopProduct?.product?.baseToppingsDescription
        image?.glide(shopProduct?.product?.image)
        nameTextView?.text = shopProduct?.product?.name
        recycler?.adapter = BaseToppingsSelectionAdapter().apply {
            selectedBaseToppings?.takeIf { it.isNotEmpty() }?.let { selectedBaseToppings ->
                submitList(shopProduct?.shopBaseToppings?.mapIndexed { index, shopTopping ->
                    BaseToppingsSelectionAdapter.Item(
                        index.toLong(),
                        shopTopping,
                        shopTopping.id !in selectedBaseToppings
                    )
                } ?: listOf())
            } ?: submitList(shopProduct?.shopBaseToppings ?: listOf())
        }

        submit?.backgroundTintList = mainColor
        submit?.onClick {
            onFinish(adapter?.currentList?.filter { !it.isSelected }?.map { it.topping }
                ?: listOf())
            dismiss()
        }

    }
}