package com.crtmg.bletime

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

class PermissionFragment : Fragment() {

    companion object {
        fun newInstance() = PermissionFragment()
    }

    private lateinit var permissionsRequester: PermissionsRequester

    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionsRequester = constructPermissionsRequest(Manifest.permission.CAMERA,
            onShowRationale = ::onCameraShowRationale,
            onPermissionDenied = ::onCameraDenied,
            onNeverAskAgain = ::onCameraNeverAskAgain) {
            childFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .addToBackStack("camera")
                .commitAllowingStateLoss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val buttonCamera: Button = view.findViewById(R.id.button_camera)
//        buttonCamera.setOnClickListener {
//            permissionsRequester.launch()
//        }
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