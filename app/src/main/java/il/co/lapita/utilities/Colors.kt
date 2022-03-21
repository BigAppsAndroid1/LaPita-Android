package il.co.lapita.utilities

import android.content.res.ColorStateList
import com.dm6801.framework.ui.getColor
import il.co.lapita.R
import il.co.lapita.data.Shop

val newColor by lazy {
    ColorStateList.valueOf(getColor(R.color.orange) ?: Shop.getShopColor())
}
val saleColor by lazy {
    ColorStateList.valueOf(getColor(R.color.red) ?: Shop.getShopColor())
}
val mainColor get() = ColorStateList.valueOf(Shop.getShopColor())

