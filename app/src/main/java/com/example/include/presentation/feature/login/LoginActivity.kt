package com.example.include.presentation.feature.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.include.IncludeApp
import com.example.include.presentation.feature.mainplayer.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class LoginActivity : MvpAppCompatActivity(), LoginView {

    @Inject
    @InjectPresenter
    lateinit var loginActivityPresenter: LoginActivityPresenter

    private lateinit var auth: FirebaseAuth

    @ProvidePresenter
    fun initPresenter() = loginActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        IncludeApp.appComponent.loginComponent().build().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(com.example.include.R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null)
            enterApp()
        btn_login.setOnClickListener { loginActivityPresenter.login() }
    }

    override fun enterApp() {
        loginActivityPresenter.setPic()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun loginVK() {
        VKSdk.login(this, VKScope.PHOTOS)
        btn_login.isClickable = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
                override fun onResult(res: VKAccessToken) {
                    Toast.makeText(applicationContext, "Success logging VK", Toast.LENGTH_LONG).show()
                    loginActivityPresenter.setUser(auth)
                }

                override fun onError(error: VKError) {
                    showError(error.errorMessage, true)
                }
            })) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showError(e: String, b: Boolean) {
        Toast.makeText(applicationContext, e, Toast.LENGTH_LONG).show()
        btn_login.isClickable = b
    }
}
