package com.example.ralphmanzano.firebasmessenger.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ralphmanzano.firebasmessenger.messages.LatestMessagesActivity
import com.example.ralphmanzano.firebasmessenger.R
import com.example.ralphmanzano.firebasmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

  private val TAG: String = "RegisterActivity"
  
  private var seletedPhotoUri: Uri? = null
  private lateinit var mAuth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_register)

    mAuth = FirebaseAuth.getInstance()

    setupUI()
  }

  private fun setupUI() {
    btnRegister.setOnClickListener {
      performRegister()
    }

    txtHaveAccount.setOnClickListener {
      Log.d(TAG, "Show logdin activity")
      val intent = Intent(this, LoginActivity::class.java)
      startActivity(intent)
    }

    btnSelectPhoto.setOnClickListener {
      Log.d(TAG, "Try to show photo selector")

      val intent = Intent(Intent.ACTION_PICK)
      intent.type = "image/*"
      startActivityForResult(intent, 0)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
      Log.d(TAG, "Photo was selected")

      seletedPhotoUri = data.data
      val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, seletedPhotoUri)

      imgPhoto.setImageBitmap(bitmap)
      btnSelectPhoto.alpha = 0f
    }
  }

  private fun performRegister() {
    val email = etRegEmail.text.toString()
    val password = etRegPassword.text.toString()

    if (email.isEmpty() || password.isEmpty() || seletedPhotoUri == null) {
      Toast.makeText(this, "Please enter your username, email, password and profile picture", Toast.LENGTH_LONG).show()
    }
    Log.d(TAG, "Email is: $email \tPassword is: $password")

    mAuth
        .createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
          if (!it.isSuccessful) return@addOnCompleteListener

          //else if successful
          Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")

          uploadImageToFirebaseStorage()
        }
        .addOnFailureListener {
          Log.d(TAG, "Failed to create user: ${it.message}")
          Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
  }

  private fun uploadImageToFirebaseStorage() {
    if (seletedPhotoUri == null) return

    val filename = UUID.randomUUID().toString()
    val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

    ref.putFile(seletedPhotoUri!!)
        .addOnSuccessListener {
          Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

          ref.downloadUrl.addOnSuccessListener { uri ->
            Log.d(TAG, "File location: $uri")

            saveUserToFirebaseDatabase(uri.toString())
          }
        }
        .addOnFailureListener { 
          Log.e(TAG, "Error putfile selectedPhotoUri ", it)
        }
  }

  private fun saveUserToFirebaseDatabase(profileImgUrl: String) {
    val uid = mAuth.uid ?: ""
    val username = etRegUsername.text.toString()
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

    val user = User(uid, username, profileImgUrl)

    ref.setValue(user)
        .addOnSuccessListener {
          Log.d(TAG, "Finally we saved the user to Firebase Database")

          startLatestMessagesActivity()
        }
        .addOnFailureListener { 
          Log.e(TAG, "Error adding to firebase database", it)
        }
  }

  private fun startLatestMessagesActivity() {
    val intent = Intent(this, LatestMessagesActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
  }
}
