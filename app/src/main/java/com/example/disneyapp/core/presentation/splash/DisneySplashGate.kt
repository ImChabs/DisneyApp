package com.example.disneyapp.core.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.disneyapp.R
import com.example.disneyapp.ui.theme.DisneyAppTheme
import com.example.disneyapp.ui.theme.DisneyBrushes
import com.example.disneyapp.ui.theme.DisneyColors
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun DisneySplashGate(
    content: @Composable () -> Unit,
) {
    var isSplashVisible by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(SplashDurationMillis)
        isSplashVisible = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()
        AnimatedVisibility(
            visible = isSplashVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = SplashEnterMillis)),
            exit = fadeOut(animationSpec = tween(durationMillis = SplashExitMillis)),
        ) {
            DisneySplashScreen()
        }
    }
}

@Composable
private fun DisneySplashScreen(
    modifier: Modifier = Modifier,
) {
    var isLogoVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isLogoVisible = true
    }

    val logoAlpha by animateFloatAsState(
        targetValue = if (isLogoVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = LogoEnterMillis,
            easing = FastOutSlowInEasing,
        ),
        label = "splashLogoAlpha",
    )
    val logoScale by animateFloatAsState(
        targetValue = if (isLogoVisible) 1f else 0.92f,
        animationSpec = tween(
            durationMillis = LogoEnterMillis,
            easing = FastOutSlowInEasing,
        ),
        label = "splashLogoScale",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splashTransition")
    val starPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = StarPulseMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "splashStarPulse",
    )
    val dustProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = DustSweepMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "splashDustProgress",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DisneyBrushes.catalogBackground),
    ) {
        SplashNightSky(
            starPulse = starPulse,
            dustProgress = dustProgress,
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .widthIn(max = 430.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = logoAlpha
                    scaleX = logoScale
                    scaleY = logoScale
                },
        ) {
            Image(
                painter = painterResource(R.drawable.disney_splash_castle),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.96f),
            )
            Image(
                painter = painterResource(R.drawable.disney_splash_wordmark),
                contentDescription = stringResource(R.string.splash_logo_content_description),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(0.88f),
            )
        }
    }
}

@Composable
private fun SplashNightSky(
    starPulse: Float,
    dustProgress: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val stars = listOf(
            Offset(size.width * 0.08f, size.height * 0.12f),
            Offset(size.width * 0.18f, size.height * 0.28f),
            Offset(size.width * 0.28f, size.height * 0.08f),
            Offset(size.width * 0.38f, size.height * 0.22f),
            Offset(size.width * 0.48f, size.height * 0.12f),
            Offset(size.width * 0.58f, size.height * 0.30f),
            Offset(size.width * 0.68f, size.height * 0.10f),
            Offset(size.width * 0.80f, size.height * 0.24f),
            Offset(size.width * 0.92f, size.height * 0.14f),
            Offset(size.width * 0.12f, size.height * 0.44f),
            Offset(size.width * 0.26f, size.height * 0.54f),
            Offset(size.width * 0.42f, size.height * 0.40f),
            Offset(size.width * 0.54f, size.height * 0.58f),
            Offset(size.width * 0.70f, size.height * 0.46f),
            Offset(size.width * 0.86f, size.height * 0.56f),
            Offset(size.width * 0.18f, size.height * 0.72f),
            Offset(size.width * 0.34f, size.height * 0.84f),
            Offset(size.width * 0.50f, size.height * 0.76f),
            Offset(size.width * 0.66f, size.height * 0.88f),
            Offset(size.width * 0.82f, size.height * 0.78f),
            Offset(size.width * 0.94f, size.height * 0.90f),
        )

        stars.forEachIndexed { index, offset ->
            val baseAlpha = when {
                index % 6 == 0 -> 0.58f
                index % 2 == 0 -> 0.42f
                else -> 0.28f
            }
            val pulse = if (index % 3 == 0) starPulse * 0.18f else 0f
            val radius = when {
                index % 6 == 0 -> 1.9.dp.toPx()
                index % 3 == 0 -> 1.45.dp.toPx()
                else -> 1.05.dp.toPx()
            }
            drawCircle(
                color = Color.White.copy(alpha = baseAlpha + pulse),
                radius = radius,
                center = offset,
            )
        }

        drawCircle(
            color = DisneyColors.BlueGlow.copy(alpha = 0.18f),
            radius = size.minDimension * 0.42f,
            center = Offset(size.width * 0.96f, size.height * 0.08f),
        )
        drawCircle(
            color = DisneyColors.MagentaGlow.copy(alpha = 0.12f),
            radius = size.minDimension * 0.36f,
            center = Offset(size.width * 0.10f, size.height * 0.86f),
        )
        drawCircle(
            color = DisneyColors.Gold.copy(alpha = 0.14f),
            radius = 3.dp.toPx(),
            center = Offset(size.width * 0.76f, size.height * 0.34f),
        )

        val head = Offset(
            x = size.width * (-0.12f + 1.24f * dustProgress),
            y = size.height * (0.48f - 0.24f * sin(dustProgress * PI).toFloat()),
        )
        repeat(18) { index ->
            val trailProgress = (dustProgress - index * 0.018f).coerceIn(0f, 1f)
            val trail = Offset(
                x = size.width * (-0.12f + 1.24f * trailProgress),
                y = size.height * (0.48f - 0.24f * sin(trailProgress * PI).toFloat()),
            )
            val alpha = ((18 - index) / 18f) * 0.32f
            drawCircle(
                color = DisneyColors.Gold.copy(alpha = alpha),
                radius = (2.4f - index * 0.08f).dp.toPx(),
                center = trail,
            )
        }
        drawCircle(
            color = DisneyColors.GoldSoft.copy(alpha = 0.82f),
            radius = 3.2.dp.toPx(),
            center = head,
        )
    }
}

private const val SplashDurationMillis = 2_340L
private const val SplashEnterMillis = 286
private const val SplashExitMillis = 416
private const val LogoEnterMillis = 910
private const val StarPulseMillis = 2_080
private const val DustSweepMillis = 2_210

@Preview(showBackground = true)
@Composable
private fun DisneySplashScreenPreview() {
    DisneyAppTheme(dynamicColor = false) {
        DisneySplashScreen()
    }
}
