package ru.tatalaraydar.nmedia.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.tatalaraydar.nmedia.viewmodel.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginBinding.bind(view)

        binding.signInButton.setOnClickListener {
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            viewModel.login(login, password)
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is LoginViewModel.AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().popBackStack()
                }
                is LoginViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE

                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}