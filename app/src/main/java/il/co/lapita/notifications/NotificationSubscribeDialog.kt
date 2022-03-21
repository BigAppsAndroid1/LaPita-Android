package il.co.lapita.notifications

import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import il.co.lapita.infrastructure.Locator
import il.co.lapita.R
import il.co.lapita.infrastructure.BaseDialog
import com.dm6801.framework.ui.onClick
import il.co.lapita.data.Database
import kotlinx.android.synthetic.main.dialog_notification_subscribe.*

class NotificationSubscribeDialog : BaseDialog() {

    companion object : Comp<NotificationSubscribeDialog>() {
        private val database get() = Locator.database
        private val notifications get() = Locator.notifications
    }

    override val layout = R.layout.dialog_notification_subscribe
    override val widthFactor: Float get() = 0.8f
    override val heightFactor: Float get() = 0.5f
    override val gravity: Int = Gravity.CENTER
    private val text: TextView? get() = notification_subscribe_sub_label
    private val submit: TextView? get() = notification_subscribe_submit


    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        text?.text = Database.shop?.welcomeMessage
        submit?.onClick {
            database.setNotifications(true)
            notifications.init()
            dismiss()
        }
    }

}