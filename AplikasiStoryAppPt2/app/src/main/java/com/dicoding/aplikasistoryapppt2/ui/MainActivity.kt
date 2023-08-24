package com.dicoding.aplikasistoryapppt2.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dicoding.aplikasistoryapppt2.R
import com.dicoding.aplikasistoryapppt2.api.ApiConfig
import com.dicoding.aplikasistoryapppt2.data.LoginResponse
import com.dicoding.aplikasistoryapppt2.data.UserModel
import com.dicoding.aplikasistoryapppt2.data.UserPreference
import com.dicoding.aplikasistoryapppt2.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mUserPreference: UserPreference
    private lateinit var userModel: UserModel
    private var isPreferenceEmpty = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUserPreference = UserPreference(this)
        checkUserPreference()

        playAnimation()

        binding.btnSignIn.setOnClickListener {
            signIn()
        }

        binding.btnRegister.setOnClickListener {
            register()
        }
    }

    private fun playAnimation() {
        val name = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(1000)
        val password = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(1000)
        val login = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(1000)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(1000)

        val together = AnimatorSet().apply {
            playSequentially(name, password)
        }

        AnimatorSet().apply {
            playSequentially(together, login, register)
            start()
        }
    }

    private fun checkUserPreference() {
        userModel = mUserPreference.getUser()
        if (userModel.userId.toString().isNotEmpty()) {
            isPreferenceEmpty = false
            val intent = Intent(this, StoriesActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            isPreferenceEmpty = true
        }
    }

    private fun register() {
        val moveToRegisterActivity = Intent(this@MainActivity, RegisterActivity::class.java)
        startActivity(moveToRegisterActivity)
    }

    private fun signIn() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        showLoading(true)
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                showLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val userId = responseBody.loginResult.userId
                    val name = responseBody.loginResult.name
                    val token = responseBody.loginResult.token
                    saveUser(userId, name, token)

                    Toast.makeText(this@MainActivity, resources.getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show()
                    val moveToStoriesActivity = Intent(this@MainActivity, StoriesActivity::class.java)
                    startActivity(moveToStoriesActivity)
                    finish()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    Toast.makeText(this@MainActivity, resources.getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@MainActivity, resources.getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUser(userId: String, name: String, token: String) {
        userModel = UserModel(userId, name, token)
        mUserPreference.setUser(userModel)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}