package com.example.colortracker.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch
import com.example.colortracker.domain.repository.PaletteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository : PaletteRepository
) : ViewModel(){

    private val _favoritesList = MutableStateFlow<List<FavoriteSwatch>>(emptyList())
    val favoritesList = _favoritesList.asStateFlow()

    init {
        getFavorites()
    }

    fun getFavorites(){
        viewModelScope.launch {
            repository.getFavorites().collect {
                _favoritesList.value = it
            }
        }
    }

    private fun addFavorites(favoriteSwatch: FavoriteSwatch){
        viewModelScope.launch {
            repository.addFavorites(favoriteSwatch)
        }
    }

    private fun removeFromFavorites(hex : String){
        viewModelScope.launch {
            repository.deleteFavorites(hex)
        }
    }

    fun onPress(favoriteSwatch: FavoriteSwatch){
        viewModelScope.launch {
            val hexList = _favoritesList.value.map { it.hex }
            if (hexList.contains(favoriteSwatch.hex)){
                removeFromFavorites(favoriteSwatch.hex)
            }else{
                addFavorites(favoriteSwatch)
            }
        }

    }

    fun isFavorite(sw : ColorSwatchInfo) : Boolean{
        val fs = FavoriteSwatch(
            hex = sw.hex,
            rgb = sw.rgb,
            population = sw.population,
            percentage = sw.percentage,
            titleTextColor = sw.titleTextColor,
            bodyTextColor = sw.bodyTextColor
        )
        return _favoritesList.value.contains(fs)
    }

    fun isFavorite(fs : FavoriteSwatch) : Boolean{
        return _favoritesList.value.contains(fs)
    }


}
