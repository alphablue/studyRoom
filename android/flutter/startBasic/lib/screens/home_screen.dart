
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:startbasic/utils/app_styles.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Styles.bgColor,
      body: ListView(
        children: [
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      /**
                       * 내부 텍스트의 라인을 맞춰주기 위한 속성
                       * */
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text("Good Morning", style: Styles.headLineStyle3,),
                        /**
                         * 이렇게 SizedBox 를 계속 사용하기는 어려우니 Gap 이라는 라이브러리를 활용하면 편리하게 간격
                         * 조절이 가능하다. pubspec 을 참고 해 보면 gap 이라는 것이 등록 되어 있음
                         * */
                        // SizedBox(height: 5,),
                        const Gap(5),
                        Text("Book Tickets", style: Styles.headLineStyle1,)
                      ],
                    ),
                    Container(
                      height: 50,
                      width: 50,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(10),
                        image: const DecorationImage(
                          fit: BoxFit.fitHeight,
                          image: AssetImage(
                            "assets/images/img_1.png"
                          )
                        )
                      ),
                    )
                  ],
                )
              ],
            ),
          )
        ],
      ),
    );
  }
}
