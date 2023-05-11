package com.Halza.Master.presentation.customcomponent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.ProgressIndicatorDefaults
import com.Halza.Master.presentation.customcomponent.ProgressIndicatorDefaults.BaseRotationAngle
import com.Halza.Master.presentation.customcomponent.ProgressIndicatorDefaults.CircularEasing
import com.Halza.Master.presentation.customcomponent.ProgressIndicatorDefaults.CircularIndicatorDiameter
import com.Halza.Master.presentation.customcomponent.ProgressIndicatorDefaults.HeadAndTailAnimationDuration
import com.Halza.Master.presentation.customcomponent.ProgressIndicatorDefaults.HeadAndTailDelayDuration
import com.Halza.Master.presentation.customcomponent.ProgressIndicatorDefaults.JumpRotationAngle
import com.Halza.Master.presentation.customcomponent.ProgressIndicatorDefaults.RotationAngleOffset
import com.Halza.Master.presentation.customcomponent.ProgressIndicatorDefaults.RotationDuration
import com.Halza.Master.presentation.customcomponent.ProgressIndicatorDefaults.RotationsPerCycle

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
//Custom CircularProgressIndicator for customize the Color of progress and control the Proprties as per the design give by PDT
@Composable
fun CustomCircularProgressIndicator(
    /* @FloatRange(fromInclusive = true, from = 0.0, toInclusive = true, to = 1.0) */
    progress: Float,
    modifier: Modifier = Modifier,
    startAngle: Float = 270f,
    endAngle: Float = startAngle,
    indicatorColor: Color = MaterialTheme.colors.primary,
    trackColor: Color = MaterialTheme.colors.onBackground
        .copy(alpha = ContentAlpha.disabled),
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }

    Canvas(
        modifier
            .progressSemantics(progress)
            .size(CircularIndicatorDiameter)
            .focusable()
    ) {
        val backgroundSweep = 360f - ((startAngle - endAngle) % 360 + 360) % 360
        val progressSweep = backgroundSweep * progress.coerceIn(0f..1f)
        // Draw a background
        drawCircularIndicator(
            startAngle,
            backgroundSweep,
            trackColor,
            stroke
        )

        // Draw a progress
        drawCircularIndicator2(
            startAngle,
            progressSweep,
            indicatorColor,
            stroke
        )
    }
}

/**
 * Indeterminate <a href="https://material.io/components/progress-indicators#circular-progress-indicators" class="external" target="_blank">Material Design circular progress indicator</a>.
 *
 * Indeterminate progress indicator expresses an unspecified wait time and spins indefinitely.
 *
 * [Progress Indicator doc](https://developer.android.com/training/wearables/components/progress-indicator)
 * ![Progress indicator image](https://developer.android.com/images/reference/androidx/compose/material/circular-progress-indicator.png)
 *
 * Example of indeterminate progress indicator:
 * @sample androidx.wear.compose.material.samples.IndeterminateCircularProgressIndicator
 *
 * @param modifier Modifier to be applied to the CircularProgressIndicator
 * @param startAngle The starting position of the progress arc,
 * measured clockwise in degrees (0 to 360) from the 3 o'clock position. For example, 0 and 360
 * represent 3 o'clock, 90 and 180 represent 6 o'clock and 9 o'clock respectively.
 * Default is 270 degrees (top of the screen)
 * @param indicatorColor The color of the progress indicator bar.
 * @param trackColor The color of the background progress track
 * @param strokeWidth The stroke width for the progress indicator.
 */
@Composable
public fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    startAngle: Float = 270f,
    indicatorColor: Color = MaterialTheme.colors.primary,
    trackColor: Color = MaterialTheme.colors.onBackground
        .copy(alpha = ContentAlpha.disabled),
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }

    val transition = rememberInfiniteTransition()
    // The current rotation around the circle, so we know where to start the rotation from
    val currentRotation by transition.animateValue(
        0,
        RotationsPerCycle,
        Int.VectorConverter,
        infiniteRepeatable(
            animation = tween(
                durationMillis = RotationDuration * RotationsPerCycle,
                easing = LinearEasing
            )
        )
    )
    // How far forward (degrees) the base point should be from the start point
    val baseRotation by transition.animateFloat(
        0f,
        BaseRotationAngle,
        infiniteRepeatable(
            animation = tween(
                durationMillis = RotationDuration,
                easing = LinearEasing
            )
        )
    )
    // How far forward (degrees) both the head and tail should be from the base point
    val endAngle by transition.animateFloat(
        0f,
        JumpRotationAngle,
        infiniteRepeatable(
            animation = keyframes {
                durationMillis = HeadAndTailAnimationDuration + HeadAndTailDelayDuration
                0f at 0 with CircularEasing
                JumpRotationAngle at HeadAndTailAnimationDuration
            }
        )
    )

    val startProgressAngle by transition.animateFloat(
        0f,
        JumpRotationAngle,
        infiniteRepeatable(
            animation = keyframes {
                durationMillis = HeadAndTailAnimationDuration + HeadAndTailDelayDuration
                0f at HeadAndTailDelayDuration with CircularEasing
                JumpRotationAngle at durationMillis
            }
        )
    )
    Box(
        modifier = modifier
            .progressSemantics()
            .size(CircularIndicatorDiameter)
            .focusable()
    ) {
        Canvas(
            modifier
                .progressSemantics()
                .size(CircularIndicatorDiameter)
                .focusable()
        ) {

            val currentRotationAngleOffset = (currentRotation * RotationAngleOffset) % 360f

            // How long a line to draw using the start angle as a reference point
            val sweep = abs(endAngle - startProgressAngle)

            // Offset by the constant offset and the per rotation offset
            val offset = (startAngle + currentRotationAngleOffset + baseRotation) % 360f
            drawCircularIndicator(0f, 360f, trackColor, stroke)
            drawIndeterminateCircularIndicator2(
                startProgressAngle + offset,
                sweep,
                indicatorColor,
                stroke
            )

        }
        Text(
            text = "50 %", color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    PaddingValues(
                        top = 3.dp,
                        start = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    )
                ),


            )
    }

}

