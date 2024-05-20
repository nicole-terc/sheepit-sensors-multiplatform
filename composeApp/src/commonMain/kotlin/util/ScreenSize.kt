package util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset

data class ScreenSize(
    val width: Dp,
    val height: Dp,
    val widthPx: Int,
    val heightPx: Int
) {
    val middlePoint: DpOffset = DpOffset(width / 2, height / 2)
    val halfSize: Offset = Offset(widthPx / 2f, heightPx / 2f)
}