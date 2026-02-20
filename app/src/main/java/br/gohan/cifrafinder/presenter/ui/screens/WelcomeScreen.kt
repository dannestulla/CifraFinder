package br.gohan.cifrafinder.presenter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.ui.theme.Background
import br.gohan.cifrafinder.presenter.ui.theme.CifraFinderTheme
import br.gohan.cifrafinder.presenter.ui.theme.GradientBottom
import br.gohan.cifrafinder.presenter.ui.theme.GradientTop
import br.gohan.cifrafinder.presenter.ui.theme.Primary
import br.gohan.cifrafinder.presenter.ui.theme.Surface
import br.gohan.cifrafinder.presenter.ui.theme.TextPrimary
import br.gohan.cifrafinder.presenter.ui.theme.TextSecondary

@Composable
fun WelcomeScreen(
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GradientTop,
                        Background,
                        GradientBottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(30.dp),
                        ambientColor = Primary.copy(alpha = 0.3f),
                        spotColor = Primary.copy(alpha = 0.3f)
                    )
                    .background(
                        color = Surface,
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Primary
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = stringResource(id = R.string.login_greeting),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = stringResource(id = R.string.first_step_description),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Continue Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Primary.copy(alpha = 0.4f),
                        spotColor = Primary.copy(alpha = 0.4f)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.continue_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 390,
    heightDp = 844
)
@Composable
fun WelcomeScreenPreview() {
    CifraFinderTheme {
        WelcomeScreen { }
    }
}
