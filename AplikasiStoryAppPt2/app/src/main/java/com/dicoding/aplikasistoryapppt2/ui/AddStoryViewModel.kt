package com.dicoding.aplikasistoryapppt2.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class AddStoryViewModel : ViewModel() {
    private val _photo = MutableLiveData<File>()
    val photo: LiveData<File> = _photo

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    fun updateDescription(newDesc: String) {
        _description.value = newDesc
    }

    fun updatePhoto(newPhoto: File) {
        _photo.value = newPhoto
    }
}