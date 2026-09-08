package com.example.ui.scanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.scanner.SamplePackagesRepository
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.AppFontWeights
import com.example.ui.theme.AgriEmerald
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriHarvestGold
import com.example.ui.theme.AgriLeafGreen
import com.example.util.HapticFeedbackHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * CameraScreen composable using Accompanist permissions, CameraX live preview,
 * and a specialized visual viewfinder overlay for Legal Metrology Packaged Commodities
 * Rules (PCR 2011) label compliance auditing.
 */
@Composable
fun CameraScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisStep by viewModel.analysisStep.collectAsState()

    CameraScreen(
        onPhotoCaptured = { bitmap ->
            viewModel.analyzeCapturedBitmap(bitmap)
        },
        onSelectSample = { sample ->
            viewModel.selectSamplePackage(sample)
        },
        onNavigateBack = {
            viewModel.navigateTo(AppScreen.DASHBOARD)
        },
        isAnalyzing = isAnalyzing,
        analysisStep = analysisStep,
        modifier = modifier
    )
}

/**
 * Standalone CameraScreen composable with Accompanist Camera permission handling,
 * customized visual viewfinder alignment HUD, and Gemini Multimodal OCR triggers.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onPhotoCaptured: (Bitmap) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onSelectSample: ((com.example.data.model.SamplePackage) -> Unit)? = null,
    isAnalyzing: Boolean = false,
    analysisStep: String = "Analyzing compliance under Legal Metrology Rules, 2011..."
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // Accompanist runtime camera permission state
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    // Request permission automatically on screen entry if not granted
    LaunchedEffect(cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var isTorchOn by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var showRuleAssistance by remember { mutableStateOf(true) }
    var isWideFrameMode by remember { mutableStateOf(false) } // False: 4:3 Box/Pouch, True: 16:9 Sack/Bag

    var cameraProviderInstance by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val cameraExecutor: Executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                cameraProviderInstance?.unbindAll()
            } catch (e: Exception) {
                Log.e("CameraScreen", "Error unbinding camera", e)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (cameraPermissionState.status.isGranted) {
            // Live CameraX Preview View with TextureView (COMPATIBLE) to prevent BufferQueue abandonment
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("camerax_preview_view"),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            cameraProviderInstance = cameraProvider
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val capture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build()
                            imageCapture = capture

                            cameraProvider.unbindAll()
                            camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                capture
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraScreen", "CameraX binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )

            // Visual Viewfinder Overlay with alignment brackets, scanning sweep, and crosshairs
            ViewfinderOverlayCanvas(
                modifier = Modifier.fillMaxSize(),
                isWideMode = isWideFrameMode
            )

            // Top Status, Flash & Controls Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("button_back_camera")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Badge: Legal Metrology Gemini Vision
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = BorderStroke(1.dp, AgriEmerald)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AgriHarvestGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gemini AI • PCR 2011 Scanner",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Frame Mode Toggle (Standard vs Wide Bag)
                        IconButton(
                            onClick = {
                                HapticFeedbackHelper.vibrateClick(context)
                                isWideFrameMode = !isWideFrameMode
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .testTag("button_toggle_frame_aspect")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AspectRatio,
                                contentDescription = "Toggle Frame Shape",
                                tint = if (isWideFrameMode) AgriHarvestGold else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Prominent Torch Flashlight Toggle Pill with active status
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isTorchOn) AgriHarvestGold else Color.Black.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, if (isTorchOn) AgriHarvestGold else Color.White.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    HapticFeedbackHelper.vibrateClick(context)
                                    isTorchOn = !isTorchOn
                                    camera?.cameraControl?.enableTorch(isTorchOn)
                                }
                                .testTag("button_toggle_torch")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Flashlight Toggle",
                                    tint = if (isTorchOn) AgriForestGreen else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isTorchOn) "Flash ON" else "Flash OFF",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isTorchOn) AgriForestGreen else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rule 6 Compliance Checklist Helper Chips
                if (showRuleAssistance) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RuleCheckChip("Rule 6(1)(a) Mfg Details")
                        RuleCheckChip("Rule 6(1)(c) Net Qty (SI)")
                        RuleCheckChip("Rule 6(1)(da) MRP incl. taxes")
                        RuleCheckChip("Rule 6(1)(e) Unit Sale Price")
                        RuleCheckChip("Rule 6(1)(g) Consumer Care")
                    }
                }
            }

            // Viewfinder Alignment Instructions Callout (Positioned Above the Shutter Bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.72f),
                        border = BorderStroke(1.dp, AgriEmerald.copy(alpha = 0.6f)),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = AgriHarvestGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Align MRP, USP, Net Qty & Mfg text inside frame",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFE8F5E9),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Shutter & Controls HUD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.82f))
                    .padding(bottom = 24.dp, top = 14.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Benchmark Quick Samples (Instant test packages)
                if (onSelectSample != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SamplePackagesRepository.samplePackages.take(4).forEach { sample ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .clickable {
                                        HapticFeedbackHelper.vibrateClick(context)
                                        onSelectSample(sample)
                                    }
                                    .testTag("quick_sample_${sample.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Layers,
                                        contentDescription = null,
                                        tint = AgriHarvestGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = sample.title.take(22) + "...",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Shutter Bar Row with Thumb-Reachable Flashlight Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Switch Camera Lens (Front / Back)
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.vibrateClick(context)
                            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("button_switch_lens")
                    ) {
                        Icon(
                            Icons.Default.Cameraswitch,
                            contentDescription = "Switch Camera",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Manual Flashlight / Torch Toggle Button for Low-Light Retail Environments
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.vibrateClick(context)
                            isTorchOn = !isTorchOn
                            camera?.cameraControl?.enableTorch(isTorchOn)
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isTorchOn) AgriHarvestGold else Color.White.copy(alpha = 0.15f))
                            .border(
                                width = if (isTorchOn) 2.dp else 1.dp,
                                color = if (isTorchOn) Color.White else Color.White.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .testTag("button_quick_torch_toggle")
                    ) {
                        Icon(
                            imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Flashlight",
                            tint = if (isTorchOn) AgriForestGreen else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Main Capture Shutter Button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(3.5.dp, Color.White, CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(if (isCapturing) Color.Gray else AgriEmerald)
                            .testTag("button_capture_label_photo"),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (isCapturing || isAnalyzing) return@IconButton
                                HapticFeedbackHelper.vibrateClick(context)
                                isCapturing = true
                                val capture = imageCapture
                                if (capture != null) {
                                    capture.takePicture(
                                        cameraExecutor,
                                        object : ImageCapture.OnImageCapturedCallback() {
                                            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                                val bitmap = imageProxyToBitmap(imageProxy)
                                                imageProxy.close()
                                                HapticFeedbackHelper.vibrateCaptureSuccess(context)
                                                scope.launch(Dispatchers.Main) {
                                                    isCapturing = false
                                                    if (bitmap != null) {
                                                        onPhotoCaptured(bitmap)
                                                    }
                                                }
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                Log.e("CameraScreen", "Capture failed", exception)
                                                scope.launch(Dispatchers.Main) {
                                                    isCapturing = false
                                                }
                                            }
                                        }
                                    )
                                } else {
                                    isCapturing = false
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (isCapturing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = "Capture Label",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    // Toggle Rule Guidance Checklist
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.vibrateClick(context)
                            showRuleAssistance = !showRuleAssistance
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("button_toggle_rule_help")
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = "Toggle Rule Guidance",
                            tint = if (showRuleAssistance) AgriHarvestGold else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        } else {
            // Accompanist Camera Permission Denied / Rationale View
            CameraPermissionDeniedScreen(
                shouldShowRationale = cameraPermissionState.status.shouldShowRationale,
                onRequestPermission = {
                    cameraPermissionState.launchPermissionRequest()
                },
                onOpenSettings = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                },
                onNavigateBack = onNavigateBack
            )
        }

        // Animated Gemini Multi-Stage AI Analysis Overlay
        AnimatedVisibility(
            visible = isAnalyzing,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GeminiAnalysisLoadingOverlay(stepMessage = analysisStep)
        }
    }
}

/**
 * Visual Viewfinder Overlay that paints a clear cutout window, alignment brackets,
 * scanning sweep line, crosshair ticks, and orientation guides.
 */
