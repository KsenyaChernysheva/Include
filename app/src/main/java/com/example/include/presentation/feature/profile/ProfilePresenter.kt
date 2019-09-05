package com.example.include.presentation.feature.profile

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.include.domain.user.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.vk.sdk.VKSdk
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InjectViewState
class ProfilePresenter
    @Inject constructor(val model: UserModel): MvpPresenter<ProfileView>() {

    override fun onFirstViewAttach() {
        model.curUser?.let { viewState.setUserInfo(it) }
        model.curUserPic?.let { viewState.setPic(it) }
    }

    fun openHistory() {
        viewState.openHistory()
    }

    fun exit() {
        FirebaseAuth.getInstance().signOut()
        VKSdk.logout()
        viewState.exit()
    }
}