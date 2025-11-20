package com.example.colortracker.presentation.palette

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.repository.PaletteRepository
import com.example.colortracker.domain.use_case.AnalyzeImageColorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PaletteViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyzeImageColorUseCase: AnalyzeImageColorUseCase,
    private val repository: PaletteRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(PaletteUiState())
    val uiState : StateFlow<PaletteUiState> = _uiState.asStateFlow()

    fun analyzeBitmap(bitmap : Bitmap){

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true,error = null, bitmap = bitmap) }
            try {
                val colors = analyzeImageColorUseCase(bitmap)
                _uiState.update { it.copy(isLoading = false,swatches = colors) }
                insertSwatch(bitmap,colors)////////////
            }catch (e: Exception){
                _uiState.update { it.copy(isLoading = false,error = e.localizedMessage?:"Error!") }
            }
        }
    }

    init {
        getAllSwatch()
    }

    fun insertSwatch( bitmap: Bitmap, swatches : List<ColorSwatchInfo>){
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(context.filesDir, "swatch_${System.currentTimeMillis()}.png")
            file.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            repository.insertSwatch(
                ColorEntity(swatches,file.absolutePath)
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
        viewModelScope.launch {
            val colorEntities = repository.getAllSwatch()
            _uiState.update {
                it.copy(colorEntities = colorEntities)
            }
        }

    }

    fun updateBitmap(bitmap : Bitmap){
        viewModelScope.launch {
            _uiState.update {
                it.copy(bitmap = bitmap)
            }
        }

    }
}

data class PaletteUiState(
    val isLoading : Boolean = false,
    val swatches : List<ColorSwatchInfo> = emptyList(),
    val bitmap : Bitmap? = null,
    val colorEntities : List<ColorEntity> = emptyList(),
    val error : String? = null
)

