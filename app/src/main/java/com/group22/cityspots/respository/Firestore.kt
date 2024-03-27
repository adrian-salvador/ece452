package com.group22.cityspots.respository

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.group22.cityspots.model.Entry
import com.group22.cityspots.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Firestore {
    private val entriesCollectionRef = Firebase.firestore.collection("entries")
    private val usersCollectionRef = Firebase.firestore.collection("users")
    fun saveUser(user: User, context: Context) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = usersCollectionRef.whereEqualTo("userId", user.userId).get().await()

            if (querySnapshot.documents.isEmpty()) {
                usersCollectionRef.add(user).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Successfully Saved User", Toast.LENGTH_LONG).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "User already exists", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveEntry(entry: Entry, context: Context) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val documentReference = entriesCollectionRef.add(entry).await()
            val entryId = documentReference.id
            entriesCollectionRef.document(entryId).update("entryId", entryId).await()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully Saved Entry", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    suspend fun getEntriesByUserId(userId: String): List<Entry> = withContext(Dispatchers.IO) {
        try {
            println("User Id $userId")
            val querySnapshot = entriesCollectionRef
                .whereEqualTo("userId", userId)
                .orderBy("rating", Query.Direction.DESCENDING) // Sorting by rating in descending order
                .get().await()

            val list = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Entry::class.java)
            }

            println("Query Data $list")
            return@withContext list

        } catch (e: Exception) {
            println("Error fetching entries $e")
            emptyList<Entry>()
        }
    }

    suspend fun uploadPicture(imageData: ByteArray, userId: String, context: Context): String? {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/$userId/${System.currentTimeMillis()}.jpg")

        return try {
            val uploadTaskSnapshot = imageRef.putBytes(imageData).await()
            uploadTaskSnapshot.storage.downloadUrl.await().toString()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Upload Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
            null
        }
    }
}


