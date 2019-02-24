package com.tomergoldst.gepp.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import com.tomergoldst.gepp.R
import com.tomergoldst.gepp.utils.InjectorUtils
import kotlinx.android.synthetic.main.activity_kickoff.*
import com.tomergoldst.gepp.model.Status
import java.lang.RuntimeException

class KickOffActivity : AppCompatActivity() {

    companion object {
        private val TAG = KickOffActivity::class.java.simpleName

    }

    private lateinit var mModel: KickOffViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.tomergoldst.gepp.R.layout.activity_kickoff)

        mModel = ViewModelProviders.of(this, InjectorUtils.getKickOffViewModelProvider(application))
            .get(KickOffViewModel::class.java)

        mModel.getStatus().observe(this, Observer {
            onStatusChanged(it)
        })

        startBtn.setOnClickListener {
            // Check for location runtime permission
            Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : BasePermissionListener() {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        mModel.start()
                    }
                })
                .check()

        }
    }

    private fun onStatusChanged(status: Status){
        when (status){
            Status.NO_INTERNET_CONNECTION -> {
                val snackbar = Snackbar.make(rootLayout, getString(R.string.error_no_internet_connection), Snackbar.LENGTH_LONG)
                snackbar.setAction(getString(R.string.action_enable)) {
                    Toast.makeText(this, "Not implemented yet, Please turn it on", Toast.LENGTH_SHORT).show()
                }
                snackbar.show()
            }
            Status.NO_GEO_API_KEY -> {
                Toast.makeText(
                    this,
                    "Replace 'geo_api_key' string with your Api key",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            Status.START -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else -> throw RuntimeException("Unknown status")
        }
    }
}
