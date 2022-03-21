package il.co.lapita.toppings

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
import il.co.lapita.data.ShopTopping
import il.co.lapita.utilities.glide
import il.co.lapita.utilities.mainColor
import il.co.lapita.utilities.onClick
import kotlinx.android.synthetic.main.item_base_topping.view.*

class BaseToppingsSelectionAdapter(onChange:(() -> Unit)? = null) :
    ListAdapter<BaseToppingsSelectionAdapter.Item, BaseToppingsSelectionAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    private var onChange:(()->Unit)? = null

    init {
        this.onChange = onChange
    }

    @Suppress("UNUSED_PARAMETER")
    fun submitList(toppings: List<ShopTopping>, a: Unit = Unit) {
        submitList(toppings.mapIndexed { index, shopTopping ->
            Item(
                index.toLong(),
                shopTopping,
                true
            )
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_base_topping, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView? get() = itemView.item_base_topping_name
        private val image: ImageView? get() = itemView.item_base_topping_img
        private val checkBox: CheckBox? get() = itemView.item_base_topping_check


        @SuppressLint("NotifyDataSetChanged")
        fun bind(item: Item) {
            name?.text = item.topping.topping.name
            image?.glide(item.topping.topping.imageKiosk)
            checkBox?.isChecked = item.isSelected
            checkBox?.backgroundTintList = mainColor
            itemView.onClick {
                item.isSelected = !item.isSelected
                notifyDataSetChanged()
                onChange?.invoke()
            }
        }
    }

    data class Item(
        val id: Long,
        val topping: ShopTopping,
        var isSelected: Boolean
    )
}
