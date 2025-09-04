package com.example.sarkarisnap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.sarkarisnap.BuildConfig
import com.example.sarkarisnap.bloger.ui.home.HomeScreenRoot
import com.example.sarkarisnap.bloger.ui.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            DebugComposable("MyScreen") {
                App()
            }

        }
    }
}
@Composable
fun DebugComposable(name: String, content: @Composable () -> Unit) {
    val count = remember { mutableStateOf(0) }
    SideEffect {
        count.value++
        println("Recomposition #${count.value} for $name")
    }
    content()
}
