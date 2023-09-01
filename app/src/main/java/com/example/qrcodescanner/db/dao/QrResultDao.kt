package com.example.qrcodescanner.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.qrcodescanner.db.entities.QrResult

@Dao
interface QrResultDao {

    // Queries

    @Query("SELECT * FROM QrResult ORDER BY time DESC")
    fun getAllScannedResult(): List<QrResult>

    @Query("SELECT * FROM QrResult WHERE favourite = 1 ORDER BY time DESC")
    fun getAllFavouriteResult(): List<QrResult>

    @Query("SELECT * FROM QrResult WHERE id = :id")
    fun getQrResult(id: Int): QrResult

    @Query("SELECT * FROM QrResult WHERE result = :result ")
    fun checkIfQrResultExist(result: String): Int

    // Inserts

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQrResult(qrResult: QrResult): Long

    // Updates

    @Query("UPDATE QrResult SET favourite = 1 WHERE id = :id")
    fun addToFavourite(id: Int): Int

    @Query("UPDATE QrResult SET favourite = 0 WHERE id = :id")
    fun removeFromFavourite(id: Int): Int

    // Deletes

    @Query("DELETE FROM QrResult")
    fun deleteAllScannedResult()

    @Query("DELETE FROM QrResult WHERE favourite = 1")
    fun deleteAllFavouriteResult()

    @Query("DELETE FROM QrResult WHERE id = :id")
    fun deleteQrResult(id: Int): Int
}