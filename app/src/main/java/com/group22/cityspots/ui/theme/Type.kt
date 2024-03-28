package com.group22.cityspots.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.group22.cityspots.R
val WorkSans = FontFamily(
    Font(R.font.worksans_regular),
    Font(R.font.worksans_bold, FontWeight.Bold),
    Font(R.font.worksans_extrabold, FontWeight.ExtraBold),
    Font(R.font.worksans_medium, FontWeight.Medium),
    Font(R.font.worksans_thin, FontWeight.Thin)
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

