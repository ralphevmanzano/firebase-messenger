package com.example.ralphmanzano.firebasmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

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
      Log.d("RegisterActivity", "Show logdin activity")
      val intent = Intent(this, LoginActivity::class.java)
      startActivity(intent)
    }

    btnSelectPhoto.setOnClickListener {
      Log.d("RegisterActivity", "Try to show photo selector")

      val intent = Intent(Intent.ACTION_PICK)
      intent.type = "image/*"
      startActivityForResult(intent, 0)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
      Log.d("RegisterActivity", "Photo was selected")

      seletedPhotoUri = data.data
      val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, seletedPhotoUri)
      val bitmapDrawable = BitmapDrawable(resources, bitmap)

      btnSelectPhoto.background = bitmapDrawable
    }
  }

  private fun performRegister() {
    val email = etRegEmail.text.toString()
    val password = etRegPassword.text.toString()

    if (email.isEmpty() || password.isEmpty()) {
      Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_LONG).show()
    }
    Log.d("RegisterActivity", "Email is: $email \tPassword is: $password")

    mAuth
        .createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
          if (!it.isSuccessful) return@addOnCompleteListener

          //else if successful
          Log.d("Main", "Successfully created user with uid: ${it.result?.user?.uid}")

          uploadImageToFirebaseStorage()
        }
        .addOnFailureListener {
          Log.d("Main", "Failed to create user: ${it.message}")
          Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
  }

  private fun uploadImageToFirebaseStorage() {

  }
}
