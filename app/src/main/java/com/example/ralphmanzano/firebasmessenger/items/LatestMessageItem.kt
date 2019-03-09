package com.example.ralphmanzano.firebasmessenger.items

import com.example.ralphmanzano.firebasmessenger.R
import com.example.ralphmanzano.firebasmessenger.databinding.LatestMessagesItemBinding
import com.example.ralphmanzano.firebasmessenger.models.Chat
import com.example.ralphmanzano.firebasmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem

class LatestMessageItem(val chat: Chat): BindableItem<LatestMessagesItemBinding>() {
  var chatPartnerUser: User? = null

  override fun getLayout(): Int {
    return R.layout.latest_messages_item
  }

  override fun bind(viewBinding: LatestMessagesItemBinding, position: Int) {
    viewBinding.chat = chat

    val chatPartnerId: String
    if (chat.fromId == FirebaseAuth.getInstance().uid) {
      chatPartnerId = chat.toId
    } else {
      chatPartnerId = chat.fromId
    }

    val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
      override fun onDataChange(p0: DataSnapshot) {
        chatPartnerUser = p0.getValue(User::class.java)
        viewBinding.txtName.text = chatPartnerUser?.username
        Picasso.get().load(chatPartnerUser?.profileImgUrl).into(viewBinding.circleImageView)
      }
      override fun onCancelled(p0: DatabaseError) {}
    })
  }
}