package com.example.customizetextfield.component

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CustomizeTextField(
    value: TextFieldValue,
    state: CustomizeTextFieldState,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() },
    placeholder: @Composable (() -> Unit)? = null,
    ) {

    Box {

        //force recomposition when apply changes that do not trigger onValueChange
        if(state.shouldRecompose.value) {
            state.shouldRecompose.value = false
        }
        BasicTextField(
            value = value,
            onValueChange = { it ->

                val step = it.text.length - value.text.length
                state.update(step, it.selection)
                Log.d("change", "tu")
                onValueChange(it)

            },

            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = StyleTextTransformation(state),
            onTextLayout = onTextLayout,
            cursorBrush = cursorBrush,
            decorationBox = decorationBox,
            interactionSource = interactionSource,
        )
        if (placeholder != null && value.text.isEmpty()) {
            placeholder()
        }

    }
}

class StyleTextTransformation(var state: CustomizeTextFieldState): VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            state.buildAnnotatedStringWithText(text.toString()),
            OffsetMapping.Identity
        )
    }
}