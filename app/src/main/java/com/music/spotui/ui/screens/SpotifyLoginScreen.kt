package com.music.spotui.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.music.spotui.ui.navigation.Routes
import com.music.spotui.ui.theme.AppBackground
import com.music.spotui.ui.theme.AppPalette

/**
 * Informational replacement for the former Spotify WebView login.
 *
 * Spotify blocks embedded account pages on many devices. Spotui must not collect
 * passwords or force users through an opaque WebView, because Spotify is used for
 * metadata only while Deezer and other sources provide audio.
 */
@Composable
fun SpotifyLoginScreen(navController: NavController) {
    fun continueToHome() {
        navController.navigate(Routes.Home.route) {
            popUpTo(Routes.Login.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Spotify metadata is optional",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Spotui no longer opens a Spotify login page inside the app. Spotify can block embedded pages, which caused the black or error screen.",
                color = Color(0xFFB3B3B3),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Audio is provided by Deezer and other configured sources. You can continue, connect Deezer in Settings, and use local audio without entering a Spotify password here.",
                color = Color(0xFFB3B3B3),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Spotify catalog, radio, library sync and Echo Brain candidate injection stay unavailable until a supported metadata connection is configured.",
                color = Color(0xFF8D8D8D),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = ::continueToHome,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppPalette,
                    contentColor = Color.Black,
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text("Continue to Spotui", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = ::continueToHome, modifier = Modifier.fillMaxWidth()) {
                Text("Set up Deezer from Settings", color = Color(0xFFB3B3B3), fontSize = 14.sp)
            }
        }
    }
}