@Composable
private fun ViewfinderOverlayCanvas(
    modifier: Modifier = Modifier,
    isWideMode: Boolean = false
) {
    // Scanning Laser Vertical Animation
    val infiniteTransition = rememberInfiniteTransition(label = "viewfinder_laser")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.96f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    Canvas(modifier = modifier) {
        val canvasW = size.width
        val canvasH = size.height

        // Frame dimensions based on aspect mode
        val boxW = if (isWideMode) canvasW * 0.90f else canvasW * 0.84f
        val boxH = if (isWideMode) canvasH * 0.38f else canvasH * 0.48f
        val left = (canvasW - boxW) / 2f
        val top = (canvasH - boxH) / 2f - 35.dp.toPx()

        // 1. Semi-transparent backdrop mask
        drawRect(color = Color.Black.copy(alpha = 0.52f))

        // 2. Clear transparent cutout in the center
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(boxW, boxH),
            cornerRadius = CornerRadius(22.dp.toPx()),
            blendMode = BlendMode.Clear
        )

        // 3. Frame boundary with subtle emerald border
        drawRoundRect(
            color = Color(0xFF52B788).copy(alpha = 0.8f),
            topLeft = Offset(left, top),
            size = Size(boxW, boxH),
            cornerRadius = CornerRadius(22.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )

        // 4. Prominent golden corner brackets
        val cornerLen = 34.dp.toPx()
        val cornerW = 4.5.dp.toPx()
        val gold = Color(0xFFE9C46A)

        // Top-Left Bracket
        drawLine(gold, Offset(left, top), Offset(left + cornerLen, top), cornerW)
        drawLine(gold, Offset(left, top), Offset(left, top + cornerLen), cornerW)

        // Top-Right Bracket
        drawLine(gold, Offset(left + boxW, top), Offset(left + boxW - cornerLen, top), cornerW)
        drawLine(gold, Offset(left + boxW, top), Offset(left + boxW, top + cornerLen), cornerW)

        // Bottom-Left Bracket
        drawLine(gold, Offset(left, top + boxH), Offset(left + cornerLen, top + boxH), cornerW)
        drawLine(gold, Offset(left, top + boxH), Offset(left, top + boxH - cornerLen), cornerW)

        // Bottom-Right Bracket
        drawLine(gold, Offset(left + boxW, top + boxH), Offset(left + boxW - cornerLen, top + boxH), cornerW)
        drawLine(gold, Offset(left + boxW, top + boxH), Offset(left + boxW, top + boxH - cornerLen), cornerW)

        // 5. Crosshair ticks at midpoints for label horizontal/vertical alignment
        val tickLen = 14.dp.toPx()
        val tickColor = Color(0xFF52B788).copy(alpha = 0.6f)
        val tickW = 1.5.dp.toPx()

        // Top & Bottom midpoint ticks
        drawLine(tickColor, Offset(left + boxW / 2, top), Offset(left + boxW / 2, top + tickLen), tickW)
        drawLine(tickColor, Offset(left + boxW / 2, top + boxH), Offset(left + boxW / 2, top + boxH - tickLen), tickW)

        // Left & Right midpoint ticks
        drawLine(tickColor, Offset(left, top + boxH / 2), Offset(left + tickLen, top + boxH / 2), tickW)
        drawLine(tickColor, Offset(left + boxW, top + boxH / 2), Offset(left + boxW - tickLen, top + boxH / 2), tickW)

        // 6. Dynamic Scanning Laser Sweep
        val laserY = top + (boxH * laserYRatio)
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF52B788),
                    Color(0xFFE9C46A),
                    Color(0xFF52B788),
                    Color.Transparent
                ),
                startX = left,
                endX = left + boxW
            ),
            start = Offset(left + 8f, laserY),
            end = Offset(left + boxW - 8f, laserY),
            strokeWidth = 3.dp.toPx()
        )
    }
}