/**
 * Contains the default values used for [CircularProgressIndicator].
 */
public object ProgressIndicatorDefaults {
    /**
     * Default stroke width for [CircularProgressIndicator]
     *
     * This can be customized with the `strokeWidth` parameter on [CircularProgressIndicator]
     */
    public val StrokeWidth = 4.dp

    /**
     * The default [AnimationSpec] that should be used when animating between progress in a
     * determinate progress indicator.
     */
    public val ProgressAnimationSpec = SpringSpec(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessVeryLow,
        // The default threshold is 0.01, or 1% of the overall progress range, which is quite
        // large and noticeable.
        visibilityThreshold = 1 / 1000f
    )

    // CircularProgressIndicator Material specs
    // Diameter of the indicator circle
    internal val CircularIndicatorDiameter = 40.dp

    // The animation comprises of 5 rotations around the circle forming a 5 pointed star.
    // After the 5th rotation, we are back at the beginning of the circle.
    internal const val RotationsPerCycle = 5

    // Each rotation is 1 and 1/3 seconds, but 1332ms divides more evenly
    internal const val RotationDuration = 1332

    // How far the base point moves around the circle, in degrees
    internal const val BaseRotationAngle = 286f

    // How far the head and tail should jump forward during one rotation past the base point,
    // in degrees
    internal const val JumpRotationAngle = 290f

    // Each rotation we want to offset the start position by this much, so we continue where
    // the previous rotation ended. This is the maximum angle covered during one rotation.
    internal const val RotationAngleOffset = (BaseRotationAngle + JumpRotationAngle) % 360f

    // The head animates for the first half of a rotation, then is static for the second half
    // The tail is static for the first half and then animates for the second half
    internal const val HeadAndTailAnimationDuration = (RotationDuration * 0.5).toInt()
    internal const val HeadAndTailDelayDuration = HeadAndTailAnimationDuration

    // The easing for the head and tail jump
    internal val CircularEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameter = min(size.width, size.height)
    val diameterOffset = stroke.width / 2
    val arcDimen = diameter - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(
            diameterOffset + (size.width - diameter) / 2,
            diameterOffset + (size.height - diameter) / 2
        ),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

private fun DrawScope.drawCircularIndicator2(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameter = min(size.width, size.height)
    val diameterOffset = stroke.width / 2
    val arcDimen = diameter - 2 * diameterOffset
    drawArc(
        brush = Brush.verticalGradient(
            // in this color we are specifying a gradient
            // with the list of the colors.
            listOf(
                // on below line we are adding two colors.
                Color(0xFFFFA500),
                Color(0xFFffd700),

                Color(0xFFffd700),
            ),
        ),

        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(
            diameterOffset + (size.width - diameter) / 2,
            diameterOffset + (size.height - diameter) / 2
        ),
        size = Size(arcDimen, arcDimen),
        style = stroke,
    )
}

private fun DrawScope.drawIndeterminateCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // When the start and end angles are in the same place, we still want to draw a small sweep, so
    // the stroke caps get added on both ends and we draw the correct minimum length arc
    val adjustedSweep = max(sweep, 0.1f)

    drawCircularIndicator(startAngle, adjustedSweep, color, stroke)
}

private fun DrawScope.drawIndeterminateCircularIndicator2(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // When the start and end angles are in the same place, we still want to draw a small sweep, so
    // the stroke caps get added on both ends and we draw the correct minimum length arc
    val adjustedSweep = max(sweep, 0.1f)

    drawCircularIndicator2(startAngle, adjustedSweep, color, stroke)
}

private operator fun Size.minus(offset: Float): Size =
    Size(this.width - offset, this.height - offset)