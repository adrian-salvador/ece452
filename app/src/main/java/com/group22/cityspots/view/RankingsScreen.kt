package com.group22.cityspots.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.group22.cityspots.viewmodel.RankingScreenViewModel
import com.group22.cityspots.viewmodel.RankingScreenViewModelFactory
import com.group22.cityspots.viewmodel.UserViewModel

@Composable
fun RankingScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.userLiveData.observeAsState()
    val rankingScreenViewModel: RankingScreenViewModel = viewModel(
        factory = RankingScreenViewModelFactory(user!!.userId)
    )
    val entries by rankingScreenViewModel.entriesLiveData.observeAsState()
    val tags = rankingScreenViewModel.tags.observeAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            TagEditorFragment(
                tags = tags,
                addTag = { tag -> rankingScreenViewModel.addTag(tag) },
                removeTag = { tag -> rankingScreenViewModel.removeTag(tag) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            val chunkedEntries = entries?.chunked(2) ?: listOf()
            chunkedEntries.forEachIndexed { rowIndex, rowEntries ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    rowEntries.forEachIndexed { columnIndex, entry ->
                        val index = rowIndex * 2 + columnIndex
                        EntryCardFragment(navController = navController, entry = entry, index = index, height = 200.dp, modifier = Modifier.weight(1f) )
                        if (rowEntries.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}