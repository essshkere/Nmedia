package ru.tatalaraydar.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.tatalaraydar.nmedia.databinding.FragmentLoginBinding
import ru.tatalaraydar.nmedia.error.ApiError
import ru.tatalaraydar.nmedia.error.NetworkError
import ru.tatalaraydar.nmedia.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()

            viewModel.login(login, password)
        }


        viewModel.authState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is LoginViewModel.AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().popBackStack()
                }

                is LoginViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                }

                is LoginViewModel.AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })


        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            val errorMessage = when (error) {
                is ApiError -> "Ошибка API: ${error.status}"
                is NetworkError -> "Ошибка сети"
                is UnknownError -> "Неизвестная ошибка"
                else -> "Ошибка"
            }
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}