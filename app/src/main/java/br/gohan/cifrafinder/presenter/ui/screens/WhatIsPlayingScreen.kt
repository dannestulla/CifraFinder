package br.gohan.cifrafinder.presenter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.AppEvents
import br.gohan.cifrafinder.presenter.model.WhatIsPlayingState
import br.gohan.cifrafinder.presenter.ui.components.BottomNavigationBar
import br.gohan.cifrafinder.presenter.ui.components.LogoutConfirmationDialog
import br.gohan.cifrafinder.presenter.ui.theme.Background
import br.gohan.cifrafinder.presenter.ui.theme.CifraFinderTheme
import br.gohan.cifrafinder.presenter.ui.theme.Primary
import br.gohan.cifrafinder.presenter.ui.theme.Surface
import br.gohan.cifrafinder.presenter.ui.theme.TextPrimary
import br.gohan.cifrafinder.presenter.ui.theme.TextSecondary
import coil3.compose.AsyncImage

@Composable
fun WhatIsPlayingScreen(
    whatIsPlayingState: WhatIsPlayingState,
    snackbarHost: SnackbarHostState = remember { SnackbarHostState() },
    event: (AppEvents) -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {

            // Album Art
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .shadow(
                        elevation = 32.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Black.copy(alpha = 0.6f),
                        spotColor = Color.Black.copy(alpha = 0.6f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Surface,
                                Color(0xFF2A2A2A)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (whatIsPlayingState.artCover != null) {
                    AsyncImage(
                        model = whatIsPlayingState.artCover,
                        contentDescription = stringResource(R.string.album_cover) + whatIsPlayingState.songName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = TextSecondary.copy(alpha = 0.5f)
                    )
                }
            }

            // Song info - songName contains "Song - Artist" format
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val displayText = whatIsPlayingState.songName?.trim()
                    ?: stringResource(id = R.string.third_step_title)

                Text(
                    text = if (whatIsPlayingState.songName.isNullOrEmpty()) {
                        ""
                    } else {
                        stringResource(id = R.string.playing)
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )

                Text(
                    text = displayText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Search button
            Button(
                onClick = { event(AppEvents.MusicFetch) },
                enabled = !whatIsPlayingState.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (whatIsPlayingState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = stringResource(id = R.string.third_step_button_search_music),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bottom Navigation Bar
        BottomNavigationBar(
            onLogoutClick = { showLogoutDialog = true },
            onHomeClick = { /* Already on home */ },
            modifier = Modifier.align(Alignment.BottomCenter),
            hasTablature = !whatIsPlayingState.searchUrl.isNullOrEmpty(),
            onTablatureClick = { event(AppEvents.OpenTablature) }
        )

        // Snackbar
        SnackbarHost(
            hostState = snackbarHost,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )

        // Logout dialog
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onConfirm = {
                    showLogoutDialog = false
                    event(AppEvents.LogOff)
                },
                onDismiss = { showLogoutDialog = false }
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 390,
    heightDp = 844
)
@Composable
fun WhatIsPlayingScreenPreview() {
    CifraFinderTheme {
        WhatIsPlayingScreen(
            whatIsPlayingState = WhatIsPlayingState(
                songName = " Wonderwall - Oasis ",
                musicDurationInMiliSec = 0
            )
        ) {}
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 390,
    heightDp = 844
)
@Composable
fun WhatIsPlayingScreenEmptyPreview() {
    CifraFinderTheme {
        WhatIsPlayingScreen(
            whatIsPlayingState = WhatIsPlayingState(
                musicDurationInMiliSec = 0
            )
        ) {}
    }
}
