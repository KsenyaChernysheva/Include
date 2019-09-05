package com.example.include.presentation.di.component

import com.example.include.presentation.di.module.FirebaseModule
import com.example.include.presentation.di.module.MainModule
import com.example.include.presentation.di.module.UserModule
import com.example.include.presentation.feature.mainplayer.MainActivity
import com.example.include.presentation.feature.podcastslist.PodcastsFragment
import com.example.include.presentation.feature.profile.ProfileFragment
import com.example.include.presentation.feature.trackslist.TrackListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UserModule::class, MainModule::class,FirebaseModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(trackListFragment: TrackListFragment)

    fun inject(profileFragment: ProfileFragment)

    fun inject(podcastsFragment: PodcastsFragment)

    fun loginComponent(): LoginComponent.Builder
}
