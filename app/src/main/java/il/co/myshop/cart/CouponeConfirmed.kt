package il.co.lapita.cart

import android.view.View
import android.widget.Button
import android.widget.TextView
import il.co.lapita.R
import il.co.lapita.data.Database
import il.co.lapita.infrastructure.BaseDialog
import il.co.lapita.infrastructure.Locator.database
import il.co.lapita.infrastructure.foregroundFragment
import il.co.lapita.order.OrderTypeFragment
import il.co.lapita.utilities.getString
import il.co.lapita.utilities.onClick
import kotlinx.android.synthetic.main.dialog_coupon_confirmed.*


class CouponConfirmed : BaseDialog() {

    override val layout: Int get() = R.layout.dialog_coupon_confirmed
    override val closeWithActivity: Boolean get() = false
    override val isCancelable: Boolean get() = false
    //override val widthFactor: Float get() = 0.7f
    //override val heightFactor: Float get() = 0.4f

    private val text:TextView? get() = coupon_confirmed_text
    private val confirm:TextView? get() = coupon_confirmed_to_payment

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        confirm?.onClick {
            dismiss()
            (foregroundFragment as? CartFragment)?.disableCouponButton()
            if(database.shop?.branches.isNullOrEmpty())
                OrderTypeFragment.open()
        }
        text?.text =getString(R.string.coupon_confirmed, Database.coupon?.discount.toString())
    }

}