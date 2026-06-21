package com.easyfetch.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.easyfetch.app.EasyFetchApplication
import com.easyfetch.app.ui.EasyFetchApp
import com.easyfetch.app.ui.theme.EasyFetchTheme

class MainActivity : ComponentActivity() {

    private val sharedUrlState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedUrlState.value = extractSharedUrl(intent)

        val app = application as EasyFetchApplication

        setContent {
            val sharedUrl by sharedUrlState
            EasyFetchTheme {
                EasyFetchApp(
                    extractionRepository = app.extractionRepository,
                    mediaStorageRepository = app.mediaStorageRepository,
                    sharedUrl = sharedUrl,
                    onSharedUrlConsumed = { sharedUrlState.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        sharedUrlState.value = extractSharedUrl(intent)
    }

    private fun extractSharedUrl(intent: Intent): String? {
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            return intent.getStringExtra(Intent.EXTRA_TEXT)?.takeIf { it.isNotBlank() }
        }
        return null
    }
}
