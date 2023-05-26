package com.example.examen1



import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun ListScreen(navController: NavHostController, viewModel: ListViewModel) {
    val showDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (viewModel.items.isNotEmpty()) {
            ItemsList(viewModel.items, navController)
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(16.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        showDialog.value = true
                    },
                ) {
                    Icon(Icons.Filled.Add, "Localized description")
                }
                // Mostrar el diálogo si showDialog es true
                if (showDialog.value) {
                    MyDialog(onDismiss = { showDialog.value = false }, viewModel)
                }
            }
        } else {
            EmptyListMessage()
        }
    }
}
@Composable
fun ItemsList(
    attendees: List<Item>,
    navController: NavHostController
) {
    LazyColumn {
        items(attendees) { person ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 5.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Items(person,navController)
                }
            }
        }
    }

}

@Composable
fun Items(
    item: Item,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Title: ${item.title}")
            Text(text = "Description: ${item.description}")
            Text(text = "Image: ${item.archive}")


            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

                Button(onClick = {
                    navController.navigate("item/${item.id}")
                }) {
                    Text(text = "View")
                }
            }
        }
    }
}

@Composable
fun MyDialog(onDismiss: () -> Unit, viewModel: ListViewModel) {
    // Variables de estado para el título y la descripción
    var titleState by remember { mutableStateOf("") }
    var descriptionState by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Nuevo elemento") },
        text = {
            Column {
                TextField(
                    value = titleState,
                    onValueChange = { titleState = it },
                    label = { Text(text = "Título") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = descriptionState,
                    onValueChange = { descriptionState = it },
                    label = { Text(text = "Descripción") },
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newItem = Item("img5.jpg", titleState, descriptionState, 1)
                    viewModel.addItem(newItem)

                    onDismiss()
                }
            ) {
                Text(text = "Confirmar")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text(text = "Cancelar")
            }
        }
    )
}


@Composable
fun EmptyListMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No attendees registered")
    }
}
