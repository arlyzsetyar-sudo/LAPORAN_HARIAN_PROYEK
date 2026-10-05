package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ProgressPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getPhotosByProject(projectId: Long): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM photos WHERE projectId = :projectId AND workCategory = :category ORDER BY timestamp DESC")
    fun getPhotosByCategory(projectId: Long, category: String): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM photos WHERE projectId = :projectId AND locationName = :locationName ORDER BY timestamp DESC")
    fun getPhotosByLocation(projectId: Long, locationName: String): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM photos WHERE projectId = :projectId ORDER BY dateStr DESC, timestamp DESC")
    fun getPhotosGroupedByDate(projectId: Long): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoById(id: Long): ProgressPhoto?

    @Query("SELECT COUNT(*) FROM photos WHERE projectId = :projectId")
    fun getPhotoCount(projectId: Long): Flow<Int>

    @Query("SELECT COUNT(DISTINCT locationName) FROM photos WHERE projectId = :projectId")
    fun getActiveLocationCount(projectId: Long): Flow<Int>

    @Query("SELECT AVG(progress) FROM photos WHERE projectId = :projectId")
    fun getAverageProgress(projectId: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: ProgressPhoto): Long

    @Update
    suspend fun updatePhoto(photo: ProgressPhoto)

    @Delete
    suspend fun deletePhoto(photo: ProgressPhoto)
}
