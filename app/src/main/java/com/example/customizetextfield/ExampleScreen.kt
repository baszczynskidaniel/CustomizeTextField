package com.example.customizetextfield.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun ExampleScreen() {
    val color by remember {
        mutableStateOf(
            TextFeature<Color>(
            default = Color.White,
            type = TextFeatureType.COLOR,
            availableValues = mapOf("white" to Color.White, "red" to Color.Red, "blue" to Color.Blue )
            )
        )
    }
    val background by remember {
        mutableStateOf(TextFeature<Color>(
            default = Color.Transparent,
            type = TextFeatureType.BACKGROUND,
            availableValues = mapOf("transparent" to Color.Transparent, "red" to Color.Red, "blue" to Color.Blue )

        ))
    }
    var weight by remember {
        mutableStateOf(TextFeature<FontWeight>(
            default = FontWeight.Normal,
            type = TextFeatureType.FONT_WEIGHT,
            availableValues = mapOf("normal" to FontWeight.Normal, "bold" to FontWeight.Bold, "thin" to FontWeight.ExtraLight )
        ))
    }

    var fontSize by remember {
        mutableStateOf(TextFeature<TextUnit>(
            default = 25.sp,
            type = TextFeatureType.FONT_SIZE,
            availableValues = mapOf("10" to 10.sp, "15" to 15.sp, "20" to 20.sp, "25" to 25.sp, "30" to 30.sp, "40" to 40.sp, "50" to 50.sp)
        ))
    }

    var fontFamily by remember {
        mutableStateOf(TextFeature<FontFamily>(
            default = FontFamily.Default,
            type = TextFeatureType.FONT_FAMILY,
            availableValues = mapOf("default" to FontFamily.Default, "serif" to FontFamily.Serif, "monospace" to FontFamily.Monospace)
        ))
    }

    var fontStyle by remember {
        mutableStateOf(TextFeature<FontStyle>(
            default = FontStyle.Normal,
            type = TextFeatureType.FONT_STYLE,
            availableValues = mapOf("normal" to FontStyle.Normal, "italic" to FontStyle.Italic)
        ))
    }


    var textDecoration by remember {
        mutableStateOf(TextFeature<TextDecoration>(
            default = TextDecoration.None,
            type = TextFeatureType.TEXT_DECORATION,
            availableValues = mapOf("none" to TextDecoration.None, "underline" to TextDecoration.Underline, "line through" to TextDecoration.LineThrough, "both" to TextDecoration.combine(
                listOf(TextDecoration.Underline, TextDecoration.LineThrough)))
        ))
    }

    var text by remember {
        mutableStateOf(TextFieldValue(""))
    }

    var align by remember {
        mutableStateOf(TextAlign.Left)
    }

    var formatTextFieldState by remember {
        mutableStateOf(CustomizeTextFieldState( weight))//color, background, weight, fontSize, fontFamily, fontStyle, textDecoration))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
           // FeatureSelectionWithRadioButtons(title = "background", feature = background, textState = formatTextFieldState)
            FeatureSelectionWithRadioButtons(title = "weight", feature = weight, textState = formatTextFieldState)
           // FeatureSelectionWithRadioButtons(title = "color", feature = color, textState = formatTextFieldState)
            //FeatureSelectionWithRadioButtons(title = "font size", feature = fontSize, textState = formatTextFieldState)
            //FeatureSelectionWithRadioButtons(title = "font family", feature = fontFamily, textState = formatTextFieldState)
            //FeatureSelectionWithRadioButtons(title = "font style", feature = fontStyle, textState = formatTextFieldState)
            //FeatureSelectionWithRadioButtons(title = "text decoration", feature = textDecoration, textState = formatTextFieldState)
        }
        Divider(color = Color.White, thickness = 2.dp, modifier = Modifier.padding(vertical = 15.dp))
        CustomizeTextField(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(15.dp))
                .background(Color.DarkGray)
                .padding(15.dp)

            ,
            value = text,
            state = formatTextFieldState,
            onValueChange = {text = it},
        placeholder = {
            Text(
                "You can write something ... ",
                fontSize = 25.sp,
                color = Color(0x77FFFFFF),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(15.dp)
            )}
        )
    }
}



@Composable fun <T> FeatureSelectionWithRadioButtons(title: String, feature: TextFeature<T>, textState: CustomizeTextFieldState){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(BorderStroke(2.dp, Color.DarkGray), RoundedCornerShape(15.dp))
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, fontSize = 15.sp, color = Color.White)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for ((k, v) in feature.availableValues!!) {
                RadioButton(selected = feature.getValue() == v, onClick = {
                    feature.setValue(v)
                    if(textState.isInSelectionRange) {
                        feature.updateRangeBySelection(
                            textState.selection
                        )
                        textState.shouldRecompose.value = true


                    }

                })
                Text(text = k)
            }
        }
    }
}