/**
 * Screen displayed when camera permissions have not yet been granted via Accompanist.
 */
@Composable
private fun CameraPermissionDeniedScreen(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriForestGreen)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = null,
                tint = AgriHarvestGold,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (shouldShowRationale) "Camera Access Required for Compliance Audits" else "Enable Camera to Scan Packages",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Mudra Check uses live camera vision to capture packaged commodities, seeds, and fertilizer labels to perform automated compliance audits under Legal Metrology (Packaged Commodities) Rules, 2011.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.88f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Statutory Declarations Checked:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AgriHarvestGold,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Rule 6(1)(a): Manufacturer / Packer / Importer Details\n• Rule 6(1)(c): Net Quantity in Standard SI Units\n• Rule 6(1)(da): MRP inclusive of all taxes\n• Rule 6(1)(e): Unit Sale Price (USP per g/ml)\n• Schedule II: Minimum font height verification",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("button_grant_camera_permission"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AgriHarvestGold,
                contentColor = AgriForestGreen
            )
        ) {
            Text("Grant Camera Access", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("button_open_camera_settings"),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open App Settings")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.18f),
                contentColor = Color.White
            )
        ) {
            Text("Back to Dashboard")
        }
    }
}

@Composable
private fun RuleCheckChip(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.68f),
        border = BorderStroke(0.8.dp, Color(0xFF52B788).copy(alpha = 0.75f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF52B788),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun GeminiAnalysisLoadingOverlay(stepMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.84f))
            .testTag("gemini_analysis_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, AgriEmerald.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(AgriEmerald.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AgriLeafGreen,
                        modifier = Modifier.size(46.dp),
                        strokeWidth = 3.5.dp
                    )
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AgriHarvestGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Gemini Legal Metrology Vision",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Packaged Commodities Rules, 2011 Compliance Audit",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AgriLeafGreen,
                        fontWeight = AppFontWeights.Subheader
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stepMessage,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Model: Gemini 3.5 Flash Multimodal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null) ?: return null

    val rotationDegrees = image.imageInfo.rotationDegrees
    return if (rotationDegrees != 0) {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}
