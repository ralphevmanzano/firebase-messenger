package com.example.ralphmanzano.firebasmessenger.items

import com.example.ralphmanzano.firebasmessenger.R
import com.example.ralphmanzano.firebasmessenger.databinding.UserItemBinding
import com.example.ralphmanzano.firebasmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem

class UserItem(val user: User): BindableItem<UserItemBinding>() {

  override fun getLayout(): Int {
    return R.layout.user_item
  }

  override fun bind(viewBinding: UserItemBinding, position: Int) {
    viewBinding.user = this.user
    Picasso.get().load(this.user.profileImgUrl).into(viewBinding.imgProfile)
  }

}