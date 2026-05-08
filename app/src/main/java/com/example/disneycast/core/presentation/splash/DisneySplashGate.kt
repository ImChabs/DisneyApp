package com.example.disneycast.core.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.disneycast.R
import com.example.disneycast.ui.theme.DisneyCastTheme
import com.example.disneycast.ui.theme.DisneyBrushes
import com.example.disneycast.ui.theme.DisneyColors
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
        if (!isSplashVisible) {
            content()
        }
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

    val logoAlpha = animateFloatAsState(
        targetValue = if (isLogoVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = LogoEnterMillis,
            easing = FastOutSlowInEasing,
        ),
        label = "splashLogoAlpha",
    )
    val logoScale = animateFloatAsState(
        targetValue = if (isLogoVisible) 1f else 0.92f,
        animationSpec = tween(
            durationMillis = LogoEnterMillis,
            easing = FastOutSlowInEasing,
        ),
        label = "splashLogoScale",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splashTransition")
    val starPulse = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = StarPulseMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "splashStarPulse",
    )
    val dustProgress = animateFloatAsState(
        targetValue = if (isLogoVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = DustSweepMillis,
            easing = LinearEasing,
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
                    alpha = logoAlpha.value
                    scaleX = logoScale.value
                    scaleY = logoScale.value
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
    starPulse: State<Float>,
    dustProgress: State<Float>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        StaticNightSky(modifier = Modifier.fillMaxSize())
        PulsingStars(
            starPulse = starPulse,
            modifier = Modifier.fillMaxSize(),
        )
        SparkleDust(
            dustProgress = dustProgress,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun StaticNightSky(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.drawWithCache {
            val stars = SplashStarFractions.mapIndexed { index, fraction ->
                val radius = when {
                    index % 6 == 0 -> 1.9.dp.toPx()
                    index % 3 == 0 -> 1.45.dp.toPx()
                    else -> 1.05.dp.toPx()
                }
                val alpha = when {
                    index % 6 == 0 -> 0.58f
                    index % 2 == 0 -> 0.42f
                    else -> 0.28f
                }
                SplashStar(
                    center = Offset(size.width * fraction.x, size.height * fraction.y),
                    radius = radius,
                    alpha = alpha,
                )
            }

            onDrawBehind {
                stars.forEach { star ->
                    drawCircle(
                        color = Color.White.copy(alpha = star.alpha),
                        radius = star.radius,
                        center = star.center,
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
            }
        },
    )
}

@Composable
private fun PulsingStars(
    starPulse: State<Float>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        SplashStarFractions.forEachIndexed { index, fraction ->
            if (index % 3 != 0) return@forEachIndexed

            val pulse = starPulse.value * 0.18f
            val radius = when {
                index % 6 == 0 -> 1.9.dp.toPx()
                else -> 1.45.dp.toPx()
            }
            drawCircle(
                color = Color.White.copy(alpha = pulse),
                radius = radius,
                center = Offset(size.width * fraction.x, size.height * fraction.y),
            )
        }
    }
}

@Composable
private fun SparkleDust(
    dustProgress: State<Float>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val progress = dustProgress.value
        val head = Offset(
            x = size.width * (-0.12f + 1.24f * progress),
            y = size.height * (0.48f - 0.24f * sin(progress * PI).toFloat()),
        )
        repeat(SparkleTrailCount) { index ->
            val trailProgress = (progress - index * SparkleTrailProgressStep).coerceIn(0f, 1f)
            val trail = Offset(
                x = size.width * (-0.12f + 1.24f * trailProgress),
                y = size.height * (0.48f - 0.24f * sin(trailProgress * PI).toFloat()),
            )
            val alpha = ((SparkleTrailCount - index) / SparkleTrailCount.toFloat()) * 0.32f
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

private data class SplashStar(
    val center: Offset,
    val radius: Float,
    val alpha: Float,
)

private const val SplashDurationMillis = 2_340L
private const val SplashEnterMillis = 286
private const val SplashExitMillis = 416
private const val LogoEnterMillis = 910
private const val StarPulseMillis = 2_080
private const val DustSweepMillis = 2_210
private const val SparkleTrailCount = 18
private const val SparkleTrailProgressStep = 0.018f

private val SplashStarFractions = listOf(
    Offset(0.08f, 0.12f),
    Offset(0.18f, 0.28f),
    Offset(0.28f, 0.08f),
    Offset(0.38f, 0.22f),
    Offset(0.48f, 0.12f),
    Offset(0.58f, 0.30f),
    Offset(0.68f, 0.10f),
    Offset(0.80f, 0.24f),
    Offset(0.92f, 0.14f),
    Offset(0.12f, 0.44f),
    Offset(0.26f, 0.54f),
    Offset(0.42f, 0.40f),
    Offset(0.54f, 0.58f),
    Offset(0.70f, 0.46f),
    Offset(0.86f, 0.56f),
    Offset(0.18f, 0.72f),
    Offset(0.34f, 0.84f),
    Offset(0.50f, 0.76f),
    Offset(0.66f, 0.88f),
    Offset(0.82f, 0.78f),
    Offset(0.94f, 0.90f),
)

@Preview(showBackground = true)
@Composable
private fun DisneySplashScreenPreview() {
    DisneyCastTheme(dynamicColor = false) {
        DisneySplashScreen()
    }
}
