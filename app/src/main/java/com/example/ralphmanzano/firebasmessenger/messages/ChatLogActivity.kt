package com.example.ralphmanzano.firebasmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ralphmanzano.firebasmessenger.R
import com.example.ralphmanzano.firebasmessenger.items.ChatFromItem
import com.example.ralphmanzano.firebasmessenger.items.ChatToItem
import com.example.ralphmanzano.firebasmessenger.models.Chat
import com.example.ralphmanzano.firebasmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {

  companion object {
    private const val TAG = "ChatLogActivity"
  }

  private val adapter = GroupAdapter<ViewHolder>()
  private lateinit var mDb: FirebaseDatabase
  private lateinit var toUser: User
  private lateinit var fromId: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_chat_log)
    setSupportActionBar(toolbar)

    toUser = intent.getParcelableExtra(NewMessageActivity.USER_KEY)
    mDb = FirebaseDatabase.getInstance()
    fromId = FirebaseAuth.getInstance().uid.toString()

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.title = toUser.username

    setupUI()
    setupFirebase()
  }

  private fun setupUI() {
    rv.adapter = adapter
    rv.layoutManager = LinearLayoutManager(this)

    btnSend.setOnClickListener {
      if (etMessage.text.toString().isEmpty()) return@setOnClickListener
      sendMessage(etMessage.text.toString())
    }
  }

  private fun setupFirebase() {
    val ref = mDb.getReference("/user-messages/$fromId/${toUser.uid}")

    ref.addChildEventListener(object : ChildEventListener {
      override fun onCancelled(p0: DatabaseError) {
      }

      override fun onChildMoved(p0: DataSnapshot, p1: String?) {
      }

      override fun onChildChanged(p0: DataSnapshot, p1: String?) {
      }

      override fun onChildAdded(p0: DataSnapshot, p1: String?) {
        val chat = p0.getValue(Chat::class.java)
        if (chat != null) {
          Log.d(TAG, chat.message)

          if (chat.fromId == fromId) {
            val currentUser = LatestMessagesActivity.user
            if (currentUser != null) {
              adapter.add(ChatFromItem(chat, currentUser.profileImgUrl))
            }
          } else {
            adapter.add(ChatToItem(chat, toUser.profileImgUrl))
          }
        }
        rv.scrollToPosition(adapter.itemCount - 1)
      }

      override fun onChildRemoved(p0: DataSnapshot) {
      }
    })
  }

  private fun sendMessage(msg: String) {
    val ref = mDb.getReference("/user-messages/$fromId/${toUser.uid}").push()
    val toRef = mDb.getReference("/user-messages/${toUser.uid}/$fromId").push()

    if (ref.key == null) return

    val chat = Chat(ref.key!!, fromId, toUser.uid, msg, System.currentTimeMillis() / 1000)

    ref.setValue(chat)
        .addOnSuccessListener {
          etMessage.text.clear()
          rv.scrollToPosition(adapter.itemCount - 1)
        }

    toRef.setValue(chat)

    val latestMessageRef = mDb.getReference("/latest-messages/$fromId/${toUser.uid}")
    val latestMessageToRef = mDb.getReference("/latest-messages/${toUser.uid}/$fromId")

    latestMessageRef.setValue(chat)
    latestMessageToRef.setValue(chat)
  }
}
