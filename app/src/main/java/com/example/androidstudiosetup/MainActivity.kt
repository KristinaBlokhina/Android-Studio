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
                AppDestinations.REMEMBER -> RememberScreen(contentModifier)
                AppDestinations.SAVEABLE -> SaveableScreen(contentModifier)
                AppDestinations.VIEWMODEL -> ViewModelScreen(contentModifier)
            }
        }
    }
}

// ---------- Екран 1: remember (після повороту екрана значення ЗНИКНЕ) ----------
// Усе по центру, середній шрифт, вузька кнопка
@Composable
fun RememberScreen(modifier: Modifier = Modifier) {
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