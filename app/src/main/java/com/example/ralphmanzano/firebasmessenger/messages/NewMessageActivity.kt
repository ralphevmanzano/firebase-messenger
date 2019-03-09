package com.example.ralphmanzano.firebasmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ralphmanzano.firebasmessenger.R
import com.example.ralphmanzano.firebasmessenger.items.UserItem
import com.example.ralphmanzano.firebasmessenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

  companion object {
    const val USER_KEY = "USER_KEY"
  }

  private val adapter = GroupAdapter<ViewHolder>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_new_message)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.title = "Select User"

    setupUI()
    fetchUsers()
  }

  private fun setupUI() {
    adapter.setOnItemClickListener { item, view ->
      val userItem = item as UserItem

      showChatLogActivity(userItem)
    }
    rv.adapter = adapter
    rv.layoutManager = LinearLayoutManager(this)
  }

  private fun showChatLogActivity(userItem: UserItem) {
    val intent = Intent(this, ChatLogActivity::class.java)
    intent.putExtra(USER_KEY, userItem.user)
    startActivity(intent)

    finish()
  }

  private fun fetchUsers() {
    val ref = FirebaseDatabase.getInstance().getReference("/users")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
      override fun onCancelled(p0: DatabaseError) {

      }

      override fun onDataChange(p0: DataSnapshot) {
        val users: MutableCollection<UserItem> = mutableListOf()

        p0.children.forEach {
          val user = it.getValue(User::class.java)
          user?.let { it1 -> users.add(UserItem(it1)) }
        }
        adapter.update(users)
      }
    })
  }
}
