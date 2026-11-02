package com.example.androidstudiosetup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidstudiosetup.ui.theme.AndroidStudioSetUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidStudioSetUpTheme {
                MyApp()
            }
        }
    }
}

// ---------- Пункти навігації (мінімум 2, тут 3) ----------
enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    REMEMBER("remember", Icons.Default.Home),
    SAVEABLE("rememberSaveable", Icons.Default.Favorite),
    VIEWMODEL("ViewModel", Icons.Default.AccountBox),
}

@Composable
fun MyApp() {
    // Поточний вибраний пункт зберігається через rememberSaveable,
    // щоб після повороту екрана ви лишилися на тій самій вкладці
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.REMEMBER) }

    // NavController створюємо один раз на верхньому рівні (як радить стаття),
    // щоб історія переходів не губилась при перемиканні нижніх вкладок
    val navController = rememberNavController()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination },
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)
            when (currentDestination) {
                AppDestinations.REMEMBER -> RememberNavHost(navController, contentModifier)
                AppDestinations.SAVEABLE -> SaveableScreen(contentModifier)
                AppDestinations.VIEWMODEL -> ViewModelScreen(contentModifier)
            }
        }
    }
}

// ---------- Навігація всередині вкладки remember (лабораторна 2) ----------
// Маршрути (назви підекранів)
object Routes {
    const val MAIN = "main"
    const val DETAILS = "details"
}

@Composable
fun RememberNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN,
        modifier = modifier,
    ) {
        // Підекран 1: розмітка з лабораторної 1 + кнопка переходу
        composable(Routes.MAIN) {
            // Передаємо лише колбек, а не сам navController
            RememberScreen(onNext = { navController.navigate(Routes.DETAILS) })
        }
        // Підекран 2: довільна розмітка + кнопка повернення
        composable(Routes.DETAILS) {
            DetailsScreen(onBack = { navController.navigateUp() })
        }
    }
}

// Підекран 2: довільна розмітка
@Composable
fun DetailsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Другий підекран",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = "Сюди ви потрапили за допомогою NavController.\n" +
                    "Поверніться кнопкою нижче або системною кнопкою/жестом «Назад».",
            fontSize = 18.sp,
        )
        Button(onClick = onBack) {
            Text("← Назад")
        }
    }
}

// ---------- Підекран 1: remember (після повороту екрана значення ЗНИКНЕ) ----------
// Усе по центру, середній шрифт, вузька кнопка
@Composable
fun RememberScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("Текст до натискання (remember)") }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = text, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { text = "Кнопку натиснуто! Поверніть екран і подивіться, що буде" },
            modifier = Modifier.fillMaxWidth(0.5f),
        ) {
            Text("Натисни")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Кнопка переходу на другий підекран
        Button(onClick = onNext) {
            Text("Далі →")
        }
    }
}

// ---------- Екран 2: rememberSaveable (після повороту значення ЗБЕРЕЖЕТЬСЯ) ----------
// Вирівнювання зліва зверху, великий жирний шрифт, широка висока кнопка
@Composable
fun SaveableScreen(modifier: Modifier = Modifier) {
    var text by rememberSaveable { mutableStateOf("Текст до натискання (rememberSaveable)") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { text = "Це значення переживе поворот екрана" },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
        ) {
            Text("Змінити текст", fontSize = 18.sp)
        }
    }
}

// ---------- Екран 3 (додатково): ViewModel ----------
class CounterViewModel : ViewModel() {
    var text by mutableStateOf("Текст до натискання (ViewModel)")
        private set

    fun onButtonClick() {
        text = "Змінено через ViewModel"
    }
}

// Вирівнювання справа внизу, дрібний шрифт, маленька кнопка
@Composable
fun ViewModelScreen(
    modifier: Modifier = Modifier,
    vm: CounterViewModel = viewModel(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        Text(text = vm.text, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { vm.onButtonClick() }) {
            Text("OK")
        }
    }
}