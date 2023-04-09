package ru.rustore.universalpushexampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.rustore.sdk.core.tasks.OnCompleteListener
import ru.rustore.sdk.universalpush.RuStoreUniversalPushClient
import ru.rustore.sdk.universalpush.UNIVERSAL_FCM_PROVIDER
import ru.rustore.sdk.universalpush.UNIVERSAL_HMS_PROVIDER
import ru.rustore.sdk.universalpush.UNIVERSAL_RUSTORE_PROVIDER

class MainActivity : ComponentActivity() {
    val viewModel = UniversalPushViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RuStoreUniversalPushClient.checkAvailability(this)
            .addOnCompleteListener(object : OnCompleteListener<Map<String, Boolean>> {
                override fun onSuccess(result: Map<String, Boolean>) {
                    viewModel.fcmAvailability.value = result[UNIVERSAL_FCM_PROVIDER] ?: false
                    viewModel.hmsAvailability.value = result[UNIVERSAL_HMS_PROVIDER] ?: false
                    viewModel.rustoreAvailability.value = result[UNIVERSAL_RUSTORE_PROVIDER] ?: false
                }

                override fun onFailure(throwable: Throwable) {
                    viewModel.errorAvailability.value = throwable.message.orEmpty()
                }
            })

        RuStoreUniversalPushClient.getTokens()
            .addOnCompleteListener(object : OnCompleteListener<Map<String, String>> {
                override fun onSuccess(result: Map<String, String>) {
                    viewModel.fcmToken.value = result[UNIVERSAL_FCM_PROVIDER].orEmpty()
                    viewModel.hmsToken.value = result[UNIVERSAL_HMS_PROVIDER].orEmpty()
                    viewModel.rustoreToken.value = result[UNIVERSAL_RUSTORE_PROVIDER].orEmpty()
                }

                override fun onFailure(throwable: Throwable) {
                    viewModel.errorToken.value = throwable.message.orEmpty()
                }
            })

        setContent {
            Screen(viewModel = viewModel)
        }
    }
}


@Composable
fun Screen(viewModel: UniversalPushViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Text("Availability Universal Push SDK")
            Text("hms availability: ${viewModel.hmsAvailability.value}")
            Text("fcm availability: ${viewModel.fcmAvailability.value}")
            Text("rustore availability: ${viewModel.rustoreAvailability.value}")
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(1.dp)
            )
            Text("Tokens Universal Push SDK")
            Text("hms token: ${viewModel.hmsToken.value}")
            Text("fcm token: ${viewModel.fcmToken.value}")
            Text("rustore token: ${viewModel.rustoreToken.value}")
        }
    }
}