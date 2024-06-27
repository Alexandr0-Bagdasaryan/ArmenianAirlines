package com.example.armenianairlines.presentation

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.armenianairlines.R
import com.example.armenianairlines.data.model.User
import com.example.armenianairlines.domain.viewModel.SharedViewModel
import com.google.android.material.snackbar.Snackbar

private const val TAG = "LoginSignFragment"

class LoginSignFragment : Fragment() {

    interface CallBacks {
        fun loginToMenu()
    }

    private lateinit var type: RadioGroup
    private lateinit var login: RadioButton
    private lateinit var signUp: RadioButton
    private lateinit var edName: EditText
    private lateinit var edSurName: EditText
    private lateinit var edMail: EditText
    private lateinit var edPass: EditText
    private lateinit var enterButton: Button


    private var callbacks: CallBacks? = null


    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as CallBacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login_sign, container, false)
        initViews(view)
        type.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            if (checkedId == login.id) {
                edName.visibility = View.GONE
                edSurName.visibility = View.GONE
            } else if (checkedId == signUp.id) {
                edName.visibility = View.VISIBLE
                edSurName.visibility = View.VISIBLE
            }
        }
        enterButton.setOnClickListener {
            if (validateData(view)) {
                val name = edName.text.trim().toString()
                val email = edMail.text.toString()
                val password = edPass.text.toString()
                val surName = edSurName.text.trim().toString()
                val newUser = User(name, surName, email, password)
                if (signUp.isChecked) {
                    sharedViewModel.firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val uid = sharedViewModel.firebaseAuth.currentUser?.uid
                                sharedViewModel.dataBase.child("users").child(uid!!)
                                    .setValue(newUser)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "Created user")
                                        }
                                    }
                                sharedViewModel.setCurrentUser()
                                callbacks?.loginToMenu()

                            } else {
                                Toast.makeText(
                                    this.context,
                                    "Не удалось создать учетную запись!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    sharedViewModel.firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                sharedViewModel.setCurrentUser()
                                callbacks?.loginToMenu()
                            } else {
                                Toast.makeText(
                                    this.context,
                                    "Не удалось войти в учетную запись!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
        return view
    }


    private fun initViews(view: View) {
        type = view.findViewById(R.id.type)
        login = view.findViewById(R.id.login)
        signUp = view.findViewById(R.id.signUp)
        edName = view.findViewById(R.id.editTextName)
        edSurName = view.findViewById(R.id.editTextSurName)
        edMail = view.findViewById(R.id.editTextEmail)
        edPass = view.findViewById(R.id.editTextPass)
        enterButton = view.findViewById(R.id.buttonEnter)
        edName.visibility=View.GONE
        edSurName.visibility=View.GONE
    }

    private fun validateData(view: View): Boolean {
        val name = edName.text.trim().toString()
        val email = edMail.text.toString()
        val password = edPass.text.toString()
        val surName = edSurName.text.trim().toString()

        var isValid = true
        if (signUp.isChecked) {
            if (name.isEmpty()) {
                edName.error = "Введите имя!"
                isValid = false
            } else {
                edName.error = null
            }

            if (surName.isEmpty()) {
                edSurName.error = "Введите фамилию!"
                isValid = false
            } else {
                edSurName.error = null
            }
        } else {
            edName.error = null
            edSurName.error = null
        }

        if (email.isEmpty()) {
            edMail.error = "Введите почту!"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edMail.error = "Неверный формат почты!"
            isValid = false
        } else {
            edMail.error = null
        }

        if (password.length <= 6) {
            edPass.error = "Пароль должен содержать не менее 6 символов!"
            isValid = false
        } else {
            edPass.error = null
        }

        if (!isValid) {
            val snackbar = Snackbar.make(view, "некорректные данные!", Snackbar.LENGTH_LONG)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.setTextColor(Color.WHITE)
            snackbar.show()
        }

        return isValid
    }
}