package com.example.examen1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardItem( viewModel: ListViewModel, id: Int? ){
    val item = id?.let { viewModel.getItem(it) }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (item != null) {
                Text(text = "Title: ${item.title}")
                Text(text = "Description: ${item.description}")
                Text(text = "Image: ${item.archive}")
            }


            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}