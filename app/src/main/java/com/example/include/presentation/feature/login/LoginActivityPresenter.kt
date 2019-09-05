package com.example.include.presentation.feature.login

import android.util.Log
import androidx.constraintlayout.widget.Constraints.TAG
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.include.domain.user.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.example.include.data.user.UserResponse
import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import javax.inject.Inject

@InjectViewState
class LoginActivityPresenter
@Inject constructor(val model: UserModel) : MvpPresenter<LoginView>() {

    fun login() = viewState.loginVK()

    fun setUser(auth: FirebaseAuth) {
        val vkRequest = VKRequest("account.getProfileInfo")
        vkRequest.executeWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) {
                super.onComplete(response)
                val user = Gson().fromJson(response?.responseString, UserResponse::class.java)
                model.curUser = user.response
                loginFirebase(auth)
            }
            override fun onError(error: VKError?) {
                viewState.showError(error?.errorMessage.toString(),true)
            }
        }
        )
    }

    fun setPic(){
        model.setUserPic()
    }

    fun loginFirebase(auth: FirebaseAuth) {
        var login = model.curUser?.screen_name.toString()
        val password = "${model.curUser?.last_name}${model.curUser?.first_name}"
        if (!login.equals("")) {
            login = "${login}@qq.qq"
            auth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        viewState.enterApp()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        if (task.exception?.message.equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {

                            viewState.showError("No such user in app, registering...", false)

                            auth.createUserWithEmailAndPassword(login, password)
                                .addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success")
                                        val user = auth.currentUser
                                        viewState.enterApp()
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task2.exception)
                                        viewState.showError(task2.exception?.message.toString(), true)
                                    }
                                }
                        } else
                            viewState.showError(task.exception?.message.toString(), true)
                    }
                }
        } else
            viewState.showError("you must set shortId in Vk", true)

    }

}
