package com.crtmg.bletime

import android.Manifest
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.crtmg.bletime.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest
import java.util.*

class MainFragment : Fragment(), Observer {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var permissionsRequester: PermissionsRequester

    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionsRequester = constructPermissionsRequest(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            onShowRationale = ::onCameraShowRationale,
            onPermissionDenied = ::onCameraDenied,
            onNeverAskAgain = ::onCameraNeverAskAgain
        ) {
            Toast.makeText(context, "Scan started", Toast.LENGTH_SHORT).show()
            refresh.isRefreshing = false
            viewModel.clear()
            CentralManager.initBle(activity!!.application) // potential risk
            CentralManager.startScan()
        }
    }


    private val delegate: MainActivityCallback?
        get() {
            return this.context as? MainActivityCallback
        }

    private val viewModel: PeripheralListModel = CentralManager.model

    private lateinit var mFragmentRootView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )
        binding.viewModel = viewModel
        mFragmentRootView = binding.root

        viewModel.addObserver(this)

        val adapter = PeripheralListAdapter()
        mFragmentRootView.rvPeripherals.layoutManager = LinearLayoutManager(context)
        mFragmentRootView.rvPeripherals.adapter = adapter

        mFragmentRootView.refresh.setOnRefreshListener {
            permissionsRequester.launch()
        }

        return mFragmentRootView
    }

    override fun update(o: Observable?, arg: Any?) {

    }


    private fun onCameraDenied() {
        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    private fun onCameraShowRationale(request: PermissionRequest) {
        request.proceed()
    }

    private fun onCameraNeverAskAgain() {
        Toast.makeText(context, "Permission Strictly Denied", Toast.LENGTH_SHORT).show()
    }

}