package eu.brrm.oblivio.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isFinite
import androidx.compose.ui.unit.sp
import kotlin.math.min
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.brrm.oblivio.ui.theme.BrandBronze
import eu.brrm.oblivio.ui.theme.BrandCharcoal
import eu.brrm.oblivio.ui.theme.BrandCopper
import eu.brrm.oblivio.ui.theme.BrandIvory
import eu.brrm.oblivio.ui.theme.BrandMint
import eu.brrm.oblivio.ui.theme.BrandRedShadow
import eu.brrm.oblivio.ui.theme.DarkBackground
import eu.brrm.oblivio.ui.theme.DarkHint
import eu.brrm.oblivio.ui.theme.OblivioLogoMarkGradientFrom
import eu.brrm.oblivio.ui.theme.OblivioLogoMarkGradientTo
import eu.brrm.oblivio.ui.theme.LightHint
import eu.brrm.oblivio.ui.theme.LocalOblivioInDarkTheme
import eu.brrm.oblivio.ui.theme.OblivioLogoWordmarkBaseStyle

/**
 * Reference proportions from the Oblivio wordmark spec (**H** = mark size = cap height; the mark is the first “O”).
 * - Ring stroke **0.15H**; mark-to-text **0.55H**; inter-character space **0.42H** (use **0.42em** at the chosen font size).
 */
private object OblivioLogoProportions {
    val MarkSize: Dp = 56.dp
    const val MarkStrokeToHeight: Float = 0.15f
    const val IconToTextGapToHeight: Float = 0.55f
    const val WordmarkLetterSpacingEm: Float = 0.42f
}

@Composable
fun OblivioBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val isAppDark = LocalOblivioInDarkTheme.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(320.dp),
        ) {
            drawArc(
                color = BrandMint.copy(alpha = if (isAppDark) 0.18f else 0.36f),
                startAngle = 220f,
                sweepAngle = 290f,
                useCenter = false,
                topLeft = Offset(-size.width * 0.35f, size.height * 0.1f),
                size = Size(size.width * 1.3f, size.height * 1.3f),
                style = Stroke(width = size.minDimension * 0.17f, cap = StrokeCap.Butt),
            )
            drawArc(
                brush = Brush.linearGradient(listOf(BrandCharcoal.copy(alpha = 0.12f), BrandRedShadow.copy(alpha = 0.35f))),
                startAngle = 220f,
                sweepAngle = 135f,
                useCenter = false,
                topLeft = Offset(-size.width * 0.35f, size.height * 0.1f),
                size = Size(size.width * 1.3f, size.height * 1.3f),
                style = Stroke(width = size.minDimension * 0.17f, cap = StrokeCap.Butt),
            )
        }
        content()
    }
}

