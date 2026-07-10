package com.example.expense_tracker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tarjeta con efecto claymorfismo: sombra suave coloreada + brillo superior.
 * Simula el aspecto de "plastilina" característico de este estilo.
 */
@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    cornerRadius: Dp = 28.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Column(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = ClayShadowDark,
                spotColor = ClayShadowDark
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        Color.Transparent
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}

/**
 * Botón con el mismo estilo claymorfismo, en tono azul principal.
 */
@Composable
fun ClayButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)

    Button(
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = BlueCrossPrimary,
            contentColor = Color.White
        ),
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = ClayShadowDark,
                spotColor = ClayShadowDark
            )
    ) {
        Text(text)
    }
}