package com.agar.commander.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.agar.commander.ui.theme.AgarBlue
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarSurface
import com.agar.commander.ui.theme.AgarSurfaceRaised

@Composable
fun FieldMap(
    modifier: Modifier = Modifier,
    label: String = "Live field preview",
) {
    Box(
        modifier = modifier
            .background(AgarSurface, RoundedCornerShape(6.dp)),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val gridColor = AgarBorder.copy(alpha = 0.45f)
            val xStep = size.width / 12f
            val yStep = size.height / 8f

            for (index in 1 until 12) {
                drawLine(
                    color = gridColor,
                    start = Offset(index * xStep, 0f),
                    end = Offset(index * xStep, size.height),
                    strokeWidth = 1f,
                )
            }
            for (index in 1 until 8) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, index * yStep),
                    end = Offset(size.width, index * yStep),
                    strokeWidth = 1f,
                )
            }

            val route = Path().apply {
                moveTo(size.width * 0.12f, size.height * 0.75f)
                cubicTo(
                    size.width * 0.28f,
                    size.height * 0.62f,
                    size.width * 0.31f,
                    size.height * 0.31f,
                    size.width * 0.49f,
                    size.height * 0.34f,
                )
                cubicTo(
                    size.width * 0.66f,
                    size.height * 0.37f,
                    size.width * 0.70f,
                    size.height * 0.69f,
                    size.width * 0.88f,
                    size.height * 0.48f,
                )
            }
            drawPath(
                path = route,
                color = AgarBlue.copy(alpha = 0.35f),
                style = Stroke(width = 12f, cap = StrokeCap.Round),
            )
            drawPath(
                path = route,
                color = AgarBlue,
                style = Stroke(width = 3.5f, cap = StrokeCap.Round),
            )

            listOf(
                Offset(size.width * 0.12f, size.height * 0.75f),
                Offset(size.width * 0.49f, size.height * 0.34f),
                Offset(size.width * 0.88f, size.height * 0.48f),
            ).forEach { point ->
                drawCircle(AgarSurfaceRaised, radius = 10f, center = point)
                drawCircle(AgarGreen, radius = 6f, center = point)
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            color = AgarSurfaceRaised.copy(alpha = 0.92f),
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Surface(
            modifier = Modifier.align(Alignment.Center),
            color = AgarGreen,
            shape = RoundedCornerShape(5.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Navigation,
                contentDescription = "Robot position",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
