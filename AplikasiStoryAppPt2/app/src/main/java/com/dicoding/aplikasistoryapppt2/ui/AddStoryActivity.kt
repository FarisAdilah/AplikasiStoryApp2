package com.dicoding.aplikasistoryapppt2.ui

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.dicoding.aplikasistoryapppt2.R
import com.dicoding.aplikasistoryapppt2.api.ApiConfig
import com.dicoding.aplikasistoryapppt2.createCustomTempFile
import com.dicoding.aplikasistoryapppt2.data.StoryResponse
import com.dicoding.aplikasistoryapppt2.data.UserPreference
import com.dicoding.aplikasistoryapppt2.databinding.ActivityAddStoryBinding
import com.dicoding.aplikasistoryapppt2.rotateFile
import com.dicoding.aplikasistoryapppt2.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private lateinit var mUserPreference: UserPreference
    private lateinit var addStoryViewModel: AddStoryViewModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION)
        }

        addStoryViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[AddStoryViewModel::class.java]
        addStoryViewModel.description.observe(this) {
            binding.edAddDescription.setText(it)
        }
        addStoryViewModel.photo.observe(this) {
            binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(it.path))
        }

        mUserPreference = UserPreference(this)

        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.dicoding.aplikasistoryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                getFile = file
                rotateFile(file, true)
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
                addStoryViewModel.updatePhoto(file)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
                addStoryViewModel.updatePhoto(getFile as File)
            }
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            showLoading(true)
            val file = reduceFileImage(getFile as File)

            addStoryViewModel.updateDescription(binding.edAddDescription.text.toString())
            val description = binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val token = "Bearer " + mUserPreference.getUser().token


            val client = ApiConfig.getApiService().addStory(description, imageMultipart, null, null, token)
            client.enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    showLoading(false)
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        Toast.makeText(this@AddStoryActivity, resources.getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@AddStoryActivity, StoriesActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    showLoading(false)
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })

        } else {
            Toast.makeText(this, resources.getString(R.string.file_failed_exist), Toast.LENGTH_SHORT).show()
        }
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return  file
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val MAXIMAL_SIZE = 1000000
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
        private const val TAG = "AddStoryActivity"
        const val RESULT_CODE = 101
    }
}