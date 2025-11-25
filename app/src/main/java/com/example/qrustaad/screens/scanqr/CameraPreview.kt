package com.example.qrustaad.screens.scanqr

import android.os.Handler
import android.os.Looper
import android.view.ScaleGestureDetector
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import com.example.qrustaad.utils.processImageProxy
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onQRCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current

    var scannedOnce by remember { mutableStateOf(false) }

    // Camera control reference (comes from AndroidView)
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    // Zoom animation states
    var targetZoom by remember { mutableStateOf(1f) }
    val animatedZoom by animateFloatAsState(
        targetValue = targetZoom,
        animationSpec = tween(600),
        label = "zoomAnimation"
    )

    // Animate zoom safely in Compose
    LaunchedEffect(animatedZoom) {
        cameraControl?.setZoomRatio(animatedZoom)
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val executor = Executors.newSingleThreadExecutor()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(executor) { proxy ->
                    if (!scannedOnce) {
                        processImageProxy(proxy) { result ->
                            scannedOnce = true

                            // animate zoom-in
                            targetZoom = 2.5f

                            Handler(Looper.getMainLooper()).postDelayed({
                                targetZoom = 1f   // smooth zoom-out
                            }, 1500)

                            onQRCodeScanned(result)
                        }
                    }
                    proxy.close()
                }

                cameraProvider.unbindAll()

                val camera = cameraProvider.bindToLifecycle(
                    lifecycle,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )

                // store cameraControl for Compose to use
                cameraControl = camera.cameraControl

                // Center auto-focus
                previewView.post {
                    val factory = SurfaceOrientedMeteringPointFactory(
                        previewView.width.toFloat(),
                        previewView.height.toFloat()
                    )
                    val point = factory.createPoint(
                        previewView.width / 2f,
                        previewView.height / 2f
                    )

                    camera.cameraControl.startFocusAndMetering(
                        FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                            .setAutoCancelDuration(3, TimeUnit.SECONDS)
                            .build()
                    )
                }

                // Pinch zoom gesture
                val zoomGesture = ScaleGestureDetector(
                    ctx,
                    object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        override fun onScale(detector: ScaleGestureDetector): Boolean {
                            val currentZoom =
                                camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                            camera.cameraControl.setZoomRatio(currentZoom * detector.scaleFactor)
                            return true
                        }
                    })

                previewView.setOnTouchListener { _, event ->
                    zoomGesture.onTouchEvent(event)
                    true
                }

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}


