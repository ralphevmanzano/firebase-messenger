package com.example.ralphmanzano.firebasmessenger.items

import com.example.ralphmanzano.firebasmessenger.R
import com.example.ralphmanzano.firebasmessenger.databinding.ChatFromItemBinding
import com.example.ralphmanzano.firebasmessenger.models.Chat
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem

class ChatFromItem(val chat: Chat, private val imgUrl: String): BindableItem<ChatFromItemBinding>() {
  override fun getLayout(): Int {
    return R.layout.chat_from_item
  }

  override fun bind(viewBinding: ChatFromItemBinding, position: Int) {
    viewBinding.chat = this.chat
    Picasso.get().load(imgUrl).into(viewBinding.imgProfile)
  }
}