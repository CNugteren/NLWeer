// From https://stackoverflow.com/questions/50785840/change-locale-programmatically-in-kotlin
package foss.cnugteren.nlweer

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.ContextThemeWrapper
import androidx.preference.PreferenceManager
import java.util.*

class ApplicationLanguageHelper(base: Context) : ContextThemeWrapper(base, R.style.AppTheme) {
    companion object {

        fun wrap(context: Context): ContextThemeWrapper {

            // Determine the language to set
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val language = sharedPreferences.getString("language", "")
            var locale = Locale.getDefault()  // system default if nothing else found
            if (language != null && language in arrayListOf("en", "nl")) {
                locale = Locale(language)
            }

            // Sets the new language
            var newContext = context
            val config = newContext.resources.configuration
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setSystemLocale(config, locale)
            } else {
                setSystemLocaleLegacy(config, locale)
            }
            config.setLayoutDirection(locale)
            newContext = newContext.createConfigurationContext(config)
            return ApplicationLanguageHelper(newContext)
        }

        @SuppressWarnings("deprecation")
        fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun setSystemLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
        }
    }
}
