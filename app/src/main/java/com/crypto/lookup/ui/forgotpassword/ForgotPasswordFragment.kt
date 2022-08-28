package com.crypto.lookup.ui.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.crypto.lookup.R
import com.crypto.lookup.data.UserFirebaseDaoImpl
import com.crypto.lookup.data.UserService
import com.crypto.lookup.data.listeners.onGetNoDataListener
import com.crypto.lookup.databinding.FragmentForgotPasswordBinding


class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val db = UserService(UserFirebaseDaoImpl())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ForgotPasswordSendButton.setOnClickListener {
            if (binding.ForgotPasswordEmail.text.isNotEmpty()) {
                db.resetPassword(binding.ForgotPasswordEmail.text.toString(), object : onGetNoDataListener {
                    override fun onSuccess() {
                        binding.ForgotPasswordEmail.setText("")
                        Toast.makeText(context, getString(R.string.resetPasswordSuccess), Toast.LENGTH_LONG).show()
                    }

                    override fun onFailed(e: Exception) {
                        Toast.makeText(context, getString(R.string.errorResetPassword), Toast.LENGTH_LONG).show()
                    }

                })
            } else {
                Toast.makeText(context, getString(R.string.errorResetPassword), Toast.LENGTH_LONG).show()
            }
        }
    }
}