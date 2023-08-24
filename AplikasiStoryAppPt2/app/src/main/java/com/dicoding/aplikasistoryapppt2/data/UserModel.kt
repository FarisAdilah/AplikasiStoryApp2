package com.dicoding.aplikasistoryapppt2.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var userId: String? = null,
    var name: String? = null,
    var token: String? = null
): Parcelable
