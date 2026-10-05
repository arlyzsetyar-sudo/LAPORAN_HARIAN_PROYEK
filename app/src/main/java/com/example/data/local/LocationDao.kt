package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations WHERE projectId = :projectId ORDER BY name ASC")
    fun getLocationsByProject(projectId: Long): Flow<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long

    @Delete
    suspend fun deleteLocation(location: LocationEntity)

    @Query("SELECT COUNT(*) FROM locations WHERE projectId = :projectId")
    fun getLocationCount(projectId: Long): Flow<Int>
}
