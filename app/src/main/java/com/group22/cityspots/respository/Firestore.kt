package com.group22.cityspots.respository

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.group22.cityspots.model.Entry
import com.group22.cityspots.model.Trip
import com.group22.cityspots.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Firestore {
    private val entriesCollectionRef = Firebase.firestore.collection("entries")
    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val tripsCollectionRef = Firebase.firestore.collection("trips")
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
            if (!entry.entryId.isNullOrEmpty()) {
                entriesCollectionRef.document(entry.entryId).set(entry).await()
            } else {
                val documentReference = entriesCollectionRef.add(entry).await()
                val entryId = documentReference.id
                entriesCollectionRef.document(entryId).update("entryId", entryId).await()
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully Saved Entry", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun deleteEntry(entry: Entry, context: Context) = CoroutineScope(Dispatchers.IO).launch {
        try {
            entriesCollectionRef.document(entry.entryId!!)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Successfully Deleted Entry", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveTrip(trip: Trip, context: Context) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val documentReference = tripsCollectionRef.add(trip).await()
            val tripId = documentReference.id
            tripsCollectionRef.document(tripId).update("tripId", tripId).await()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully Saved Trip", Toast.LENGTH_LONG).show()
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

    suspend fun getTripsByUserId(userId: String): List<Trip> = withContext(Dispatchers.IO) {
        try {
            println("User Id $userId")
            val querySnapshot = tripsCollectionRef
                .whereEqualTo("userId", userId)
                .orderBy("title", Query.Direction.ASCENDING) // Sorting by title in ascending order
                .get().await()

            val list = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Trip::class.java)
            }

            println("Query Data $list")
            return@withContext list

        } catch (e: Exception) {
            println("Error fetching trips $e")
            emptyList<Trip>()
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

    suspend fun deletePicture(imageUrl: String, context: Context): Boolean {
        val storageRef = Firebase.storage.reference

        return try {
            val imageRef = storageRef.storage.getReferenceFromUrl(imageUrl)
            imageRef.delete().await()
            true
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {

                Toast.makeText(context, "Deletion Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
            false
        }
    }
}


