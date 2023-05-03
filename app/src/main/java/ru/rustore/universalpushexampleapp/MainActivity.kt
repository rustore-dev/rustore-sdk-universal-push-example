package ru.rustore.universalpushexampleapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rustore.sdk.core.tasks.OnCompleteListener
import ru.rustore.sdk.universalpush.RuStoreUniversalPushClient
import ru.rustore.sdk.universalpush.UNIVERSAL_FCM_PROVIDER
import ru.rustore.sdk.universalpush.UNIVERSAL_HMS_PROVIDER
import ru.rustore.sdk.universalpush.UNIVERSAL_RUSTORE_PROVIDER
import ru.rustore.universalpushexampleapp.theme.PushTheme


class MainActivity : ComponentActivity() {
    val viewModel by viewModels<UniversalPushViewModel> ()

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
            PushTheme {
                Screen(viewModel = viewModel)
            }
        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    val viewModel = UniversalPushViewModel()
    PushTheme {
        Screen(viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(viewModel: UniversalPushViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.surface
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }) {
        Column(modifier = Modifier.padding(it)) {
            TokenCard(
                type = "HMS",
                availability = viewModel.hmsAvailability,
                token = viewModel.hmsToken
            )
            TokenCard(
                type = "FCM",
                availability = viewModel.fcmAvailability,
                token = viewModel.fcmToken
            )
            TokenCard(
                type = "RuStore",
                availability = viewModel.rustoreAvailability,
                token = viewModel.rustoreToken
            )
        }
    }
}

@Composable
fun TokenCard(
    type: String,
    availability: MutableState<Boolean>,
    token: MutableState<String>
) {
    val context = LocalContext.current

    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                    append("$type availability: ")
                }
                append("${availability.value}")
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(4.dp),
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                        append("Token: ")
                    }
                    append(token.value.ifEmpty { "Unavailable" })
                }
            )
            IconButton(
                onClick = {
                    copyToClipBoard(context = context, text = token.value)
                    Toast.makeText(context, "Token is copied to clipboard", Toast.LENGTH_LONG)
                        .show()
                },
                enabled = token.value.isNotEmpty()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_content_copy_24),
                    contentDescription = "copy"
                )
            }
        }
    }
}

fun copyToClipBoard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("token", text)
    clipboard.setPrimaryClip(clip)
}
