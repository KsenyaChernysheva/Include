package com.example.include.presentation.feature.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.include.IncludeApp
import com.example.include.R
import com.example.include.presentation.feature.mainplayer.MainActivity
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

    @ProvidePresenter
    fun initPresenter() = loginActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        IncludeApp.appComponent.loginComponent().build().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener { loginActivityPresenter.onLoginClick() }
    }

    override fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun showLoginVK() {
        VKSdk.login(this, VKScope.PHOTOS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
                override fun onResult(res: VKAccessToken) {
                    Toast.makeText(applicationContext, "Success logging VK", Toast.LENGTH_LONG).show()
                    loginActivityPresenter.onSuccessAuth()
                }

                override fun onError(error: VKError) = loginActivityPresenter.onAuthError(error)
            })) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showError(e: String) {
        Toast.makeText(applicationContext, e, Toast.LENGTH_LONG).show()
    }

    override fun enableLoginBtn() {
        btn_login.isEnabled = true
    }

    override fun disableLoginBtn() {
        btn_login.isEnabled = false
    }
}
