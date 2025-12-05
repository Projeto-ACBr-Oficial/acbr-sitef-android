package com.mjtech.fintesthub.android.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mjtech.fintesthub.android.ui.theme.Gray400
import com.mjtech.fintesthub.android.ui.theme.Typography

@Composable
fun FinSwitch(
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.headlineSmall
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = Typography.bodySmall,
                    color = Gray400
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview
@Composable
private fun FinSwitchPreview() {
    FinSwitch(title = "Title", isChecked = false) { }
}

@Preview
@Composable
private fun FinSwitchSubtitlePreview() {
    FinSwitch(title = "Title", subtitle = "Subtitle", isChecked = false) { }
}