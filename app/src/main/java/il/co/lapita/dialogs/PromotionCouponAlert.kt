package il.co.lapita.dialogs

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dm6801.framework.infrastructure.foregroundActivity
import com.dm6801.framework.ui.getColor
import com.dm6801.framework.utilities.toast
import il.co.lapita.MainActivity
import il.co.lapita.R
import il.co.lapita.data.Database
import il.co.lapita.infrastructure.BaseDialog
import il.co.lapita.infrastructure.Locator
import il.co.lapita.network.NetworkFragment
import il.co.lapita.product_list.ProductListFragment
import il.co.lapita.utilities.*
import kotlinx.android.synthetic.main.alert_promotion_coupone.*

class PromotionCouponAlert: BaseDialog() {

    override val layout: Int get() = R.layout.alert_promotion_coupone
    override val gravity: Int get() = Gravity.CENTER
    override val closeWithActivity: Boolean get() = false
    override val isCancelable: Boolean get() = false
    override val isBackgroundDim: Boolean get() = true
    override val widthFactor: Float get() = 0.8f
    override val heightFactor: Float get() = 0.5f

    private val text: TextView? get() = promotion_text
    private val coupon: TextView? get() = promotion_coupon
    private val disclaimer: TextView? get() = promotion_disclaimer
    private val back: ImageView? get() = promotion_back
    private val download: TextView? get() = promotion_download

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        back?.onClick {
            (foregroundActivity as? MainActivity)?.clearBackStack()
            if (!Locator.database.network?.shops.isNullOrEmpty())
                NetworkFragment.open()
            ProductListFragment.open(Database.shop?.categories?.firstOrNull()?.name ?: return@onClick)
            dismiss()
        }

        text?.text = getString(R.string.promotion_text, Database.shop?.name.toString(), Database.promotionCoupon?.discount.toString())
        coupon?.text = getString(R.string.promotion_coupon, Database.promotionCoupon?.code.toString())

        download?.link("לחץ כאן!" to {
            this.view?.getScreenshot()?.let {
                saveImage(it, context, getString(R.string.app_name)){
                    toast("התמונה נשמרה בגלריה")
                    delay(500) {
                        back?.callOnClick()
                    }
                }
            }
        },color=getColor(R.color.black), isBold = true)

        disclaimer?.text = SpannableString(getString(R.string.promotion_disclamer_text, Database.promotionCoupon?.dateToFormat.toString())).apply {
            setSpan(UnderlineSpan(), 0, 10, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
        }

    }

}