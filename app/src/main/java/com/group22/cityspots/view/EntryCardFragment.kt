package com.group22.cityspots.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.group22.cityspots.model.Entry

@Composable
fun EntryCardFragment(navController: NavController?, entry: Entry, index:Int?, height:Dp, modifier: Modifier = Modifier){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xffd5d4d7))
            .clickable {
                navController?.navigate("entry/${entry.entryId}")
            }
    ) {
        if (entry.pictures?.isNotEmpty() == true) {
            AsyncImage(
                model = entry.pictures.first(),
                contentDescription = "Image of entry",
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.FillBounds
            )
        } else {
            Icon(
                Icons.Filled.Place,
                contentDescription = "No image available",
                modifier = Modifier.size(50.dp),
                tint = Color.Gray
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            if (index != null) {
                Text(
                    text = "${index + 1}.",
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            RoundedCornerShape(15.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = entry.title,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(15.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(15.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "No image available",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xfff8d675)
                )
                Text(
                    text = String.format("%.2f", entry.rating),
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}