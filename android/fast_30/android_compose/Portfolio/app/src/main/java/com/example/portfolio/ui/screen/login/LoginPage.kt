package com.example.portfolio.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.portfolio.MainActivityViewModel
import com.example.portfolio.ui.common.SimpleTitleTopBar

@Composable
fun LoginPage(
    sharedViewModel: MainActivityViewModel,
    upPress: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .background(Color.White)
            .wrapContentSize()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var email by remember { mutableStateOf("") }
        var pass by remember { mutableStateOf("") }

        SimpleTitleTopBar(upPress, "로그인")

        Column {
            TextField(value = email, onValueChange = { email = it })
            TextField(value = pass, onValueChange = { pass = it })
        }

        if (sharedViewModel.loginState.not()) {
            Button(
                onClick = {
                    sharedViewModel.signInWithEmailPassword(
                        email, pass,
                        successCallback = upPress,
                        failCallback = {
                            Toast.makeText(context,
                                "이메일과 비밀번호를 확인해 주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            ) {
                Text(text = "로그인")
            }

            Button(
                onClick = {
                    sharedViewModel.signUpEmailPass(
                        email, pass,
                        successCallback = {
                            Toast.makeText(context, "회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                            upPress()
                        },
                        failCallback = {
                            Toast.makeText(context, "회원가입 실패 다시시도 해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            ) {
                Text("가입하기")
            }
        }

        /**
         * TODO
         * 구글 연동 로그인 기능 버튼 추가
         * */
//        GoogleSignButton(sharedViewModel = sharedViewModel)

    }
}