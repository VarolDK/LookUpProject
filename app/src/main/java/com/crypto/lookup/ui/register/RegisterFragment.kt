package com.crypto.lookup.ui.register

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.crypto.lookup.R
import com.crypto.lookup.data.User
import com.crypto.lookup.data.UserFirebaseDaoImpl
import com.crypto.lookup.data.UserService
import com.crypto.lookup.data.listeners.onSaveDataListener
import com.crypto.lookup.databinding.FragmentRegisterBinding
import com.crypto.lookup.utils.Common
import com.crypto.lookup.utils.Validation
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import java.sql.Timestamp
import java.util.*


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val db = UserService(UserFirebaseDaoImpl())
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setValidationListeners()
//        //TODO test data remove
//        binding.email.setText("test@test.com")
//        binding.password.setText("123456")
//        binding.passwordAgain.setText("123456")
//        binding.name.setText("Oguzhan")
//        binding.surname.setText("Duymaz")
//        binding.identityNumber.setText("12345678900")
//        binding.phoneNo.setText("123456")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setValidationListeners() {
        var isEnable = arrayListOf<Boolean>(false, false, false, false, false, false, false)
        binding.email.addTextChangedListener {
            val emailValid = Validation.isEmailValid(it.toString()) || it.toString().isEmpty()
            if (emailValid) {
                isEnable[0] = emailValid
                binding.emailTil.isErrorEnabled = false
            } else {
                binding.emailTil.error = getString(R.string.email_valid)
            }
        }

        binding.name.addTextChangedListener {
            val nameValid = Validation.isTextValid(it.toString(), 10, 2) || it.toString().isEmpty()
            if (nameValid) {
                isEnable[1] = nameValid
                binding.nameTil.isErrorEnabled = false
            } else {
                binding.nameTil.error = getString(R.string.name_valid)
            }
        }

        binding.password.addTextChangedListener {
            val passwordValid = Validation.isTextValid(it.toString(), 10, 6) || it.toString().isEmpty()
            if (passwordValid) {
                isEnable[2] = passwordValid
                binding.passwordTil.isErrorEnabled = false
            } else {
                binding.passwordTil.error = getString(R.string.password_valid)
            }
        }

        binding.passwordAgain.addTextChangedListener {
            val passwordAgainValid = Validation.isTextValid(it.toString(), 10, 6) || it.toString().isEmpty()
            val passwordEqual = binding.password.text.toString() == it.toString() || it.toString().isEmpty()
            if (!passwordAgainValid) binding.passwordAgainTil.error = getString(R.string.password_valid)
            if (!passwordEqual) binding.passwordAgainTil.error = getString(R.string.password_equal)
            if (passwordAgainValid && passwordEqual) binding.passwordAgainTil.isErrorEnabled = false
            isEnable[3] = passwordAgainValid && passwordEqual
        }

        binding.identityNumber.addTextChangedListener {
            val identityNumberValid = Validation.isTextValid(it.toString(), 11, 11) || it.toString().isEmpty()
            if (identityNumberValid) {
                isEnable[4] = identityNumberValid
                binding.identityNumberTil.isErrorEnabled = false
            } else {
                binding.identityNumberTil.error = getString(R.string.identity_number_valid)
            }
        }

        binding.phoneNo.addTextChangedListener {
            val phoneValid = Validation.isPhoneValid(it.toString()) || it.toString().isEmpty()
            if (phoneValid) {
                isEnable[5] = phoneValid
                binding.phoneNoTil.isErrorEnabled = false
            } else {
                binding.phoneNoTil.error = getString(R.string.phone_Error)
            }
        }
        val birthdate = Calendar.getInstance()
        val date = object : DatePickerDialog.OnDateSetListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int
            ) {
                birthdate.set(Calendar.YEAR, year)
                birthdate.set(Calendar.YEAR, year)
                birthdate.set(Calendar.MONTH, monthOfYear)
                birthdate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.RegisterBirthdate.text = Common.dateFormat(birthdate)
            }
        }

        binding.RegisterBirthdate.setOnClickListener {
            DatePickerDialog(
                context!!,
                date,
                birthdate.get(Calendar.YEAR),
                birthdate.get(Calendar.MONTH),
                birthdate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.RegisterCreateAccButton.setOnClickListener {
            isEnable[6] = Validation.isBirthdateValid(birthdate)
            binding.RegisterCreateAccButton.visibility = View.INVISIBLE
            binding.registerPB.visibility = View.VISIBLE
            if (isEnable.stream().allMatch { it.equals(true) }) {
                val user = User(
                    binding.name.text.toString(),
                    binding.surname.text.toString(),
                    binding.identityNumber.text.toString().toLong(),
                    binding.phoneNo.text.toString().toLong(),
                    Timestamp(birthdate.timeInMillis),
                    binding.email.text.toString()
                )
                db.save(user, binding.password.text.toString(), object : onSaveDataListener {
                    override fun onSuccess() {
                        Toast.makeText(context, getString(R.string.register_succesfull), Toast.LENGTH_LONG).show()
                        navController.popBackStack()
                    }

                    override fun onFailed(exception: Exception) {
                        if (exception.javaClass.equals(FirebaseAuthUserCollisionException::class.java)) {
                            Toast.makeText(context, getString(R.string.user_exist), Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, getString(R.string.register_failed), Toast.LENGTH_LONG).show()
                        }
                        binding.RegisterCreateAccButton.visibility = View.VISIBLE
                        binding.registerPB.visibility = View.INVISIBLE
                    }

                })
            } else {
                binding.RegisterCreateAccButton.visibility = View.VISIBLE
                binding.registerPB.visibility = View.INVISIBLE
            }
        }
        binding.RegisterLoginButton.setOnClickListener {
            navController.popBackStack()
        }
    }


}