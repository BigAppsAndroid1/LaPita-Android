package il.co.lapita.data

data class Coupon(
    val coupon: String,
    val discount: Int
){
    val getInPercent: Double get() = discount.toDouble() / 100
}