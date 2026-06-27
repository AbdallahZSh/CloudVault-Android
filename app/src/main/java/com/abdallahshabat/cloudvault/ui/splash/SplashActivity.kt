package com.abdallahshabat.cloudvault.ui.splash

import android.R
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.abdallahshabat.cloudvault.core.managers.TokenManager
import com.abdallahshabat.cloudvault.ui.auth.LoginActivity
import com.abdallahshabat.cloudvault.ui.home.MainActivity
import com.example.cloudapp.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()

        lifecycleScope.launch {
            delay(2800)
            navigateNext()
        }
    }

    private fun startAnimations() {
        binding.ivLogo.alpha = 0f
        binding.ivLogo.scaleX = 0.3f
        binding.ivLogo.scaleY = 0.3f


        val logoAlpha = ObjectAnimator.ofFloat(binding.ivLogo, "alpha", 0f, 1f).apply {
            duration  = 600
        }
        val logoScaleX = ObjectAnimator.ofFloat(binding.ivLogo, "scaleX", 0.3f, 1f).apply {
            duration  = 700
            interpolator = OvershootInterpolator(1.5f)
        }
        val logoScaleY = ObjectAnimator.ofFloat(binding.ivLogo, "scaleY", 0.3f, 1f).apply {
            duration  = 700
            interpolator = OvershootInterpolator(1.5f)
        }

        binding.tvAppName.alpha = 0f
        binding.tvAppName.translationY = 40f

        val nameAlpha = ObjectAnimator.ofFloat(binding.tvAppName, "alpha", 0f, 1f).apply {
            duration  = 500
            startDelay = 500
        }
        val nameTranslate = ObjectAnimator.ofFloat(binding.tvAppName, "translationY", 40f, 0f).apply {
            duration  = 500
            startDelay = 500
            interpolator = AccelerateDecelerateInterpolator()
        }

        binding.tvTagline.alpha = 0f
        binding.tvTagline.translationY = 30f

        val tagAlpha = ObjectAnimator.ofFloat(binding.tvTagline, "alpha", 0f, 1f).apply {
            duration  = 500
            startDelay = 750
        }
        val tagTranslate = ObjectAnimator.ofFloat(binding.tvTagline, "translationY", 30f, 0f).apply {
            duration  = 500
            startDelay = 750
            interpolator = AccelerateDecelerateInterpolator()
        }

        binding.dotsContainer.alpha = 0f
        val dotsAlpha = ObjectAnimator.ofFloat(binding.dotsContainer, "alpha", 0f, 1f).apply {
            duration  = 400
            startDelay = 1100
        }

        AnimatorSet().apply {
            playTogether(
                logoAlpha, logoScaleX, logoScaleY,
                nameAlpha, nameTranslate,
                tagAlpha, tagTranslate,
                dotsAlpha
            )
            start()
        }

        lifecycleScope.launch {
            delay(1200)
            animateDots()
        }
    }

    private suspend fun animateDots() {
        val dots = listOf(binding.dot1, binding.dot2, binding.dot3)
        var active = 1

        repeat(4) {
            dots.forEachIndexed { index, dot ->
                val isActive = index == active
                dot.animate()
                    .scaleX(if (isActive) 1.0f else 0.6f)
                    .scaleY(if (isActive) 1.0f else 0.6f)
                    .alpha(if (isActive) 1f else 0.3f)
                    .setDuration(200)
                    .start()
            }
            active = (active + 1) % 3
            delay(400)
        }
    }

    private fun navigateNext() {
        val hasToken = TokenManager.hasToken(this)
        val intent = if (hasToken) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}