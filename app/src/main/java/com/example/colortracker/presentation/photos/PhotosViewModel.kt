package com.example.colortracker.presentation.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.repository.PaletteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val repository : PaletteRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow<PhotosUiState>(PhotosUiState())
    val uiState : StateFlow<PhotosUiState> = _uiState.asStateFlow()

    init {
        getAllEntities()
    }

    fun getAllEntities(){
        viewModelScope.launch {
            val colorEntities = repository.getAllSwatch()
            _uiState.update {
                it.copy(colorEntities = colorEntities)
            }
        }
    }

    fun deleteColorEntity(colorEntity: ColorEntity){
        viewModelScope.launch {
            repository.deleteSwatch(colorEntity)
        }
    }

    fun onImageSelected(colorEntity: ColorEntity){
        _uiState.update {
            it.copy(
                showDialog = true,
                currentEntity = colorEntity
            )
        }
    }

    fun onDismiss(){
        _uiState.update {
            it.copy(
                showDialog = false
            )
        }
    }
}

data class PhotosUiState(
    val colorEntities : List<ColorEntity> = emptyList(),
    val currentEntity : ColorEntity = ColorEntity(emptyList(),""),
    val showDialog : Boolean = false
)