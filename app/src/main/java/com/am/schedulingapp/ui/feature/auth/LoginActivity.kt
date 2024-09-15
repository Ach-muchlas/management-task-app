package com.am.schedulingapp.ui.feature.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.am.schedulingapp.R
import com.am.schedulingapp.databinding.ActivityLoginBinding
import com.am.schedulingapp.service.source.Status
import com.am.schedulingapp.ui.feature.main.MainActivity
import com.am.schedulingapp.utils.Extension.goToActivity
import com.am.schedulingapp.utils.NotificationHandling.showErrorMessage
import com.am.schedulingapp.utils.ProgressHandling.showProgressbar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by inject()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setupNavigation()
    }

    private fun init() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                showErrorMessage(binding.root, e.message.toString())
                Log.e(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.signInWithGoogle(idToken).observe(this) { result ->
            when (result.status) {
                Status.LOADING -> {
                    showProgressbar(binding.progressBar, true)
                }

                Status.SUCCESS -> {
                    showProgressbar(binding.progressBar, false)
                    goToActivity(MainActivity::class.java, true)
                }

                Status.ERROR -> {
                    showProgressbar(binding.progressBar, false)
                    showErrorMessage(binding.root, result.message.toString())
                }
            }
        }
    }

    private fun setupSigInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun setupNavigation() {
        binding.btnLogin.setOnClickListener { setupLogin() }
        binding.btnLoginWithGmail.setOnClickListener { setupSigInWithGoogle() }
        binding.txtRegister.setOnClickListener { goToActivity(RegisterActivity::class.java) }
    }

    private fun setupLogin() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        viewModel.signInUser(email, password).observe(this) { result ->
            when (result.status) {
                Status.LOADING -> {
                    showProgressbar(binding.progressBar, true)
                }

                Status.SUCCESS -> {
                    showProgressbar(binding.progressBar, false)
                    goToActivity(MainActivity::class.java, true)
                }

                Status.ERROR -> {
                    showProgressbar(binding.progressBar, false)
                    showErrorMessage(binding.root, result.message.toString())
                }
            }
        }
    }

    companion object {
        private const val TAG = "SignInFragment"
        private const val RC_SIGN_IN = 9001
    }
}