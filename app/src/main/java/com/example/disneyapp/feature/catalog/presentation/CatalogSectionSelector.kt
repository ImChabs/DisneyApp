package com.example.disneyapp.feature.catalog.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.disneyapp.R
import com.example.disneyapp.ui.theme.DisneyColors

enum class CatalogSection(
    @param:StringRes val labelResId: Int,
) {
    Characters(R.string.catalog_section_characters),
    Films(R.string.catalog_section_films),
    Shows(R.string.catalog_section_shows),
    Parks(R.string.catalog_section_parks),
}

@Composable
fun CatalogSectionSelector(
    selectedSection: CatalogSection,
    onSectionClick: (CatalogSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedContainer = DisneyColors.VioletMuted.copy(alpha = 0.88f)
    val unselectedContainer = DisneyColors.Ink.copy(alpha = 0.54f)
    val selectedContent = DisneyColors.Gold
    val unselectedContent = Color.White.copy(alpha = 0.76f)
    val chipColors = FilterChipDefaults.elevatedFilterChipColors(
        containerColor = unselectedContainer,
        labelColor = unselectedContent,
        iconColor = unselectedContent,
        selectedContainerColor = selectedContainer,
        selectedLabelColor = selectedContent,
        selectedLeadingIconColor = selectedContent,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CatalogSection.entries.forEach { section ->
            val selected = section == selectedSection

            ElevatedFilterChip(
                selected = selected,
                onClick = {
                    if (!selected) {
                        onSectionClick(section)
                    }
                },
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(22.dp),
                colors = chipColors,
                elevation = FilterChipDefaults.elevatedFilterChipElevation(
                    elevation = 1.dp,
                    pressedElevation = 2.dp,
                    focusedElevation = 2.dp,
                    hoveredElevation = 2.dp,
                    draggedElevation = 3.dp,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = Color.White.copy(alpha = 0.18f),
                    selectedBorderColor = DisneyColors.Gold.copy(alpha = 0.46f),
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.dp,
                ),
                leadingIcon = if (selected) {
                    {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(
                                    color = selectedContent,
                                    shape = CircleShape,
                                ),
                        )
                    }
                } else {
                    null
                },
                label = {
                    Text(
                        text = stringResource(section.labelResId),
                        modifier = Modifier.padding(horizontal = 2.dp),
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}
