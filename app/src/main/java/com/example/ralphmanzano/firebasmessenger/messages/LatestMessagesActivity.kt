package com.example.ralphmanzano.firebasmessenger.messages

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ralphmanzano.firebasmessenger.R
import com.example.ralphmanzano.firebasmessenger.auth.RegisterActivity
import com.example.ralphmanzano.firebasmessenger.items.LatestMessageItem
import com.example.ralphmanzano.firebasmessenger.items.UserItem
import com.example.ralphmanzano.firebasmessenger.models.Chat
import com.example.ralphmanzano.firebasmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

  companion object {
    var user: User? = null
  }

  private val latestMessagesMap = HashMap<String, Chat>()
  private val adapter = GroupAdapter<ViewHolder>()
  private lateinit var mAuth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_latest_messages)
    setSupportActionBar(toolbar)

    mAuth = FirebaseAuth.getInstance()

    isUserLoggedIn()
    fetchUser()
    setupUI()
  }

  private fun setupUI() {
    rv.adapter = adapter
    rv.layoutManager = LinearLayoutManager(this)
    rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    adapter.setOnItemClickListener{ item, view ->
      @Suppress("NAME_SHADOWING") val item = item as LatestMessageItem
      showChatLogActivity(item.chatPartnerUser!!)
    }
    setupFirebaseDatabase()

    fab.setOnClickListener {
      showNewMessageActivity()
    }
  }

  private fun setupFirebaseDatabase() {
    val fromId = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
    ref.addChildEventListener(object: ChildEventListener {

      override fun onChildAdded(p0: DataSnapshot, p1: String?) {
        val chat = p0.getValue(Chat::class.java) ?: return

        latestMessagesMap[p0.key!!] = chat
        refreshRecyclerViewMessages()
      }
      override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        val chat = p0.getValue(Chat::class.java) ?: return

        latestMessagesMap[p0.key!!] = chat
        refreshRecyclerViewMessages()
      }

      override fun onCancelled(p0: DatabaseError) {}
      override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
      override fun onChildRemoved(p0: DataSnapshot) {}

    })
  }

  private fun refreshRecyclerViewMessages() {
    val chats: MutableCollection<LatestMessageItem> = mutableListOf()
    latestMessagesMap.values.forEach {
      chats.add(LatestMessageItem(it))
    }
    adapter.update(chats)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.nav_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.menu_sign_out -> {
        mAuth.signOut()
        showRegisterActivity()
      }
    }

    return super.onOptionsItemSelected(item)
  }

  private fun showNewMessageActivity() {
    val intent = Intent(this, NewMessageActivity::class.java)
    startActivity(intent)
  }

  private fun isUserLoggedIn() {
    val uid = mAuth.uid
    if (uid == null) {
      showRegisterActivity()
    }
  }

  private fun fetchUser() {
    val uid = mAuth.uid

    if (uid != null) {
      val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
      ref.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
          user = p0.getValue(User::class.java) ?: return
        }
      })
    }
  }

  private fun showRegisterActivity() {
    val intent = Intent(this, RegisterActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
  }

  private fun showChatLogActivity(user: User) {
    val intent = Intent(this, ChatLogActivity::class.java)
    intent.putExtra(NewMessageActivity.USER_KEY, user)
    startActivity(intent)
  }
}
