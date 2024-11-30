package com.capstone.nutrisee.login

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nutrisee.R
import com.capstone.nutrisee.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_loop)
        binding.ivIllustration.startAnimation(bounceAnimation)

        val spannableText = SpannableString("Already Have An Account? Log In")
        val loginClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@OnboardingActivity, LoginActivity::class.java))
            }
        }

        spannableText.setSpan(loginClick, 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLogin.text = spannableText
        binding.tvLogin.movementMethod = LinkMovementMethod.getInstance()

        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}
