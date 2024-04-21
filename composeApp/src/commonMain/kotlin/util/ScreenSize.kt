package util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset

data class ScreenSize(
    val width: Dp,
    val height: Dp,
    val widthPx: Int,
    val heightPx: Int
) {
    val middlePoint: DpOffset = DpOffset(width / 2, height / 2)
}