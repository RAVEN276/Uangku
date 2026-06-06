package com.example.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RupiahVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // Format originalText (which is pure digits) with Indonesian style (e.g. 15.000)
        val sb = StringBuilder()
        val len = originalText.length
        for (i in 0 until len) {
            sb.append(originalText[i])
            val remaining = len - 1 - i
            if (remaining > 0 && remaining % 3 == 0) {
                sb.append('.')
            }
        }
        val formatted = sb.toString()

        val origToTrans = IntArray(len + 1)
        val transToOrig = IntArray(formatted.length + 1)

        var origIdx = 0
        var transIdx = 0

        while (origIdx < len) {
            origToTrans[origIdx] = transIdx
            transToOrig[transIdx] = origIdx

            transIdx++
            val remaining = len - 1 - origIdx
            if (remaining > 0 && remaining % 3 == 0) {
                transToOrig[transIdx] = origIdx + 1
                transIdx++ // skip the dot
            }
            origIdx++
        }
        origToTrans[len] = transIdx
        transToOrig[transIdx] = len

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, len)
                return origToTrans[safeOffset]
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, formatted.length)
                return transToOrig[safeOffset]
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
