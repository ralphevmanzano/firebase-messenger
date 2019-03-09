package com.example.ralphmanzano.firebasmessenger.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ralphmanzano.firebasmessenger.R
import com.example.ralphmanzano.firebasmessenger.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    setupUI()
  }

  private fun setupUI() {
    btnLogin.setOnClickListener {
      val email = etLoginUsername.text.toString()
      val password = etLoginPassword.text.toString()

      Log.d("Login", "Attempt login with email/pw: $email/+++")

      //Firebase Auth
      FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
          .addOnCompleteListener {
            startLatestMessagesActivity()
          }
          .addOnFailureListener {
            Toast.makeText(this, "Unexpected error occured", Toast.LENGTH_LONG).show()
            Log.e("LoginActivity", it.localizedMessage)
          }
    }

    txtBackToReg.setOnClickListener {
      this.onBackPressed()
    }
  }

  private fun startLatestMessagesActivity() {
    val intent = Intent(this, LatestMessagesActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
  }
}
