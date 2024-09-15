package com.am.schedulingapp.ui.feature.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.am.schedulingapp.databinding.ActivityRegisterBinding
import com.am.schedulingapp.service.source.Status
import com.am.schedulingapp.ui.feature.main.MainActivity
import com.am.schedulingapp.utils.Extension.goToActivity
import com.am.schedulingapp.utils.NotificationHandling.showErrorMessage
import com.am.schedulingapp.utils.ProgressHandling.showProgressbar
import org.koin.android.ext.android.inject

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.btnRegister.setOnClickListener { setupRegister() }
        binding.btnLoginWithGmail.setOnClickListener { }
        binding.txtLogin.setOnClickListener { goToActivity(LoginActivity::class.java) }
    }

    private fun setupRegister() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        viewModel.signUpUser(name, email, password).observe(this) { result ->
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
}