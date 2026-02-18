package com.mjtech.fintesthub.android.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjtech.fintesthub.android.R
import com.mjtech.fintesthub.android.ui.theme.Gray300
import com.mjtech.fintesthub.android.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = koinViewModel(),
    onNavigate: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isReadyToNavigate) {
        if (uiState.isReadyToNavigate) {
            onNavigate()
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_credit_card),
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    contentDescription = stringResource(R.string.cd_app_logo)
                )
                Text(
                    stringResource(R.string.app_name),
                    style = Typography.headlineMedium.copy(fontSize = 22.sp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                )
                Text(
                    stringResource(R.string.demo),
                    style = Typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                    color = Gray300
                )
            }
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen { }
}