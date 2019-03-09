package com.example.ralphmanzano.firebasmessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid: String, val username: String, val profileImgUrl: String): Parcelable {
  constructor() : this("", "", "")
}