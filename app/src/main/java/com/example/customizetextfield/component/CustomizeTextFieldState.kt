package com.example.customizetextfield.component

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit

class CustomizeTextFieldState(vararg args: TextFeature<*>) {

    private var style = SpanStyle()
    private var lastStyle = style.copy()
    var shouldRecompose = mutableStateOf(false)

    var isInSelectionRange: Boolean = false
    private var features = mutableListOf<TextFeature<*>>()
    var selection = TextRange(0, 0)
        private set
    var lastSelection = TextRange(0, 0)
        private set
    var step: Int = 0
        private set

    init {
        for(arg in args) {
            features.add(arg)
        }
    }

    fun update(step: Int, selection: TextRange) {
        this.step = step
        this.selection = selection
        when {
            step > 0 -> addTexts()
            step < 0 -> removeTexts()
            selection.start == selection.end -> setValuesByIndex()
            else -> setValuesByRange()
        }
        lastSelection = TextRange(selection.start, selection.end)
    }

    private fun addTexts() {
        isInSelectionRange = false
        for(feature in features) {
            feature.updateAddingText(step, lastSelection.end)
        }
    }
    private fun removeTexts() {
        isInSelectionRange = false
        for(feature in features) {
            feature.updateRemovingText(lastSelection.end + step, step)

        }
        setValuesByIndex()
    }
    private fun setValuesByIndex() {
        isInSelectionRange = false
        for(feature in features) {
            feature.updateValueByIndex(selection.start)
        }
    }

    private fun setValuesByRange() {
        for(feature in features) {
            feature.updateValueByRange(selection.start, selection.end)
        }
        isInSelectionRange = true
    }

    private fun isAnyFeatureNotNullAtIndex(index: Int): Boolean {
        for(feature in features) {
            if(feature.indexes.indexes[index] != null)
                return true
        }
        return false
    }
    fun<T> updateFeature(feature: TextFeature<T>) {
        if(isInSelectionRange) {
            feature.updateRangeBySelection(selection)
        }
    }

    private fun updateStyleWithValueAndFeatureType(arg: Any, type: TextFeatureType) {
        when(type) {
            TextFeatureType.COLOR -> style = style.copy(color = arg as Color)
            TextFeatureType.BACKGROUND -> style = style.copy(background = arg as Color)
            TextFeatureType.FONT_SIZE -> style = style.copy(fontSize = arg as TextUnit)
            TextFeatureType.FONT_WEIGHT -> style = style.copy(fontWeight = arg as FontWeight)
            TextFeatureType.FONT_FAMILY -> style = style.copy(fontFamily = arg as FontFamily)
            TextFeatureType.FONT_STYLE -> style = style.copy(fontStyle = arg as FontStyle)
            TextFeatureType.STYLE -> style = arg as SpanStyle
            TextFeatureType.TEXT_DECORATION -> style = style.copy(textDecoration = arg as TextDecoration)

            else -> {}
        }
    }


    private fun updateStyleWithIndex(index: Int) {
        for(feature in features) {
            var value = feature.indexes.indexes[index]
            if(value != null) {
                updateStyleWithValueAndFeatureType(value, feature.type)
            }
        }
    }

    fun buildAnnotatedStringWithText(text: String): AnnotatedString {
        val builder = AnnotatedString.Builder()
        var stop = false
        var end = 0
        var start = 0
        var emoji = false
        for(i in text.indices) {
            if(isAnyFeatureNotNullAtIndex(i)) {
                updateStyleWithIndex(i)
                if(i == 0) {
                    lastStyle = style.copy()
                }
                stop = true
            }
            // emoji must be checked manually because they take two characters, if some text style is applied like bold there are not displayed correctly
            if(emoji) {
                builder.withStyle(SpanStyle()) {
                    append(text[i])
                }

                emoji = false
                start = i + 1
                continue
            }
            if(isEmoji(text[i])) {
                end = i
                builder.withStyle(style = lastStyle) {
                    append(text.substring(start, end))
                }
                lastStyle = style.copy()
                builder.withStyle(SpanStyle()) {
                    append(text[i])
                }
                emoji = true
                continue
            }

            if(stop || i + 1 == text.length) {
                end = i
                builder.withStyle(lastStyle) {
                    append(text.substring(start, end))
                }
                lastStyle = style.copy()
                start = i
                stop = false
            }
            if(i + 1 == text.length) {
                end = i + 1

                builder.withStyle(style = lastStyle) {
                    append(text.substring(start, end))
                }
            }
        }
        return builder.toAnnotatedString()
    }


    private fun isEmoji(char: Char): Boolean {
        return char.category == CharCategory.SURROGATE  // Enclosed Ideographic Supplement
    }
}

