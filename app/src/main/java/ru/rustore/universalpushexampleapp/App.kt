package ru.rustore.universalpushexampleapp

import android.app.Application
import android.util.Log
import ru.rustore.sdk.core.tasks.OnCompleteListener
import ru.rustore.sdk.universalpush.RuStoreUniversalPushClient
import ru.rustore.sdk.universalpush.firebase.provides.FirebasePushProvider
import ru.rustore.sdk.universalpush.hms.providers.HmsPushProvider
import ru.rustore.sdk.universalpush.rustore.logger.DefaultLogger
import ru.rustore.sdk.universalpush.rustore.providers.RuStorePushProvider

class App: Application() {

    private val tag = "UniversalPushExampleApp"

    override fun onCreate() {
        super.onCreate()

        // ru.rustore.universalpushexampleapp
        RuStoreUniversalPushClient.init(
            context = this,
            rustore = RuStorePushProvider(
                application = this,
                projectId = "m3Id6aPeXq36mpNb5q000IodEDHlGhL0",
                logger = DefaultLogger(tag = tag),
            ),
            firebase = FirebasePushProvider(
                application = this,
            ),
            hms = HmsPushProvider(
                application = this,
                appid = "108003365",
            ),
        )

        RuStoreUniversalPushClient.getTokens()
            .addOnCompleteListener(object : OnCompleteListener<Map<String, String>> {
                override fun onSuccess(result: Map<String, String>) {
                    Log.w(tag, "get tokens success ${result}")
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e(tag, "get tokens err: ${throwable}")
                }
            })

        RuStoreUniversalPushClient.checkAvailability(this)
            .addOnCompleteListener(object : OnCompleteListener<Map<String, Boolean>> {
                override fun onSuccess(result: Map<String, Boolean>) {
                    Log.w(tag, "get availability success ${result}")
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e(tag, "get tokens err: ${throwable}")
                }
            })
    }
}