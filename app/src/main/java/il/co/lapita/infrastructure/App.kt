package il.co.lapita.infrastructure

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import il.co.lapita.notifications.OneSignalExtender
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.dm6801.framework.infrastructure.AbstractApplication
import com.dm6801.framework.infrastructure.AbstractFragment
import com.dm6801.framework.infrastructure.foregroundActivity
import com.dm6801.framework.utilities.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import il.co.lapita.Dev
import il.co.lapita.MainActivity
import il.co.lapita.data.Cart
import il.co.lapita.data.Database
import il.co.lapita.data.ProductsRepository
import il.co.lapita.data.SharedPrefs
import il.co.lapita.language.LanguageManager
import kotlin.system.exitProcess

val foregroundFragment: AbstractFragment?
    get() = foregroundActivity?.supportFragmentManager?.fragments
        ?.filterNot { it is SupportRequestManagerFragment }
        ?.lastOrNull() as? AbstractFragment

class App : AbstractApplication() {

    private val Thread.isMainThread: Boolean get() = this == Looper.getMainLooper().thread
    var accessibilityLink: String? = null

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        OneSignalExtender.init()
        registerGlobalExceptionHandler()
        Dev.init()
    }

    private fun registerGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            if (!t.isMainThread) return@setDefaultUncaughtExceptionHandler
            e.printStackTrace()
            Cart.save()
            exitProcess(1)
        }
    }

    private fun restartApp() {
        Log("BLACK MAGIC restarting...")
        startActivity(Intent(instance, MainActivity::class.java)
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LanguageManager.attachBaseContext(base))
    }

}

object Locator {
    val sharedPreferences = SharedPrefs
    val json: Gson = GsonBuilder().setLenient().serializeNulls().setPrettyPrinting().create()
    val database = Database
    val repository = ProductsRepository
    val notifications = OneSignalExtender
}