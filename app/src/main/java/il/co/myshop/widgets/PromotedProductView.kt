package il.co.lapita.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import il.co.lapita.R
import il.co.lapita.data.ShopProduct
import il.co.lapita.utilities.glide
import il.co.lapita.utilities.mainColor
import kotlinx.android.synthetic.main.view_promoted_product.view.*

class PromotedProductView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        inflate(context, R.layout.view_promoted_product, this)
        layoutDirection = View.LAYOUT_DIRECTION_RTL
    }

    private val imageView: ImageView? get() = promoted_product_image
    private val nameView: TextView? get() = promoted_product_name
    private val priceBg: View? get() = promoted_product_price_bg
    private val priceView: TextView? get() = promoted_product_price
    private val pennyView: TextView? get() = promoted_product_penny

    override fun onFinishInflate() {
        super.onFinishInflate()
        priceBg?.backgroundTintList = mainColor
    }

    fun setProduct(product: ShopProduct){
        imageView?.glide(product.product.image)
        nameView?.text = product.product.name
        val price = product.unitPrice.toString().split(".")[0]
        val penny = product.unitPrice.toString().split(".")[1]
        priceView?.text = price
        pennyView?.text = if (penny.length == 1) "${penny}0" else  penny
    }



}