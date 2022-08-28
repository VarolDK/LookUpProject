package com.crypto.lookup.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.crypto.lookup.EntryActivity
import com.crypto.lookup.R
import com.crypto.lookup.data.UserFirebaseDaoImpl
import com.crypto.lookup.data.UserService
import com.crypto.lookup.data.listeners.onSaveDataListener
import com.crypto.lookup.databinding.FragmentProfileBinding
import com.crypto.lookup.ui.login.UserViewModel
import com.crypto.lookup.utils.Validation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var sharedViewModel: UserViewModel
    private var _binding: FragmentProfileBinding? = null
    val userService: UserService = UserService(UserFirebaseDaoImpl())


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = sharedViewModel.getCurrentUser()
        binding.profileEmail.text = currentUser.email
        binding.profileBirthdate.text = SimpleDateFormat("dd/MM/yyy").format(currentUser.birthDate)
        binding.profileIdentity.text = currentUser.identityNumber.toString()
        binding.profilePhone.text = currentUser.phoneNumber.toString()
        binding.profileNameSur.text =
            currentUser.name.replaceFirstChar { it.uppercase() } + " " + currentUser.surname.uppercase()

        textInputEmailCheck()
        textInputPasswordCheck()
        buttonLogout()
        buttonEmailUpdate()
        buttonPasswordUpdate()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.w("TOKEN", it)
        }
    }

    fun textInputEmailCheck() {
        binding.profileUpdateEmail.addTextChangedListener {
            if (Validation.isEmailValid(it.toString()) || it.toString().isEmpty()) {
                binding.profileEmailTil.isErrorEnabled = false
            } else {
                binding.profileEmailTil.error = getString(R.string.email_valid)
            }
        }
    }

    fun textInputPasswordCheck() {
        binding.profilePassword.addTextChangedListener {
            if (Validation.isTextValid(it.toString(), 10, 6) || it.toString().isEmpty()) {
                binding.profilePasswordTil.isErrorEnabled = false
            } else {
                binding.profilePasswordTil.error = getString(R.string.password_valid)
            }
        }

        binding.profilePasswordAgain.addTextChangedListener {
            val passwordAgainValid = Validation.isTextValid(it.toString(), 10, 6) || it.toString().isEmpty()
            val passwordEqual = binding.profilePassword.text.toString() == it.toString() || it.toString().isEmpty()
            if (!passwordAgainValid) binding.profilePasswordAgainTil.error = getString(R.string.password_valid)
            if (!passwordEqual) binding.profilePasswordAgainTil.error = getString(R.string.password_equal)
            if (passwordAgainValid && passwordEqual) binding.profilePasswordAgainTil.isErrorEnabled = false
        }
    }

    fun buttonEmailUpdate() {
        binding.profileEmailApply.setOnClickListener {
            binding.profileEmailApply.visibility = View.INVISIBLE
            emailUpdate()
        }
    }

    private fun emailUpdate() {
        if (!binding.profileEmailTil.isErrorEnabled) {
            userService.updateEmail(
                binding.profileUpdateEmail.text.toString(),
                sharedViewModel.getCurrentUser(),
                object : onSaveDataListener {
                    override fun onSuccess() {
                        logout()
                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(context, getString(R.string.updateEmailError), Toast.LENGTH_SHORT).show()
                        binding.profileEmailApply.visibility = View.VISIBLE
                    }

                })
        }
    }

    fun buttonLogout() {
        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun buttonPasswordUpdate() {
        //TODO hicbirsey yazilmazsa kontolrunu ekle
        binding.profilePasswordApply.setOnClickListener {
            if (!(binding.profileOldPasswordTil.isErrorEnabled && binding.profilePasswordTil.isErrorEnabled && binding.profilePasswordAgainTil.isErrorEnabled)) {
                userService.checkPassword(binding.profileOldPassword.text.toString(), object : onSaveDataListener {
                    override fun onSuccess() {
                        binding.profilePasswordApply.visibility = View.INVISIBLE
                        passwordUpdate()
                        binding.profileOldPasswordTil.isErrorEnabled = false
                    }

                    override fun onFailed(exception: Exception) {
                        binding.profileOldPasswordTil.error = getString(R.string.dontMatch)
                    }
                })
            }

        }
    }


    private fun passwordUpdate() {
        if (!(binding.profilePasswordTil.isErrorEnabled && binding.profilePasswordAgainTil.isErrorEnabled)) {
            userService.updatePassword(binding.profilePassword.text.toString(), object : onSaveDataListener {
                override fun onSuccess() {
//                    logout()
                    Toast.makeText(context, getString(R.string.update_success), Toast.LENGTH_LONG).show()
                    binding.profilePassword.setText("")
                    binding.profilePasswordAgain.setText("")
                    binding.profileOldPassword.setText("")

                    binding.profilePasswordTil.isErrorEnabled = false
                    binding.profilePasswordAgainTil.isErrorEnabled = false
                    binding.profileOldPasswordTil.isErrorEnabled = false


                }

                override fun onFailed(exception: Exception) {
                    Toast.makeText(context, getString(R.string.updatePasswordError), Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun logout() {
        Firebase.auth.signOut()
        val intent = Intent(activity, EntryActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}