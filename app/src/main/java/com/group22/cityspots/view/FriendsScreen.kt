package com.group22.cityspots.view

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.group22.cityspots.model.Friends
import com.group22.cityspots.model.User
import com.group22.cityspots.viewmodel.FriendsViewModel
import com.group22.cityspots.viewmodel.FriendsViewModelFactory
import com.group22.cityspots.viewmodel.UserViewModel

@Composable
fun FriendsScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.userLiveData.observeAsState()
    val friendsViewModel: FriendsViewModel = viewModel(
        factory = FriendsViewModelFactory(user!!.userId)
    )
    val friendUser by friendsViewModel.friendUser.observeAsState()
    val friends by friendsViewModel.friendsLiveData.observeAsState()

    var showFriends by remember { mutableStateOf(true) }
    var showAddFriend by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .padding(18.dp)
//                    .verticalScroll(rememberScrollState())
            ) {
                Row(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()) {
                    Button(
                        onClick = { showFriends = true },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "Friends",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(modifier = Modifier.width(25.dp))
                    Button(
                        onClick = { showFriends = false },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "Requests",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Button(
                    onClick = { showAddFriend = !showAddFriend },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = ButtonColors(
                        containerColor = Color(0xFF0182da),
                        disabledContainerColor = Color(0xFF0182da),
                        contentColor = Color(0xFF0182da),
                        disabledContentColor = Color(0xFF0182da)
                    )
                ) {
                    Text(
                        text = "Add New Friend",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (showAddFriend) {
                    AddFriendPopup(user!!, friendsViewModel) { showAddFriend = false }
                }

                if (showFriends) {
                    RenderFriends(friends, navController)
                } else{
                    RenderRequests(user!!, friendUser!!, friendsViewModel)
                }
            }

            }

            }
//            Text(
//                text = "Friends",
//                style = MaterialTheme.typography.titleMedium
//            )

    }

@Composable
fun RenderFriends(friends: List<User>?, navController: NavController) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        contentPadding = PaddingValues(8.dp),
//        verticalArrangement = Arrangement.spacedBy((-60).dp),
//        horizontalArrangement = Arrangement.spacedBy(5.dp),
//    ) {
//        friends?.forEach { user ->
//            item {
//                Column(
//                    modifier = Modifier
//                        .padding(1.dp)
//                        .height(250.dp)
//                        .width(250.dp)
//                        .clip(RoundedCornerShape(5))
//                        .shadow(elevation = 2.dp)
////                        .clickable
////                        {
////                            navController.navigate("entry/${entry.id}")
////                        }
//                        ) {
//                    Box(
//                        modifier = Modifier
//                            .height(190.dp)
//                            .width(190.dp)
//                    ) {
//                        AsyncImage(
//                            model = user.profilePictureUrl,
//                            contentDescription = "Image of entry",
//                            modifier = Modifier
//                                .padding(5.dp)
//                                .align(Alignment.Center)
//                                .clip(RoundedCornerShape(5)),
//                            contentScale = ContentScale.FillBounds
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(10.dp))
//                    Row {
//                        Spacer(modifier = Modifier.width(8.dp))
//                        user.username?.let { Text(text=it, textAlign = TextAlign.Center) }
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//            }
//        }
//    }
    Column(modifier = Modifier.padding(16.dp)) {
        val chunkedEntries = friends?.chunked(2) ?: listOf()
        chunkedEntries.forEachIndexed { rowIndex, rowEntries ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                rowEntries.forEachIndexed { columnIndex, user ->
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier
                            .height(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xffd5d4d7))
                            .clickable {
                                navController.navigate("friendsRanking/${user.userId}/${user.username}")
                            }
                    ) {
                        if (user.profilePictureUrl?.isNotEmpty() == true) {
                            AsyncImage(
                                model = user.profilePictureUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(150.dp),
                                contentScale = ContentScale.FillBounds
                            )
                        } else {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "No image available",
                                modifier = Modifier.size(150.dp),
                                tint = Color.Gray
                            )
                        }
                        user.username?.let {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                modifier = Modifier
                                    .background(
                                        Color.Gray.copy(alpha = 0.5f),
//                                        RoundedCornerShape(15.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 2.dp),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 3
                            )
                        }
                    }

                    if (rowEntries.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun RenderRequests(user: User, friendUser: Friends, friendsViewModel: FriendsViewModel) {

    val sendReqs = friendUser.sentRequests
    val recvReqs = friendUser.recvRequests
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Sent Requests",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(15.dp))
        DrawBorder()
        sendReqs?.forEach() { req ->
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = req)
            Spacer(modifier = Modifier.height(10.dp))
            DrawBorder()
        }
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Received Requests",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(15.dp))
        DrawBorder()
        recvReqs?.forEach() { req ->
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = req)
                IconButton(onClick = { friendsViewModel.modFriendReq(req,user, "accept", context) }) {
                    Icon(Icons.Default.Check, contentDescription = "Accept")
                }
                IconButton(onClick = { friendsViewModel.modFriendReq(req,user, "decline", context) }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Decline")
                }
            }
            DrawBorder()
        }
    }
}

@Composable
fun AddFriendPopup(user: User, friendsViewModel: FriendsViewModel, onClose: () -> Unit) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .padding(2.dp)
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter friend's email") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        friendsViewModel.addFriend(email, user, context)
                        onClose()
                    } else {
                        Toast.makeText(context, "Please add email", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Send Request")
            }
        }
    }
}

@Composable
private fun DrawBorder() {
    Canvas(modifier = Modifier.fillMaxWidth()) {
        drawLine(
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            color = Color.Black,
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Square
        )
    }
}