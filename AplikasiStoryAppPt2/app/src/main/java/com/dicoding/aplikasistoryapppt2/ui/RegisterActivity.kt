package com.dicoding.aplikasistoryapppt2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.aplikasistoryapppt2.R
import com.dicoding.aplikasistoryapppt2.api.ApiConfig
import com.dicoding.aplikasistoryapppt2.data.RegisterResponse
import com.dicoding.aplikasistoryapppt2.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        showLoading(true)
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : retrofit2.Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    Toast.makeText(this@RegisterActivity, resources.getString(R.string.register_success), Toast.LENGTH_LONG).show()
                    val moveToLoginActivity = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(moveToLoginActivity)
                    finish()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    Toast.makeText(this@RegisterActivity, resources.getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@RegisterActivity, resources.getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}