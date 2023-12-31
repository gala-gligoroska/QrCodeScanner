package com.example.qrcodescanner.db

import com.example.qrcodescanner.db.entities.QrResult

interface DbHelperI {

    fun insertQRResult(result: String): Int

    fun getQRResult(id: Int): QrResult

    fun addToFavourite(id: Int): Int

    fun removeFromFavourite(id: Int): Int

    fun deleteQrResult(id: Int): Int

    fun getAllQRScannedResult(): List<QrResult>

    fun getAllFavouriteQRScannedResult(): List<QrResult>

    fun deleteAllQRScannedResult()

    fun deleteAllFavouriteQRScannedResult()
}

