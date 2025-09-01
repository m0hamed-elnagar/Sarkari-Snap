package com.example.sarkarisnap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sarkarisnap.bloger.ui.home.HomeScreenRoot
import com.example.sarkarisnap.bloger.ui.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
