package com.easyfetch.app.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.easyfetch.app.domain.UrlValidator
import com.easyfetch.app.ui.components.GradientButton
import com.easyfetch.app.ui.components.PlatformBadge
import com.easyfetch.app.ui.theme.ElectricCyan
import com.easyfetch.app.ui.theme.Midnight
import com.easyfetch.app.ui.theme.MidnightDeep
import com.easyfetch.app.ui.theme.MidnightSurface
import com.easyfetch.app.ui.theme.accentColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    sharedUrl: String?,
    onSharedUrlConsumed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            viewModel.downloadVideo()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    fun startDownload() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val writeGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (writeGranted) {
                viewModel.downloadVideo()
            } else {
                storagePermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        } else {
            viewModel.downloadVideo()
        }
    }

    LaunchedEffect(sharedUrl) {
        if (!sharedUrl.isNullOrBlank()) {
            viewModel.onSharedTextReceived(sharedUrl)
            onSharedUrlConsumed()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    val detectedPlatform = remember(uiState.urlInput) {
        UrlValidator.detectPlatform(uiState.urlInput)
    }

    Scaffold(
        containerColor = Midnight,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(MidnightDeep, Midnight, MidnightSurface)
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column {
                    Text(
                        text = "PASTE A LINK",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Drop a link.\nGet the video.",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PlatformRail(detectedPlatform = detectedPlatform)
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = uiState.urlInput,
                    onValueChange = viewModel::onUrlChanged,
                    label = { Text("Video link") },
                    placeholder = { Text("https://...") },
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = {
                            val clipText = clipboardManager.getText()?.text
                            if (!clipText.isNullOrBlank()) {
                                viewModel.onUrlChanged(clipText)
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MidnightSurface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = "Paste")
                    }

                    GradientButton(
                        text = "Get Video",
                        onClick = { viewModel.fetchVideo() },
                        enabled = !uiState.isLoading,
                        loading = uiState.isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    )
                }

                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Fetching video details...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                uiState.video?.let { video ->
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(if (video.thumbnailUrl.isNullOrBlank()) 140.dp else 280.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(video.platform.accentColor())
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MidnightSurface)
                                .padding(16.dp)
                        ) {
                            if (!video.thumbnailUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = video.thumbnailUrl,
                                    contentDescription = video.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            PlatformBadge(platform = video.platform)

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GradientButton(
                                    text = "Download",
                                    onClick = { startDownload() },
                                    enabled = !uiState.isDownloading,
                                    loading = uiState.isDownloading,
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Download,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(video.downloadUrl))
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MidnightDeep,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy link")
                                }

                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, video.downloadUrl)
                                        }
                                        context.startActivity(
                                            Intent.createChooser(sendIntent, "Share video link")
                                        )
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MidnightDeep,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
