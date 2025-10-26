package com.example.colortracker.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.use_case.AnalyzeImageColorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaletteViewModel @Inject constructor(
    private val analyzeImageColorUseCase: AnalyzeImageColorUseCase
) : ViewModel(){

    private val _uiState = MutableStateFlow(PaletteUiState())
    val uiState : StateFlow<PaletteUiState> = _uiState.asStateFlow()

    fun analyzeBitmap(bitmap : Bitmap){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true,error = null) }
            try {
                val colors = analyzeImageColorUseCase(bitmap)
                _uiState.update { it.copy(isLoading = false,colors = colors) }
            }catch (e: Exception){
                _uiState.update { it.copy(isLoading = false,error = e.localizedMessage?:"Error!") }
            }
        }
    }
}

data class PaletteUiState(
    val isLoading : Boolean = false,
    val colors : List<ColorSwatchInfo> = emptyList(),
    val error : String? = null
)