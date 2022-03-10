package il.co.lapita.product_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import il.co.lapita.R
import il.co.lapita.data.Database
import il.co.lapita.data.ShopProduct
import il.co.lapita.utilities.glide
import il.co.lapita.utilities.mainColor
import il.co.lapita.utilities.onClick
import kotlinx.android.synthetic.main.item_promoted_product.view.*
import kotlinx.android.synthetic.main.view_promoted_product.view.promoted_product_image
import kotlinx.android.synthetic.main.view_promoted_product.view.promoted_product_name
import kotlinx.android.synthetic.main.view_promoted_product.view.promoted_product_penny
import kotlinx.android.synthetic.main.view_promoted_product.view.promoted_product_price
import kotlinx.android.synthetic.main.view_promoted_product.view.promoted_product_price_bg

class PromotedProductsListAdapter(val chooseProduct: (isChecked:Boolean,product: ShopProduct) -> Unit) :
    ListAdapter<ShopProduct, PromotedProductsListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<ShopProduct>() {
            override fun areItemsTheSame(oldItem: ShopProduct, newItem: ShopProduct): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ShopProduct, newItem: ShopProduct): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    var isClickable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_promoted_product, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), chooseProduct)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView? get() = itemView.promoted_product_image
        private val nameView: TextView? get() = itemView.promoted_product_name
        private val priceBg: View? get() = itemView.promoted_product_price_bg
        private val priceView: TextView? get() = itemView.promoted_product_price
        private val pennyView: TextView? get() = itemView.promoted_product_penny
        private val productCheckBox: CheckBox? get() = itemView.product_check


        @SuppressLint("ClickableViewAccessibility", "ObjectAnimatorBinding")
        fun bind(
            product: ShopProduct,
            chooseProduct: (isChecked: Boolean, product: ShopProduct) -> Unit
        ) {

            priceBg?.backgroundTintList = mainColor
            productCheckBox?.backgroundTintList = mainColor
            imageView?.glide(product.product.image)
            nameView?.text = product.product.name
            val price = product.unitPrice.toString().split(".")[0]
            val penny = product.unitPrice.toString().split(".")[1]
            priceView?.text = price
            pennyView?.text = if (penny.length == 1) "${penny}0" else penny

            productCheckBox?.isChecked = product.id in PromotedProductsFragment.cart.products.values.map { prod-> prod.productId }

            itemView.onClick {
                productCheckBox?.isChecked = !(productCheckBox?.isChecked?:false)
               if (productCheckBox?.isChecked == true)
                    chooseProduct(productCheckBox?.isChecked == true, product)
                else{
                    Database.cart.remove(product.id)
                }
            }
        }
    }
}