@Composable
fun OblivioLogo(
    wordmarkText: String,
    modifier: Modifier = Modifier,
    includeWordmark: Boolean = true,
    /** Scales mark and wordmark together (same H). When width is limited, match this to the
     * layout (e.g. 0.7f with [Modifier.fillMaxWidth] 0.7f) so the icon and text stay proportional. */
    markScale: Float = 1f,
) {
    val scale = markScale.coerceIn(0.25f, 2f)
    val markSize = OblivioLogoProportions.MarkSize * scale
    val isAppDark = LocalOblivioInDarkTheme.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .defaultMinSize(
                minHeight = if (includeWordmark) markSize else 0.dp,
            ),
    ) {
        Canvas(modifier = Modifier.size(markSize)) {
            val stroke = size.minDimension * OblivioLogoProportions.MarkStrokeToHeight
            val full = Size(size.width, size.height)
            // Concentric circle paths: R_large = R_small + W (same stroke W). The inner arc’s oval
            // is inset by W on each side so the outer edge of the inner stroke = inner edge of the outer.
            val innerW = (size.width - 2f * stroke).coerceAtLeast(1f)
            val innerH = (size.height - 2f * stroke).coerceAtLeast(1f)
            val innerTopLeft = Offset(
                (size.width - innerW) * 0.5f,
                (size.height - innerH) * 0.5f,
            )
            val innerSize = Size(innerW, innerH)
            // Two 180° semicircles, junctions on the \ diagonal (135° and 315°; 0° = 3 o’clock).
            val markGradient = if (isAppDark) {
                // Vector `paint0_linear_919_4947` (134×134 artboard, userSpaceOnUse).
                val artboard = 134f
                val sx = size.width / artboard
                val sy = size.height / artboard
                Brush.linearGradient(
                    listOf(OblivioLogoMarkGradientFrom, OblivioLogoMarkGradientTo),
                    start = Offset(21.7456f * sx, 299.389f * sy),
                    end = Offset(115.075f * sx, 283.107f * sy),
                )
            } else {
                Brush.linearGradient(
                    listOf(BrandCharcoal, BrandRedShadow, BrandCopper, BrandBronze, BrandIvory),
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height),
                )
            }
            drawArc(
                brush = markGradient,
                startAngle = 315f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset.Zero,
                size = full,
                style = Stroke(width = stroke, cap = StrokeCap.Butt),
            )
            drawArc(
                brush = markGradient,
                startAngle = 135f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = innerTopLeft,
                size = innerSize,
                style = Stroke(width = stroke, cap = StrokeCap.Butt),
            )
        }
        if (includeWordmark) {
            val iconToText = markSize * OblivioLogoProportions.IconToTextGapToHeight
            Box(
                modifier = Modifier
                    .padding(start = iconToText)
                    .weight(1f)
                    .height(markSize),
            ) {
                OblivioWordmarkFittingText(
                    text = wordmarkText,
                    baseStyle = OblivioLogoWordmarkBaseStyle,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun OblivioWordmarkFittingText(
    text: String,
    baseStyle: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier) {
        val wPx = if (maxWidth.isFinite) {
            with(density) { maxWidth.roundToPx() }
        } else {
            3_200
        }
        val hPx = if (maxHeight.isFinite) {
            with(density) { maxHeight.roundToPx() }
        } else {
            200
        }
        val fitted = remember(
            text,
            wPx,
            hPx,
            baseStyle,
            textMeasurer,
        ) {
            findLargestFittingWordmarkStyle(
                textMeasurer = textMeasurer,
                text = text,
                base = baseStyle,
                maxWidthPx = wPx,
                maxHeightPx = hPx,
            )
        }
        // Intrinsic text size, vertically + horizontally laid out from start; avoid fillMax* on
        // Text (stretches the line and breaks vertical centering with the mark).
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = text,
                style = fitted,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
        }
    }
}

private fun findLargestFittingWordmarkStyle(
    textMeasurer: TextMeasurer,
    text: String,
    base: TextStyle,
    maxWidthPx: Int,
    maxHeightPx: Int,
): TextStyle {
    if (maxWidthPx < 1 || maxHeightPx < 1) {
        return base.copy(
            fontSize = 8.sp,
            lineHeight = 10.sp,
            letterSpacing = OblivioLogoProportions.WordmarkLetterSpacingEm.em,
        )
    }
    var lo = 2f
    var hi = min(maxWidthPx, maxHeightPx).toFloat().coerceAtMost(1_200f)
    if (hi < 8f) {
        return base.copy(
            fontSize = 8.sp,
            lineHeight = 9.sp,
            letterSpacing = OblivioLogoProportions.WordmarkLetterSpacingEm.em,
        )
    }
    var best = lo
    var iteration = 0
    var low = lo
    var high = hi
    while (iteration < 32 && low <= high) {
        iteration++
        val mid = (low + high) / 2f
        val st = base.copy(
            fontSize = mid.sp,
            lineHeight = (mid * 1.12f).sp,
            letterSpacing = OblivioLogoProportions.WordmarkLetterSpacingEm.em,
        )
        val r = textMeasurer.measure(
            text = AnnotatedString(text),
            style = st,
            overflow = TextOverflow.Clip,
            softWrap = false,
            maxLines = 1,
        )
        val fits = r.size.width <= maxWidthPx && r.size.height <= maxHeightPx
        if (fits) {
            best = mid
            low = mid + 0.125f
        } else {
            high = mid - 0.125f
        }
    }
    return base.copy(
        fontSize = best.sp,
        lineHeight = (best * 1.12f).sp,
        letterSpacing = OblivioLogoProportions.WordmarkLetterSpacingEm.em,
    )
}

@Composable
fun OblivioPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isAppDark = LocalOblivioInDarkTheme.current
    val containerBrush = if (isAppDark) {
        Brush.horizontalGradient(listOf(BrandBronze, BrandIvory))
    } else {
        Brush.horizontalGradient(listOf(BrandCopper, BrandCharcoal))
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerBrush)
            .height(48.dp),
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = if (isAppDark) DarkBackground else Color.White,
            ),
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun OblivioSecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun OblivioTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val isAppDark = LocalOblivioInDarkTheme.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        label = { Text(text = label, style = MaterialTheme.typography.labelMedium) },
        placeholder = {
            Text(
                text = label,
                color = if (isAppDark) DarkHint else LightHint,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedBorderColor = if (isAppDark) BrandBronze else BrandCopper,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
        ),
    )
}
