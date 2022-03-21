package il.co.lapita.toppings

import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dm6801.framework.infrastructure.AbstractDialog
import il.co.lapita.R
import il.co.lapita.data.ShopProduct
import il.co.lapita.data.ShopTopping
import il.co.lapita.infrastructure.BaseDialog
import il.co.lapita.utilities.mainColor
import il.co.lapita.utilities.onClick
import kotlinx.android.synthetic.main.dialog_choose_options.*

class DialogChooseOptions : BaseDialog() {

    companion object : AbstractDialog.Comp<DialogChooseOptions>() {

        private var areOptionsForFree: Boolean = false
        private var shopProduct: ShopProduct? = null
        private lateinit var onFinish: (ShopTopping?) -> Unit

        fun open(shopProduct: ShopProduct?, areOptionsForFree:Boolean = true, onFinish: (ShopTopping?) -> Unit) {
            Companion.shopProduct = shopProduct
            Companion.onFinish = onFinish
            Companion.areOptionsForFree = areOptionsForFree
            DialogChooseOptions.open()
        }

    }

    override val layout = R.layout.dialog_choose_options
    override val gravity: Int = Gravity.CENTER
    override val isCancelable: Boolean get() = false
    override val closeWithActivity: Boolean get() = false
    override val widthFactor = 0.86f
    override val heightFactor = 0.35f
    private val back: ImageView? get() = dialog_pizza_type_close
    private val titleText: TextView? get() = pizza_title_dialog
    private val recycler: RecyclerView? get() = list_types_pizza
    private val adapter: TypePizzaSelectionAdapter? get() = recycler?.adapter as? TypePizzaSelectionAdapter

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        back?.onClick {
            cancel()
            onFinish.invoke(null)
        }
        shopProduct?.product?.optionsDescription?.let { titleText?.text = it }
        titleText?.setTextColor(mainColor)
        initRecycler()
    }

    private fun initRecycler() {
        recycler?.adapter = TypePizzaSelectionAdapter(areOptionsForFree) { selected ->
            onCancel()
            onFinish(selected)

        }
        adapter?.submitList(shopProduct?.shopProductOptions ?: listOf())
    }

    override fun onCancel() {
        super.onCancel()
        dismiss()
    }
}






