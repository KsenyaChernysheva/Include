package com.example.include.presentation.feature.login

import android.util.Log
import androidx.constraintlayout.widget.Constraints.TAG
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.include.data.user.UserResponse
import com.example.include.domain.user.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.gson.Gson
import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import javax.inject.Inject

@InjectViewState
class LoginActivityPresenter
@Inject constructor(
    private val model: UserModel,
    private val auth: FirebaseAuth
) : MvpPresenter<LoginView>() {

    override fun onFirstViewAttach() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewState.disableLoginBtn()
            model.loadUserPic()
            viewState.navigateToMainActivity()
        }
    }

    fun onLoginClick() {
        viewState.disableLoginBtn()
        viewState.showLoginVK()
    }

    fun onSuccessAuth() {
        val vkRequest = VKRequest("account.getProfileInfo")
        vkRequest.executeWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) {
                super.onComplete(response)
                val user = Gson().fromJson(response?.responseString, UserResponse::class.java)
                model.curUser = user.response
                loginFirebase()
            }

            override fun onError(error: VKError?) {
                viewState.enableLoginBtn()
                viewState.showError(error?.errorMessage ?: "Something went wrong")
            }
        })
    }

    fun onAuthError(error: VKError) {
        viewState.enableLoginBtn()
        error.errorMessage?.let {
            viewState.showError(it)
        }
    }

    private fun loginFirebase() {
        var login = model.curUser?.screen_name ?: ""
        val password = "${model.curUser?.last_name}${model.curUser?.first_name}"
        if (login.isNotEmpty()) {
            login = "${login}@qq.qq"
            auth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        model.loadUserPic()
                        viewState.navigateToMainActivity()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        if ((task.exception as? FirebaseAuthInvalidUserException)?.errorCode == "ERROR_USER_NOT_FOUND") {
                            viewState.showError("No such user in app, registering...")
                            registerNewUser(login, password)
                        } else {
                            viewState.enableLoginBtn()
                            viewState.showError(task.exception?.message.toString())
                        }
                    }
                }
        } else {
            viewState.showError("You must set shortId in Vk")
            viewState.enableLoginBtn()
        }

    }

    private fun registerNewUser(login: String, password: String) {
        auth.createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener { task2 ->
                if (task2.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    model.loadUserPic()
                    viewState.navigateToMainActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task2.exception)
                    viewState.showError(task2.exception?.message.toString())
                    viewState.enableLoginBtn()
                }
            }
    }

}
