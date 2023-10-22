package com.example.customizetextfield.component

import android.util.Log

class TextFeatureRange<T>(private val defaultValue: T){
    var indexes: MutableMap<Int, T> = mutableMapOf(0 to defaultValue)
        private set

    fun getValueInRange(start: Int, end: Int): T? {
        val keysInRange = indexes.keys.filter { it in start until end }
        if(keysInRange.isEmpty())
            return getValueAtIndexIncluded(start)

        if(keysInRange.size > 1)
            return defaultValue
        if(keysInRange[0] != start) {
             return defaultValue
        }
        return getValueAtIndexIncluded(start)
    }

    fun setValueInRange(start: Int, end: Int, value: T) {
        val keysInRange = indexes.keys.filter { it in start until end }
        val valueBeforeRange = getValueAtIndexExcluded(start) // avoid duplicate
        val lastRemovedKey = keysInRange.maxOrNull()
        val lastRemovedValue = if(lastRemovedKey != null) indexes[lastRemovedKey] else null

        for(key in keysInRange) {
            if(key != 0)
                indexes.remove(key)
        }
        if(keysInRange.isEmpty()) {
            if(valueBeforeRange != value)
                if(indexes[end] == null && valueBeforeRange != null)
                    indexes[end] = valueBeforeRange
        }
        if(valueBeforeRange != value)
            indexes[start] = value
        if(shouldAddLastRemovedKey(lastRemovedKey, lastRemovedValue, end))
            if(indexes[end] == null)
                indexes[end] = lastRemovedValue!!
    }

    private fun getValueAtIndexIncluded(index: Int): T? {
        val keysSmallerThanIndex = indexes.keys.filter { it <= index }
        val maxKey = keysSmallerThanIndex.maxOrNull() ?: return indexes[0]

        return indexes[maxKey]!!
    }
    fun getValueAtIndexExcluded(index: Int): T? {
        return getValueAtIndexIncluded(index - 1)
    }
    private fun getKeyBehindIndexIncluded(index: Int): Int? {
        val keysHigherThanIndex = indexes.keys.filter { it >= index }
        return keysHigherThanIndex.minOrNull()
    }

    private fun getKeyBehindIndexExcluded(index: Int): Int? {
        return getKeyBehindIndexIncluded(index + 1)
    }

    fun addWithUpdate(index: Int, value: T, step: Int) {

        if(step <= 0)
            return

        val valueAtIndex = getValueAtIndexIncluded(index)
        updateIndexesFromIndexExcludedWithStep(index - 1, step)
        add(index, value)
        add(index + step, valueAtIndex!!)
    }

    fun add(atIndex: Int, value: T) {
        if(atIndex == 0) {
            addAtIndexZero(value)
        } else {
           addAtPositiveIndex(atIndex, value)
        }
    }

    private fun addAtPositiveIndex(index: Int, value: T) {
        val valueBeforeAtIndex = getValueAtIndexExcluded(index)
        if(value == valueBeforeAtIndex)
            return
        indexes[index] = value
    }


    private fun addAtIndexZero(value: T) {
        indexes[0] = value
        val closestKey = getKeyBehindIndexExcluded(0)
        if(indexes[closestKey] == value)
            indexes.remove(closestKey)
    }


    private fun updateIndexesFromIndexWithPositiveStep(fromIndex: Int, step: Int) {
        val mapToChange = indexes.filter { it.key > fromIndex }//
        for(key in mapToChange.keys) {
            indexes.remove(key)
        }
        for ((key, _) in  mapToChange) {
            indexes[key + step] = mapToChange[key]!!
        }
    }

    private fun shouldAddLastRemovedKey(lastRemovedKey: Int?, lastRemovedValue: T?, index: Int): Boolean {
        if(lastRemovedKey == null || lastRemovedValue == null) return false
        val keyAfterIndexInTransformedMap = getKeyBehindIndexExcluded(index) ?: return false
        if(keyAfterIndexInTransformedMap == index + 1) return false
        if(getValueAtIndexIncluded(index) == lastRemovedValue) return false
        return true
    }


    private fun updateIndexesFromIndexWithNegativeStep(fromIndex: Int, step: Int) {
        val mapToChange = indexes.filter { it.key > fromIndex }
        var lastRemovedKey: Int? = null
        var lastRemovedValue: T? = null
        for(key in mapToChange.keys) {
            indexes.remove(key)
        }
        for ((key, value) in  mapToChange) {
            if(key + step <= fromIndex) {
                lastRemovedKey = key
                lastRemovedValue = value
                continue
            }
            indexes[key + step] = mapToChange[key]!!
        }
        if(shouldAddLastRemovedKey(lastRemovedKey, lastRemovedValue, fromIndex))
            indexes[fromIndex] = lastRemovedValue!!
        removeDuplicateKey(fromIndex)
    }

    fun updateIndexesFromIndexExcludedWithStep(fromIndex: Int, step: Int) {
        when {
            step > 0 -> updateIndexesFromIndexWithPositiveStep(fromIndex, step)
            step < 0 -> updateIndexesFromIndexWithNegativeStep(fromIndex, step)
            else -> return
        }
    }
    private fun removeDuplicateKey(fromIndex: Int) {
        val valueAtIndex = getValueAtIndexIncluded(fromIndex)
        var keysHigherThanIndex = indexes.keys.filter { it > fromIndex }
        keysHigherThanIndex = keysHigherThanIndex.sorted()
        var lastCheckedValue = valueAtIndex

        for(key in keysHigherThanIndex) {
            if(lastCheckedValue == indexes[key])
                indexes.remove(key)
            else
                lastCheckedValue = indexes[key]
        }
    }
}



