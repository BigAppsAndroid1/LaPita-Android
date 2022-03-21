package il.co.lapita.language

import androidx.annotation.StringRes
import il.co.lapita.R
import java.util.*

enum class Language(
    @StringRes val label: Int,
    val locale: Locale
) {
    French(
        R.string.french,
        Locale("fr")
    ),
    Hebrew(
        R.string.hebrew,
        Locale("iw")
    ),
    Arabic(
        R.string.arabic,
        Locale("ar")
    )
}