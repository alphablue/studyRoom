package com.example.portfolio.localdb

import androidx.room.Entity

@Entity(primaryKeys = ["userId", "restaurantId"])
data class Like(
    val userId: String,
    val restaurantId: String,
    val restaurantImage: String,
    val restaurantName: String
)

@Entity(primaryKeys = ["userId", "restaurantId"])
data class CartWithOrder(
    val userId: String = "",
    val restaurantId: String = "",
    val restaurantName: String ="",
    val menuName: String = "",
    val price: String =""
)


/**
 * TODO
 * 설정 하는 목록이 하나밖에 없으므로 shared preference 를 사용하고
 * 차후에 많아진다면 옮기는 것을 고려 해 볼것
 * */
//@Entity
//data class UserOptionState(
//    val notifyOption: Boolean
//)
