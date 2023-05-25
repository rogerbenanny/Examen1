package com.example.examen1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardItem(
    userDatabase: AppDatabase,
    funItem: (Item) -> Unit,
    item: Item
){
    val itemDao = userDatabase.itemDao()
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        elevation = 8.dp
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)) {
            Text(text = item.title)
            Text(text = item.description)
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                    onClick = {
                        funItem(item)

                    }
                ) {
                    Text(text = "Editar", color = Color.White)
                }

                Button(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    onClick = {
                        itemDao.deleteItem(item)
                    }
                ) {
                    Text(text = "Eliminar", color = Color.White)
                }
            }
        }
    }
}