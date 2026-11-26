package mx.edu.uttt.uniplan.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import mx.edu.uttt.uniplan.R

@Composable
fun UniPlanTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = colorResource(R.color.md_primary),
        onPrimary = colorResource(R.color.md_on_primary),
        primaryContainer = colorResource(R.color.md_primary_container),
        onPrimaryContainer = colorResource(R.color.md_on_primary_container),
        secondary = colorResource(R.color.md_secondary),
        onSecondary = colorResource(R.color.md_on_secondary),
        secondaryContainer = colorResource(R.color.md_secondary_container),
        onSecondaryContainer = colorResource(R.color.md_on_secondary_container),
        background = colorResource(R.color.md_background),
        onBackground = colorResource(R.color.md_on_background),
        surface = colorResource(R.color.md_surface),
        onSurface = colorResource(R.color.md_on_surface),
        surfaceVariant = colorResource(R.color.md_surface_variant),
        onSurfaceVariant = colorResource(R.color.md_on_surface_variant),
        surfaceContainerLow = colorResource(R.color.md_surface_container_low),
        surfaceContainer = colorResource(R.color.md_surface_container),
        surfaceContainerHighest = colorResource(R.color.md_surface_container_highest),
        outline = colorResource(R.color.md_outline),
        outlineVariant = colorResource(R.color.md_outline_variant),
        error = colorResource(R.color.md_error),
        onError = colorResource(R.color.md_on_error),
        errorContainer = colorResource(R.color.md_error_container),
        onErrorContainer = colorResource(R.color.md_on_error_container)
    )
    val cardCorner = dimensionResource(R.dimen.card_corner)
    val shapes = Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(cardCorner),
        large = RoundedCornerShape(16.dp)
    )
    // Tipografía: la predeterminada de Material 3 usa Roboto (Title Large, Body Medium, Label Large...)
    MaterialTheme(colorScheme = colorScheme, shapes = shapes, content = content)
}
