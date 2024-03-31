package com.group22.cityspots.respository

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.group22.cityspots.model.Entry
import com.group22.cityspots.model.Friends
import com.group22.cityspots.model.User
import com.group22.cityspots.model.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Firestore {
    private val entriesCollectionRef = Firebase.firestore.collection("entries")
    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val tripsCollectionRef = Firebase.firestore.collection("trips")
    private val friendsCollectionRef = Firebase.firestore.collection("friends")

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

    fun sendFriendReq(email: String, user: User, context: Context) = CoroutineScope(Dispatchers.IO).launch {

        try {
            usersCollectionRef
                .whereEqualTo("email", email)
                .get().await()

            try {
                println("Email $email")
                val querySnapshot2 = friendsCollectionRef
                    .whereEqualTo("userId", email)
                    .get().await()
                val documentSnapshot = querySnapshot2.documents.first()
                friendsCollectionRef.document(documentSnapshot.id).update("recvRequests", FieldValue.arrayUnion(user.email)).await()

                val currUserQuery = friendsCollectionRef
                    .whereEqualTo("userId", user.email)
                    .get().await()
                val currUserDoc = currUserQuery.documents.first()
                friendsCollectionRef.document(currUserDoc.id).update("sentRequests", FieldValue.arrayUnion(email)).await()

            } catch (e: Exception) {
                val newFriend =  Friends(
                    userId = email,
                    friendIDs = mutableListOf(),
                    recvRequests = mutableListOf(user.email!!),
                    sentRequests = mutableListOf(),
                )
                val documentReference = friendsCollectionRef.add(newFriend).await()
                val friendID = documentReference.id
                friendsCollectionRef.document(friendID).update("friendID", friendID).await()

                val currUserQuery = friendsCollectionRef
                    .whereEqualTo("userId", user.email)
                    .get().await()
                val currUserDoc = currUserQuery.documents.first()
                friendsCollectionRef.document(currUserDoc.id).update("sentRequests", FieldValue.arrayUnion(email)).await()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun modFriendReq(email: String, user: User, action: Boolean, context: Context) = CoroutineScope(Dispatchers.IO).launch {

        try {
            println("Email $email")
            val querySnapshot2 = friendsCollectionRef
                .whereEqualTo("userId", email)
                .get().await()
            val documentSnapshot = querySnapshot2.documents.first()

            val currUserQuery = friendsCollectionRef
                .whereEqualTo("userId", user.email)
                .get().await()
            val currUserDoc = currUserQuery.documents.first()

            friendsCollectionRef.document(documentSnapshot.id).update("sentRequests", FieldValue.arrayRemove(user.email)).await()
            friendsCollectionRef.document(currUserDoc.id).update("recvRequests", FieldValue.arrayRemove(email)).await()


            if (action) {
                friendsCollectionRef.document(documentSnapshot.id)
                    .update("friendIDs", FieldValue.arrayUnion(user.email)).await()
                friendsCollectionRef.document(currUserDoc.id)
                    .update("friendIDs", FieldValue.arrayUnion(email)).await()
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

    suspend fun getFriendByUserId(userId: String): Friends? = withContext(Dispatchers.IO) {
        try {
            println("User Friend Id $userId")
            val querySnapshot = friendsCollectionRef
                .whereEqualTo("userId", userId)
                .get().await()

            val documentSnapshot = querySnapshot.documents.first()
            val friend = documentSnapshot.toObject(Friends::class.java)

            println("Query Data $friend")
            return@withContext friend

        } catch (e: Exception) {
            println("Error: $e")
            val newFriend =  Friends(
                userId = userId,
                friendIDs = mutableListOf(),
                recvRequests = mutableListOf(),
                sentRequests = mutableListOf(),
            )
            val documentReference = friendsCollectionRef.add(newFriend).await()
            val friendID = documentReference.id
            friendsCollectionRef.document(friendID).update("friendID", friendID).await()
            println(newFriend)
            return@withContext newFriend;
        }
    }

    suspend fun getFriendsByUserIds(userIds: MutableList<String>): List<User> = withContext(Dispatchers.IO) {
        try {

            val friendsList: MutableList<User> = mutableListOf()

            userIds.forEach { userId ->
                val querySnapshot = usersCollectionRef.whereEqualTo("email", userId).get().await()

                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents.first()
                    val user = documentSnapshot.toObject(User::class.java)
                    user?.let { friendsList.add(it) }
                }
            }

            println("Query Data $friendsList")
            return@withContext friendsList

        } catch (e: Exception) {
            println("Error fetching friends $e")
            emptyList<User>()
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


