package com.mjtech.domain.print.repository

import com.mjtech.domain.common.Result
import com.mjtech.domain.print.model.ImageData
import com.mjtech.domain.print.model.TextPrint
import kotlinx.coroutines.flow.Flow

interface PrintRepository {

    fun printSimpleText(textPrint: TextPrint): Flow<Result<Unit>>

    fun printText(linesText: List<TextPrint>): Flow<Result<Unit>>

    fun printQrCode(text: String): Flow<Result<Unit>>

    fun printBitmap(imageData: ImageData): Flow<Result<Unit>>
}