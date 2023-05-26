package com.example.examen1
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class ListViewModel private constructor(id: Int?, private val itemDao: ItemDao) : ViewModel() {

    private var listItems = mutableStateListOf<Item>()
    val id : Int get() = id
    val items: List<Item> get() = listItems

    init {
        if (id != null) {
            viewModelScope.launch {
                listItems.addAll(itemDao.getItemsByUser(id))
            }
        }
    }

    fun getItem(idItem: Int): Item? {
        return listItems.find { it.id == idItem }
    }

    fun addItem(item: Item){
        viewModelScope.launch {
            itemDao.insert(item)
            listItems.add(item)
        }

    }

    companion object {
        @Volatile
        private var instance: ListViewModel? = null

        fun getInstance(id: Int?, itemDao: ItemDao): ListViewModel {
            return instance ?: synchronized(this) {
                instance ?: ListViewModel(id, itemDao).also { instance = it }
            }
        }
    }
}

