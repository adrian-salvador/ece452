package com.group22.cityspots.view

import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagEditorFragment(
    tags: State<List<String>?>,
    addTag: (String) -> Unit,
    removeTag: (String) -> Unit
) {
    var newTag by remember { mutableStateOf("") }
    Column {
        TextField(
            value = newTag,
            onValueChange = {
                newTag = it.lowercase()
            },
            placeholder = {
                Text(
                    text = "Filter Tags...",
                    color = Color(0xFFb2c5ff),
                    style = TextStyle(fontSize = 16.sp),
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    if (newTag.isNotBlank()) {
                        addTag(newTag)
                        newTag = ""
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F8FE),
                unfocusedContainerColor = Color(0xFFF3F8FE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color(0xFF176FF2),
            ),
            shape = RoundedCornerShape(40.dp),
            textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (newTag.isNotBlank()) {
                        addTag(newTag)
                        newTag = ""
                    }
                }
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (tags.value?.isNotEmpty() == true){
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ){
                tags.value?.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFDBE8F9),
                        contentColor = Color(0xFF176FF2),
                        modifier = Modifier.clickable { removeTag(tag) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                start = 10.dp,
                                end = 5.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                        ) {
                            Text(
                                text = tag,
                                style = TextStyle(fontWeight = FontWeight.Bold),
                            )
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
