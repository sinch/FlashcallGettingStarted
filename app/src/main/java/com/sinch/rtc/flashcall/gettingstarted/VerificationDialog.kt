package com.sinch.rtc.flashcall.gettingstarted

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.sinch.rtc.flashcall.gettingstarted.databinding.DialogVerificationBinding
import com.sinch.verification.core.auth.AppKeyAuthorizationMethod
import com.sinch.verification.core.auth.AuthorizationMethod
import com.sinch.verification.core.auth.BasicAuthorizationMethod
import com.sinch.verification.core.config.general.SinchGlobalConfig
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.verification.VerificationEvent
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.flashcall.FlashCallVerificationMethod
import com.sinch.verification.flashcall.config.FlashCallVerificationConfig
import com.sinch.verification.flashcall.initialization.FlashCallInitializationListener
import com.sinch.verification.flashcall.initialization.FlashCallInitializationResponseData
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*

class VerificationDialog : DialogFragment(), VerificationListener {

    companion object {
        private const val APP_KEY = "ef032cd1-8a5e-43c8-bef4-b42bb8d99791"
        private const val APP_SECRET = "YNTTH6fxE0yNbUzLrMfXLg=="
        private const val PHONE_NUMBER_TAG = "phone_number"
        fun newInstance(phoneNumber: String) = VerificationDialog().apply {
            arguments = Bundle().apply { putString(PHONE_NUMBER_TAG, phoneNumber) }
        }
    }

    private var _binding: DialogVerificationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val initListener = object : FlashCallInitializationListener {
        override fun onInitializationFailed(t: Throwable) {
            showErrorWithMessage(t.message.orEmpty())
        }

        override fun onInitiated(data: FlashCallInitializationResponseData) {}
    }

    private lateinit var verification: Verification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Normally we would implement all the logic inside View Model but for simplicity we will keep it here.
        val globalConfig = SinchGlobalConfig.Builder.instance.applicationContext(
            requireContext().applicationContext
        )
            .authorizationMethod(BasicAuthorizationMethod(APP_KEY, APP_SECRET))
            .interceptors(listOf(HttpLoggingInterceptor().apply {
                setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            }))
            .build()

        verification = FlashCallVerificationMethod.Builder().config(
            FlashCallVerificationConfig.Builder().globalConfig(globalConfig)
                .number(arguments?.getString(PHONE_NUMBER_TAG).orEmpty())
                .build()
        )
            .initializationListener(initListener)
            .verificationListener(this)
            .build()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            isCancelable = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.verifyButton.setOnClickListener {
            verification.verify(binding.codeInput.editText?.text.toString())
        }
        binding.quitButton.setOnClickListener {
            verification.stop()
            dismiss()
        }
        verification.initiate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onVerified() {
        binding.progressBar.hide()
        binding.messageText.apply {
            setTextColor(context.getColor(R.color.green))
            text = getString(R.string.successfullyVerified)
            binding.quitButton.text = getString(R.string.close)
            binding.codeInput.visibility = View.GONE
            binding.verifyButton.visibility = View.GONE
        }
    }

    override fun onVerificationFailed(t: Throwable) {
        showErrorWithMessage(t.message.orEmpty())
    }

    override fun onVerificationEvent(event: VerificationEvent) {}

    private fun showErrorWithMessage(text: String) {
        binding.progressBar.hide()
        binding.messageText.apply {
            setTextColor(context.getColor(R.color.red))
            this.text =
                String.format(Locale.US, getString(R.string.verificationFailedPlaceholder), text)
        }
    }

}