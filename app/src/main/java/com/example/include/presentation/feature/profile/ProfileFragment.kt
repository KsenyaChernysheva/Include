package com.example.include.presentation.feature.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.include.IncludeApp
import com.example.include.R
import com.example.include.data.podcast.Podcast
import com.example.include.data.user.User
import com.example.include.presentation.feature.login.LoginActivity
import com.example.include.presentation.feature.trackslist.TrackListFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    @Inject
    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    @ProvidePresenter
    fun initPresenter() = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        IncludeApp.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_history.setOnClickListener { presenter.openHistory() }
        btn_exit.setOnClickListener { presenter.exit() }
    }

    override fun openHistory() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.container, TrackListFragment.newInstance(Podcast("History","")))
            ?.commit()
    }

    override fun exit() {
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    override fun setUserInfo(user: User) {
        tv_name.text = NAME_FORMAT.format(user.first_name, user.last_name)
        tv_description.text = user.status
    }

    override fun setPic(url: String) {
        Picasso.get().load(url).into(profile_image);
    }

    companion object {
        private const val NAME_FORMAT = "%s %s"
    }
}