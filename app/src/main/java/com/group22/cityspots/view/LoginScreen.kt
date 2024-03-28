import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.group22.cityspots.R
import com.group22.cityspots.model.SignInState
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LoginScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.cityspots_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(300.dp)
                    )
                }


                Spacer(modifier = Modifier.size(48.dp))

                Box(Modifier.fillMaxWidth()){
                    Button(
                        onClick = onSignInClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonColors(
                            containerColor = Color(0xFF0182da),
                            disabledContainerColor = Color(0xFF0182da),
                            contentColor = Color(0xFF0182da),
                            disabledContentColor = Color(0xFF0182da)
                        )
                    ) {
                        Text(
                            text = "  Sign In / Register",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            textAlign = TextAlign.Center,
                            modifier =Modifier.fillMaxWidth(),
                        )
                    }
                    Row(modifier = Modifier.align(Alignment.CenterEnd)){
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(18.dp)
                                .background(color = Color.White, shape = CircleShape)

                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google_auth_logo),
                                contentDescription = "Google Auth Logo",
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                    }

                }

            }
        }
    }
}
