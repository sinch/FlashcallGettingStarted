package com.sinch.rtc.flashcall.gettingstarted

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sinch.rtc.flashcall.gettingstarted.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 5
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.initButton.setOnClickListener {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_CALL_LOG
                ), PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //We simply proceed with the verification
        VerificationDialog.newInstance(binding.phoneInputEditText.text.toString())
            .apply {
                show(supportFragmentManager, "dialog")
            }
    }

}