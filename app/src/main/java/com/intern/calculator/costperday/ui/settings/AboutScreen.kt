package com.intern.calculator.costperday.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.intern.calculator.costperday.ui.navigation.NavigationDestination
import com.intern.calculator.costperday.R
import com.intern.calculator.costperday.ui.components.MyTopAppBar
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


// Define the destination for the about screen
object AboutDestination : NavigationDestination {
    override val route = "about_app"
    override val titleRes = R.string.about_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = stringResource(AboutDestination.titleRes),
                navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack,
                navigationIconContentDescription = "Navigate back",
                actionIcon = null,
                actionIconContentDescription = null,
                onNavigationClick = navigateUp
            )
        },
    ) { padding ->
        // Retrieve the application context
        val context = LocalContext.current
        // Retrieve package information to display version and last update date
        val info = context.packageManager.getPackageInfo(
            context.packageName, 0
        )
        val versionName = info.versionName
        val versionDate = SimpleDateFormat
            .getDateInstance(DateFormat.LONG, context.resources.configuration.locale)
            .format(Date(info.lastUpdateTime))
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            ) {
                // Display the app icon
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Display the app name
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                // Display the app version
                Text(
                    text = (stringResource(id = R.string.about_version) + versionName),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                // Display the last update date
                Text(
                    text = (stringResource(id = R.string.about_time) + versionDate),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Button with link
            Button(
                onClick = {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AD-158/CalculateBudgetPerDayApplication"))
                    context.startActivity(browserIntent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.about_source_link),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
