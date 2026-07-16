package tech.jitendhull.torrnado.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.domain.model.TorrentItem
import tech.jitendhull.torrnado.domain.repository.TorrentRepository
import javax.inject.Inject

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val results: List<TorrentItem>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: TorrentRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _category = MutableStateFlow(TorrentCategory.GENERAL)
    val category: StateFlow<TorrentCategory> = _category.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun updateCategory(newCategory: TorrentCategory) {
        _category.value = newCategory
        // Automatically re-trigger search if query is not empty
        if (_query.value.isNotBlank()) {
            search()
        }
    }

    fun search() {
        val currentQuery = _query.value
        val currentCategory = _category.value
        if (currentQuery.isBlank()) return

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                val list = repository.searchTorrents(currentQuery, currentCategory)
                _uiState.value = SearchUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }
}