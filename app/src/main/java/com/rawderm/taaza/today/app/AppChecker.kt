package com.rawderm.taaza.today.app


import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.rawderm.taaza.today.core.utils.checkInternet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppChecker(private val context: Context) {

    private val _uiState = MutableStateFlow<UiState>(UiState.Checking)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var attempts = 0          // how many tries so far
    private val maxAttempts = 5       // give up after 5
    private val baseDelay = 500L      // 0.5 s initial back-off

    fun check() {
        attempts = 0
        checkInternal()
    }

    private fun checkInternal() {
        if (attempts >= maxAttempts) {
            _uiState.value = UiState.GaveUp
            return
        }
        attempts++

        _uiState.value = UiState.Checking
        context.checkInternet { online ->
            if (!online) {
                _uiState.value = UiState.NoInternet
                return@checkInternet
            }
            // online → fetch flag
            _uiState.value = UiState.Loading
            FirebaseRemoteConfig.getInstance()
                .fetch(if (attempts == 1) 0 else baseDelay * attempts) // back-off
                .addOnSuccessListener {
                    FirebaseRemoteConfig.getInstance().activate()
                    val working = FirebaseRemoteConfig.getInstance()
                        .getBoolean("isWorking")
                    _uiState.value = if (working) UiState.Working else UiState.Stopped
                }
                .addOnFailureListener {
                    // network error while fetching → treat as stopped
                    _uiState.value = UiState.Stopped
                }
        }
    }

    enum class UiState {
        Checking, Loading, NoInternet, Stopped, Working, GaveUp
    }
}