package com.crypto.lookup.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crypto.lookup.MainActivity
import com.crypto.lookup.R
import com.crypto.lookup.data.User
import com.crypto.lookup.data.UserFirebaseDaoImpl
import com.crypto.lookup.data.UserService
import com.crypto.lookup.data.listeners.onGetDataListener
import com.crypto.lookup.databinding.FragmentLoginBinding
import com.crypto.lookup.utils.Validation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedViewModel: UserViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val db = UserService(UserFirebaseDaoImpl())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        val root: View = binding.root
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        // TODO TEST DATA REMOVE THEM
//        binding.loginEmail.setText("test@test.com")
//        binding.loginPassword.setText("123456")

        binding.loginEmail.addTextChangedListener {
            if (Validation.isEmailValid(it.toString()) || it.toString().isEmpty()) {
                binding.loginEmailTil.isErrorEnabled = false
            } else {
                binding.loginEmailTil.error = getString(R.string.email_valid)
            }

        }
        binding.loginPassword.addTextChangedListener {
            if (Validation.isTextValid(it.toString(), 10, 6) || it.toString().isEmpty()) {
                binding.loginPasswordTil.isErrorEnabled = false
            } else {
                binding.loginPasswordTil.error = getString(R.string.password_valid)
            }
        }

        binding.LoginLoginButton.setOnClickListener {
            if (!binding.loginEmailTil.isErrorEnabled || binding.loginPasswordTil.isErrorEnabled) {
                binding.LoginLoginButton.isEnabled = false
                binding.LoginLoginButton.visibility = View.INVISIBLE
                binding.loginButtonPB.visibility = View.VISIBLE
                Login()
            }
        }


        binding.LoginRegisterButton.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.LoginForgotPassword.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    private fun Login() {
        db.retrieve(binding.loginEmail.text.toString(), object : onGetDataListener {
            override fun onSuccess(data: DocumentSnapshot) {
                val user = data.toObject(User::class.java)!!
                Firebase.auth.signInWithEmailAndPassword(
                    binding.loginEmail.text.toString(),
                    binding.loginPassword.text.toString()
                )

                binding.loginFail.visibility = View.INVISIBLE
                binding.LoginLoginButton.isEnabled = false
                binding.LoginLoginButton.visibility = View.INVISIBLE
                binding.loginButtonPB.visibility = View.VISIBLE

                val intent = Intent(activity, MainActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("user", user)
                intent.putExtras(bundle)
                startActivity(intent)
                activity!!.finish()
            }

            override fun onFailed(e: Exception) {
                binding.LoginLoginButton.isEnabled = true
                binding.loginFail.visibility = View.VISIBLE
                binding.LoginLoginButton.visibility = View.VISIBLE
                binding.loginButtonPB.visibility = View.INVISIBLE

            }
        })
    }

}