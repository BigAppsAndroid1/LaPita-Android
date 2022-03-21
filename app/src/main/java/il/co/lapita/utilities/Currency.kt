package il.co.lapita.utilities

import java.text.DecimalFormat

val currencyFormatter = DecimalFormat("#,##0.00")

fun Double.formatPrice(): String {
    return "\u20AA${currencyFormatter.format(this)}"
}