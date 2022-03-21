package il.co.lapita.dialogs

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.dm6801.framework.ui.dpToPx
import com.dm6801.framework.ui.setText
import il.co.lapita.R
import il.co.lapita.infrastructure.BaseDialog
import il.co.lapita.data.PaymentType
import il.co.lapita.infrastructure.Locator.database
import il.co.lapita.utilities.mainColor
import il.co.lapita.utilities.onClick
import il.co.lapita.utilities.setThemeColor
import kotlinx.android.synthetic.main.dialog_third_party_pay_code.*

class ThirdPartyPayCodeDialog : BaseDialog() {

    companion object : Comp<ThirdPartyPayCodeDialog>() {
        private val KEY_TYPE = "key_type"
        private val KEY_ON_FINISH = "key_on_finish"
        private val KEY_ON_DISMISS = "KEY_ON_DISMISS"
        fun open(type: PaymentType, onDismiss: () -> Unit, onFinish: () -> Unit) {
            open(KEY_TYPE to type, KEY_ON_DISMISS to onDismiss, KEY_ON_FINISH to onFinish)
        }
    }

    override val layout: Int get() = R.layout.dialog_third_party_pay_code

    override val gravity: Int = android.view.Gravity.CENTER
    override val widthFactor = 0.8f
    override val heightFactor = 0.33f
    override val isCancelable: Boolean get() = false
    override val closeWithActivity: Boolean get() = false
    private val dialogClose: ImageView? get() = dialog_code_close
    private val dialogSend: TextView? get() = dialog_code_send
    private val dialogCodeField: EditText? get() = dialog_code_field
    private var type: PaymentType? = null
    private var onFinish: (() -> Unit)? = null
    private var onDismiss: (() -> Unit)? = null

    @Suppress("UNCHECKED_CAST")
    override fun onArguments(arguments: Map<String, Any?>) {
        super.onArguments(arguments)
        (arguments[KEY_TYPE] as? PaymentType)?.let { type = it }
        (arguments[KEY_ON_FINISH] as? (() -> Unit))?.let { onFinish = it }
        (arguments[KEY_ON_DISMISS] as? (() -> Unit))?.let { onDismiss = it }
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        dialogClose?.onClick {
            onDismiss?.invoke()
            dismiss()
        }
        dialogSend?.setThemeColor()
        dialogSend?.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(1.dpToPx, mainColor)
            cornerRadius = 20.dpToPx.toFloat()
        }
        dialogCodeField?.setText(
            when (type) {
                PaymentType.Cibus -> database.cibus
                PaymentType.Tenbis -> database.tenbis
                PaymentType.Goodi -> database.goodi
                else -> {
                    close()
                }
            }
        )
        dialogSend?.onClick {
            if (dialogCodeField?.text?.isEmpty() == true) return@onClick
            when(type){
                PaymentType.Cibus -> database.cibus = dialogCodeField?.text?.toString()
                PaymentType.Tenbis -> database.tenbis = dialogCodeField?.text?.toString()
                PaymentType.Goodi -> database.goodi = dialogCodeField?.text?.toString()
                else -> { }
            }
            onFinish?.invoke()
            dismiss()
        }
    }
}