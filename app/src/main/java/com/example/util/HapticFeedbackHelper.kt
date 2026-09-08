package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Utility helper for managing tactile and haptic feedback across the app,
 * specifically for scanning, photo capture, and AI compliance audit completion.
 */
object HapticFeedbackHelper {

    /**
     * Trigger a crisp haptic feedback pulse when an image/label is successfully captured by the camera.
     */
    fun vibrateCaptureSuccess(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Predefined click or short snappy pulse
                val effect = try {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                } catch (e: Exception) {
                    VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(45)
            }
        } catch (e: Exception) {
            // Gracefully ignore if vibrator is unavailable or restricted
        }
    }

    /**
     * Trigger a distinct double-pulse haptic pattern when Gemini AI finishes processing
     * and completing a compliance inspection or grounded search request.
     */
    fun vibrateAiProcessingComplete(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Success double-pulse pattern: [delay, pulse1, rest, pulse2]
                val timings = longArrayOf(0, 50, 70, 90)
                val amplitudes = intArrayOf(0, 180, 0, 240)
                val effect = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        try {
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                        } catch (e: Exception) {
                            VibrationEffect.createWaveform(timings, amplitudes, -1)
                        }
                    } else {
                        VibrationEffect.createWaveform(timings, amplitudes, -1)
                    }
                } catch (e: Exception) {
                    VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 50, 70, 90), -1)
            }
        } catch (e: Exception) {
            // Gracefully ignore if vibrator is unavailable
        }
    }

    /**
     * Light click haptic for UI interactions (e.g. shutter button press).
     */
    fun vibrateClick(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = try {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                } catch (e: Exception) {
                    VibrationEffect.createOneShot(25, 100)
                }
                vibrator.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(25, 100))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }
}
