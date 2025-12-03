package com.mjtech.fintesthub.android.ui.common.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mjtech.fintesthub.android.R
import com.mjtech.fintesthub.android.ui.theme.Gray300
import com.mjtech.fintesthub.android.ui.theme.Gray400
import com.mjtech.fintesthub.android.ui.theme.MainColor
import com.mjtech.fintesthub.android.ui.theme.Typography

@Composable
fun FinButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    @DrawableRes iconRes: Int? = null,
    color: Color = MainColor,
    textColor: Color = Color.White,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .heightIn(min = 42.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        contentPadding =
            if (text == null && iconRes != null) PaddingValues(0.dp)
            else ButtonDefaults.ContentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = Gray300
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            text?.let {
                Text(
                    text = text,
                    style = Typography.labelLarge,
                    color = if (enabled) textColor else Gray400
                )
            }
            iconRes?.let {
                Icon(
                    painter = painterResource(id = iconRes),
                    tint = if (enabled) textColor else Gray400,
                    contentDescription = stringResource(R.string.button_icon)
                )
            }
        }
    }
}

@Preview
@Composable
private fun FinButtonPreview() {
    FinButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.pay),
        iconRes = R.drawable.ic_coin
    ) { }
}

@Preview
@Composable
private fun FinButtonNoIconPreview() {
    FinButton(
        text = stringResource(R.string.pay)
    ) { }
}

@Preview
@Composable
private fun FinButtonNoTextPreview() {
    FinButton(
        iconRes = R.drawable.ic_coin
    ) { }
}

@Preview
@Composable
private fun FinButtonDisabledPreview() {
    FinButton(
        text = stringResource(R.string.pay),
        enabled = false
    ) { }
}