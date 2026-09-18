package ovh.gabrielhuav.flasklogin.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ovh.gabrielhuav.flasklogin.ui.screens.LoginScreen
import kotlinx.coroutines.launch
import ovh.gabrielhuav.flasklogin.ui.screens.CrudScreen
import ovh.gabrielhuav.flasklogin.ui.screens.RegisterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var isLoggedIn by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú de Opciones", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()

                if (!isLoggedIn) {
                    NavigationDrawerItem(
                        label = { Text("Iniciar Sesión") },
                        selected = false,
                        onClick = {
                            navController.navigate("login")
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Registro") },
                        selected = false,
                        onClick = {
                            navController.navigate("register")
                            scope.launch { drawerState.close() }
                        }
                    )
                } else {
                    NavigationDrawerItem(
                        label = { Text("Operaciones CRUD") },
                        selected = false,
                        onClick = {
                            navController.navigate("crud")
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Cerrar Sesión") },
                        selected = false,
                        onClick = {
                            isLoggedIn = false
                            navController.navigate("login") { popUpTo(0) }
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("App Móvil REST") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            isLoggedIn = true
                            navController.navigate("crud")
                        },
                        onNavigateToRegister = { navController.navigate("register") }
                    )
                }
                composable("register") {
                    RegisterScreen(
                        onRegisterSuccess = {
                            navController.navigate("login"){
                                popUpTo("login"){inclusive = true}
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate("login")
                        }
                    )
                }
                composable("crud") {
                    CrudScreen()
                }
            }
        }
    }
}