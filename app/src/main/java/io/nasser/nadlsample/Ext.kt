package io.nasser.nadlsample

import android.content.Context

/**
 * Get dependency accessor
 */
internal fun Context.getDA(): App {
    return applicationContext as App
}

internal fun Context.getFeaturePlugin(): AppFeatureFacade = getDA().featurePlugin
