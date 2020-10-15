package com.crtmg.bletime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), MainActivityCallback {

    private val viewModel = CentralManager.model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNowAllowingStateLoss()
        }
    }


    override fun getViewModel(): PeripheralListModel {
        return viewModel
    }

}
