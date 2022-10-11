package com.example.portfolio

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.portfolio.ui.common.notification.NotificationBuilder
import com.example.portfolio.ui.navigation.Sections
import com.example.portfolio.ui.navigation.applicationNavGraph
import com.example.portfolio.ui.navigation.rememberApplicationNavState
import com.example.portfolio.ui.theme.PortfolioTheme
import com.example.portfolio.ui.theme.textColor
import com.example.portfolio.ui.theme.textPrimaryColor

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val GOOGLE_MAP = "GoogleMap"
    const val LOGIN_PAGE = "Login"
    const val CART_PAGE = "Cart"
}

@Composable
fun StartApp(
    activityViewModel: MainActivityViewModel
) {
    PortfolioTheme {
        val appState = rememberApplicationNavState()
        val context = LocalContext.current
        val noti = NotificationBuilder(context)

        Scaffold(
            modifier= Modifier.fillMaxSize(),
            bottomBar = {
                if(appState.shouldShowBottomBar) {
                    ApplicationBottomBar(
                        tabs = appState.bottomBarTabs,
                        currentRoute = appState.currentRoute!!,
                        navigateToRoute = appState::navigateToBottomBarRoute,
                        navController = appState.navController
                    )
                }
            },
            floatingActionButton = {

                if(activityViewModel.floatingState) {
                    FloatingActionButton(
                        backgroundColor = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(14.dp),
                        onClick = {
                            noti.createDeliveryNotificationChannel(
                                true, "알림 테스트 중", "알림 테스트 내용"
                            )
                        }) {
                        Text(text = "테스트용", color = textColor)
                    }
                }
            }
        ) { innerPaddingModifier ->
            NavHost(navController = appState.navController,
                startDestination = MainDestinations.HOME_ROUTE,
                modifier = Modifier.padding(innerPaddingModifier)
            ) {
                applicationNavGraph(
                    upPress = appState::upPress,
                    itemSelect = appState::navigateToItemDetail,
                    goMap = appState::navigateToGoogleMap,
                    goLogin = appState::navigateToLoginPage,
                    goCart = appState::navigateToCart,
                    activityViewModel
                )
            }

        }
    }
}

@Composable
fun ApplicationBottomBar(
    tabs: Array<Sections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    navController: NavController
) {

    // TODO 커스텀 네비게이션을 활용하기 위해 필요함
//    val routes = remember { tabs.map { it.route }}
//    val currentSection = tabs.first { it.route == currentRoute }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val paddingValues = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    BottomNavigation(
        modifier = Modifier.padding(bottom = paddingValues),
    ) {
        tabs.forEach { screen ->
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any{it.route == screen.route} == true,
                onClick = {
                    Log.d("checkCurrentRoute","${currentDestination?.hierarchy?.any { it.route == screen.route } == true}")
                    navigateToRoute(screen.route)
                          },
                icon = {
                    Icon(imageVector = screen.icon,
                    contentDescription = screen.title)
                }
            )
        }
    }
}