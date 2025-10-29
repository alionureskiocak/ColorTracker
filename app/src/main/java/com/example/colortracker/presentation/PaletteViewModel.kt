package com.example.colortracker.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.repository.PaletteRepository
import com.example.colortracker.domain.use_case.AnalyzeImageColorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class PaletteViewModel @Inject constructor(
    private val analyzeImageColorUseCase: AnalyzeImageColorUseCase,
    private val repository: PaletteRepository
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

    fun insertSwatch(bitmap: Bitmap,swatch : ColorSwatchInfo){
        viewModelScope.launch {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val bytes = stream.toByteArray()
            repository.insertSwatch(
                ColorEntity(swatch,bytes)
            )
        }
    }

    fun deleteSwatch(colorEntity: ColorEntity){
        viewModelScope.launch {
            repository.deleteSwatch(colorEntity)
        }

    }

    fun clearAll(){
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun getAllSwatch(){
        val colorEntities = repository.getAllSwatch()
        _uiState.update {
            it.copy(colorEntities = colorEntities)
        }
    }
}

data class PaletteUiState(
    val isLoading : Boolean = false,
    val colors : List<ColorSwatchInfo> = emptyList(),
    val colorEntities : List<ColorEntity> = emptyList(),
    val error : String? = null
)