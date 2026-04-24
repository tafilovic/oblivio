package eu.brrm.oblivio.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.unit.sp
import eu.brrm.oblivio.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val poppins = GoogleFont("Poppins")

private val poppinsFamily = FontFamily(
    Font(googleFont = poppins, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = poppins, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = poppins, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = poppins, fontProvider = provider, weight = FontWeight.Bold),
)

private val dmSans = GoogleFont("DM Sans")

private val dmSansFamily = FontFamily(
    Font(googleFont = dmSans, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = dmSans, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = dmSans, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = dmSans, fontProvider = provider, weight = FontWeight.Bold),
)

/** Base typography (family + weight) for the logo wordmark; size is chosen to fill the wordmark area. */
val OblivioLogoWordmarkBaseStyle = TextStyle(
    fontFamily = dmSansFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
)

val OblivioTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 42.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
)

/** "My profile" title — DM Sans 36 sp (design). */
val OblivioProfileTitleTextStyle = TextStyle(
    fontFamily = dmSansFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 36.sp,
    lineHeight = 40.sp,
    letterSpacing = 0.sp,
)