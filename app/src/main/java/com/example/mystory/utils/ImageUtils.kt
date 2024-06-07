package com.example.mystory.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import com.example.mystory.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
private const val MAXIMAL_SIZE = 1000000

val timeStamp: String = SimpleDateFormat(
	FILENAME_FORMAT,
	Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
	val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
	return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun createFile(application: Application): File {
	val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
		File(it, application.resources.getString(R.string.appName)).apply { mkdirs() }
	}
	
	val outputDirectory = if (
		mediaDir != null && mediaDir.exists()
	) mediaDir else application.filesDir
	
	return File(outputDirectory, "$timeStamp.jpg")
}

fun rotateCamImage(file: File, isBackCamera: Boolean = false) {
	val matrix = Matrix()
	val bitmap = BitmapFactory.decodeFile(file.path)
	val rotation = if (isBackCamera) 90f else -90f
	matrix.postRotate(rotation)
	if (!isBackCamera) {
		matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
	}
	val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
	result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap {
	val orientation = ExifInterface(file).getAttributeInt(
		ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
	)
	return when (orientation) {
		ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
		ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
		ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
		ExifInterface.ORIENTATION_NORMAL -> this
		else -> this
	}
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
	val matrix = Matrix()
	matrix.postRotate(angle)
	return Bitmap.createBitmap(
		source, 0, 0, source.width, source.height, matrix, true
	)
}

fun rotateGalleryImage(file: File, isBackCamera: Boolean = false) {
	val bitmap = BitmapFactory.decodeFile(file.path)
	val rotatedBitmap = bitmap.getRotatedBitmap(file)

	rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
}

fun uriToFile(imageUri: Uri, context: Context): File {
	val myFile = createCustomTempFile(context)
	val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
	val outputStream = FileOutputStream(myFile)
	val buffer = ByteArray(1024)
	var length: Int
	while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
	outputStream.close()
	inputStream.close()
	return myFile
}

fun reduceFileImage(file: File): File {
	val bitmap = BitmapFactory.decodeFile(file.path)
	var compressQuality = 100
	var streamLength: Int
	do {
		val bmpStream = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
		val bmpPicByteArray = bmpStream.toByteArray()
		streamLength = bmpPicByteArray.size
		compressQuality -= 5
	} while (streamLength > MAXIMAL_SIZE)
	bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
	return file
}