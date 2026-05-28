package com.example.govservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.govservice.fragments.CreateApplicationFragment
import com.example.govservice.fragments.EmployeeApplicationsFragment
import com.example.govservice.fragments.LoginFragment
import com.example.govservice.fragments.MyApplicationsFragment
import com.example.govservice.fragments.ProfileFragment
import com.example.govservice.fragments.RegisterFragment
import com.example.govservice.fragments.ServicesFragment
import com.example.govservice.util.TokenManager

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            openStartScreen()
        }
    }

    fun openStartScreen() {
        if (!tokenManager.isLoggedIn()) {
            openLoginScreen()
            return
        }

        if (tokenManager.getUserRole() == "EMPLOYEE") {
            openEmployeeApplicationsScreen()
        } else {
            openServicesScreen()
        }
    }

    fun openLoginScreen() {
        supportFragmentManager.popBackStack()
        replaceFragment(LoginFragment(), false)
    }

    fun openRegisterScreen() {
        replaceFragment(RegisterFragment(), true)
    }

    fun openServicesScreen() {
        supportFragmentManager.popBackStack()
        replaceFragment(ServicesFragment(), false)
    }

    fun openEmployeeApplicationsScreen() {
        supportFragmentManager.popBackStack()
        replaceFragment(EmployeeApplicationsFragment(), false)
    }

    fun openCreateApplicationScreen(serviceId: Int, serviceTitle: String) {
        val fragment = CreateApplicationFragment.newInstance(
            serviceId = serviceId,
            serviceTitle = serviceTitle
        )

        replaceFragment(fragment, true)
    }

    fun openMyApplicationsScreen() {
        replaceFragment(MyApplicationsFragment(), true)
    }

    fun openProfileScreen() {
        replaceFragment(ProfileFragment(), true)
    }

    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragmentContainer, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}