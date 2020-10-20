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
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val systemLanguage = Locale.getDefault().toString().split('_')[0]
            var language = sharedPreferences.getString("language", systemLanguage)
            if (language != null && language == "system") { language = systemLanguage }

            var newContext = context
            val config = newContext.resources.configuration
            if (language != null && language != "") {
                val locale = Locale(language)
                Locale.setDefault(locale)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setSystemLocale(config, locale)
                } else {
                    setSystemLocaleLegacy(config, locale)
                }
                config.setLayoutDirection(locale)
                newContext = newContext.createConfigurationContext(config)
            }
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
