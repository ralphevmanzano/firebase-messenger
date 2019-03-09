package com.example.ralphmanzano.firebasmessenger.models

data class Chat(val id: String, val fromId: String, val toId: String, val message: String, val timeStamp: Long) {
  constructor(): this("", "", "", "", -1)
}