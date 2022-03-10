package il.co.lapita

object Dev {

    val isDev: Boolean
        get() = BuildConfig.DEBUG && BuildConfig.BUILD_TYPE != "release"

    operator fun invoke(action: Dev.() -> Unit): Boolean = false

    fun init() {}

    fun loadShop() {}

    fun loadUser() {}

}