package com.group22.cityspots.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group22.cityspots.model.User

@Composable
fun ProfileScreen(
    userLoginData: User?,
    onSignOut: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if(userLoginData?.profilePictureUrl != null){
            AsyncImage(
                model = userLoginData.profilePictureUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if(userLoginData?.username != null) {
            Text(
                text = userLoginData.username,
                textAlign = TextAlign.Center,
                fontSize =  36.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if(userLoginData?.email != null) {
            Text(
                text = userLoginData.email,
                textAlign = TextAlign.Center,
                fontSize =  24.sp,
                color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(onClick = onSignOut) { Text(text = "Sign Out") }
    }
}