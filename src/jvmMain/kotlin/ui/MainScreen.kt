package ui

import FitFileManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.FileDialog
import java.io.File
import javax.swing.JFrame

@Composable
fun MainScreen() {
    var selectedFile by remember { mutableStateOf<String?>(null) }
    var manufacturerId by remember { mutableStateOf("1") } // Default Garmin
    var productId by remember { mutableStateOf("0") }
    var statusMessage by remember { mutableStateOf("") }

    val fitFileManager = remember { FitFileManager() }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("FitFileTools - Device Changer", style = MaterialTheme.typography.headlineMedium)

            Button(onClick = {
                val fileDialog = FileDialog(null as JFrame?, "Select FIT File", FileDialog.LOAD)
                fileDialog.isVisible = true
                if (fileDialog.file != null) {
                    selectedFile = fileDialog.directory + fileDialog.file
                    statusMessage = "Selected: ${fileDialog.file}"
                }
            }) {
                Text("Select FIT File")
            }

            Text(text = selectedFile ?: "No file selected")

            OutlinedTextField(
                value = manufacturerId,
                onValueChange = { manufacturerId = it.filter { c -> c.isDigit() } },
                label = { Text("Manufacturer ID") }
            )

            OutlinedTextField(
                value = productId,
                onValueChange = { productId = it.filter { c -> c.isDigit() } },
                label = { Text("Product ID") }
            )

            Button(
                onClick = {
                    if (selectedFile != null) {
                        try {
                            val inputFile = File(selectedFile!!)
                            val outputPath = inputFile.parent + "/modified_" + inputFile.name
                            fitFileManager.changeDeviceType(
                                inputPath = selectedFile!!,
                                outputPath = outputPath,
                                newManufacturerId = manufacturerId.toIntOrNull() ?: 1,
                                newProductId = productId.toIntOrNull() ?: 0
                            )
                            statusMessage = "Success! Saved to $outputPath"
                        } catch (e: Exception) {
                            statusMessage = "Error: ${e.message}"
                            e.printStackTrace()
                        }
                    } else {
                        statusMessage = "Please select a file first"
                    }
                }
            ) {
                Text("Process fit File")
            }

            Text(statusMessage)
        }
    }
}
