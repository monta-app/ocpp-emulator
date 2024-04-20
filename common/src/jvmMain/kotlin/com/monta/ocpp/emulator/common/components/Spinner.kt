package com.monta.ocpp.emulator.common.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> Spinner(
    modifier: Modifier = Modifier,
    label: String,
    value: T,
    values: List<T>,
    render: (T) -> String,
    onSelectionChanged: (selection: T) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        Column {
            OutlinedTextField(
                modifier = modifier,
                value = render(value),
                onValueChange = {
                },
                label = {
                    Text(text = label)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = null
                    )
                },
                readOnly = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                values.forEach { entry ->
                    DropdownMenuItem(
                        onClick = {
                            onSelectionChanged(entry)
                            expanded = false
                        },
                        content = {
                            Text(
                                text = render(entry),
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(
                    onClick = { expanded = !expanded }
                )
        )
    }
}

@Preview
@Composable
fun Spinner_Preview() {
    MaterialTheme {
        val entry1 = Pair("Key1", "Entry1")
        val entry2 = Pair("Key2", "Entry2")
        val entry3 = Pair("Key3", "Entry3")

        Spinner(
            label = "hello",
            value = entry1,
            values = listOf(entry1, entry2, entry3),
            render = { it.second },
            onSelectionChanged = {
                /* do something with selected */
            }
        )
    }
}
