package com.example.customizetextfield.component

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange

class TextFeature<T>(default: T, type: TextFeatureType, availableValues: Map<String, T>? = null) {
    /**
     * default value for TextFeature.
     */
    var default: T

    /**
     * Information about what style configuration this feature handle
     * The generic type T must be castable to type defined in type
     */
    val type: TextFeatureType

    var availableValues: Map<String, T>? = null

    private var value: MutableState<T> = mutableStateOf(default)
    var indexes = TextFeatureRange<T>(default)
        private set
    init {
        this.default = default
        this.type = type
        this.availableValues = availableValues
    }



    fun getValue(): T = value.value
    fun setValue(value: T) {
        this.value.value = value
    }
    fun updateAddingText(step: Int, index: Int) {
        indexes.addWithUpdate(index, value.value, step)
    }
    fun updateRemovingText(fromIndex: Int, step: Int) {
        indexes.updateIndexesFromIndexExcludedWithStep(fromIndex, step)
    }
    fun updateValueByIndex(index: Int) {

        value.value = indexes.getValueAtIndexExcluded(index)!!
    }
    fun updateValueByRange(start: Int, end: Int) {
        value.value = indexes.getValueInRange(start, end)!!
    }
    fun updateRangeBySelection(selection: TextRange) {
        indexes.setValueInRange(selection.start, selection.end, value.value)
    }
}


