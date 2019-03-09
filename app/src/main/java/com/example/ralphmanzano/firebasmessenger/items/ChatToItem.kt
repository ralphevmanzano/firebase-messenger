package com.example.ralphmanzano.firebasmessenger.items

import com.example.ralphmanzano.firebasmessenger.R
import com.example.ralphmanzano.firebasmessenger.databinding.ChatToItemBinding
import com.example.ralphmanzano.firebasmessenger.models.Chat
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem

class ChatToItem(val chat: Chat, private val imgUrl: String): BindableItem<ChatToItemBinding>() {
  override fun getLayout(): Int {
    return R.layout.chat_to_item
  }

  override fun bind(viewBinding: ChatToItemBinding, position: Int) {
    viewBinding.chat = this.chat
    Picasso.get().load(imgUrl).into(viewBinding.imgProfile)
  }
}