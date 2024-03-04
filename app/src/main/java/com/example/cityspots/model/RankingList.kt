package com.example.cityspots.model

class RankingList(initialEntries: List<Entry> = listOf()) {
    private val entries = initialEntries.toMutableList()

    fun insert(rank: Int, entry: Entry) {
        val existingIndex = entries.indexOfFirst { it.id == entry.id }
        if (existingIndex != -1) {
            // Entry exists, update it
            entries[existingIndex] = entry

            // If the rank to insert is different from the existing index, move the entry
            if (rank != existingIndex) {
                val entryToMove = entries.removeAt(existingIndex)
                val adjustedRank = if (rank > existingIndex) rank - 1 else rank // Adjust rank if shifting left
                entries.add(adjustedRank.coerceIn(0, entries.size), entryToMove)
            }
        } else {
            // New entry, simply add it
            if (rank in 0..entries.size) {
                entries.add(rank, entry)
            } else {
                throw IndexOutOfBoundsException("Rank $rank out of bounds for insert operation.")
            }
        }
    }

    fun get(id: Int): Entry?{
        val index = entries.indexOfFirst { it.id == id }
        return if (index != -1) {
            entries[index]
        } else {
            null
        }
    }
    fun getByRank(rank: Int): Entry? {
        return if (rank >= 0 && rank < entries.size) {
            entries[rank]
        } else {
            null
        }
    }

    fun prevEntry(rank: Int): Entry? {
        return if (rank > 0 && rank <= entries.size) {
            entries[rank - 1]
        } else {
            null
        }
    }

    fun nextEntry(rank: Int): Entry? {
        return if (rank >= 0 && rank < entries.size - 1) {
            entries[rank + 1]
        } else {
            null
        }
    }

    fun length(): Int = entries.size

    fun isEmpty(): Boolean = entries.isEmpty()

    fun remove(rank: Int): Entry? {
        return if (rank >= 0 && rank < entries.size) {
            entries.removeAt(rank)
        } else {
            null
        }
    }

    fun clear() {
        entries.clear()
    }

    fun getRankById(entryId: Int): Int? {
        val index = entries.indexOfFirst { it.id == entryId }
        return if (index != -1) index else null
    }


    fun updateByRank(rank: Int, updatedEntry: Entry) {
        if (rank >= 0 && rank < entries.size) {
            entries[rank] = updatedEntry
        }else {
            throw IndexOutOfBoundsException("Rank $rank out of bounds for update operation.")
        }
    }

    fun updateById(id: Int, updatedEntry: Entry) {
        val index = entries.indexOfFirst { it.id == id }
        if (index != -1) {
            entries[index] = updatedEntry
        } else {
            throw NoSuchElementException("No entry found with ID $id.")
        }
    }

    fun toList(): List<Entry> {
        return entries.toList()
    }

    fun clone(): RankingList {
        // Create a new list by copying each entry. This works because Entry is a data class.
        val clonedEntries = entries.map { it.copy() }
        return RankingList(clonedEntries)
    }
}
