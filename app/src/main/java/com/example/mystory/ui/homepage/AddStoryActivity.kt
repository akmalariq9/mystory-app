package com.example.mystory.ui.homepage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mystory.R
import com.example.mystory.databinding.ActivityStoryAddBinding
import com.example.mystory.ui.main.MainActivity
import com.example.mystory.utils.reduceFileImage
import com.example.mystory.utils.rotateCamImage
import com.example.mystory.utils.rotateGalleryImage
import com.example.mystory.utils.rotateImage
import com.example.mystory.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityStoryAddBinding
	private val addStoryViewModel by viewModels<AddStoryViewModel>()
	private var getFile: File? = null
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private var lastLocation: Location? = null
	private var isUseLocation = false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityStoryAddBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		if (!permissionsCheck()) {
			ActivityCompat.requestPermissions(
				this,
				REQUIRED_PERMISSIONS,
				REQUEST_CODE_PERMISSIONS
			)
		}

		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		binding.cameraXButton.setOnClickListener { startCameraX() }
		binding.galleryButton.setOnClickListener { startGallery() }
		binding.buttonAdd.setOnClickListener { uploadImage() }
		binding.locationSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
			isUseLocation = isChecked
			getMyLocation()
		}
		binding.buttonAdd.setOnClickListener { uploadImage() }
	}

	private val requestPermissionLauncher =
		registerForActivityResult(
			ActivityResultContracts.RequestMultiplePermissions()
		) { permissions ->
			when {
				permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
					getMyLocation()
				}
				permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
					getMyLocation()
				}
				else -> {
					// No location access granted.
				}
			}
		}

	private fun checkPermission(permission: String): Boolean {
		return ContextCompat.checkSelfPermission(
			this,
			permission
		) == PackageManager.PERMISSION_GRANTED
	}

	private fun getMyLocation() {
		if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
			checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
		){
			fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
				if (location != null) {
					lastLocation = location
				} else {
					Toast.makeText(
						this,
						getString(R.string.locationUnknown),
						Toast.LENGTH_SHORT
					).show()
				}
			}
		} else {
			requestPermissionLauncher.launch(
				arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				)
			)
		}
	}
	
	private fun startGallery() {
		val intent = Intent()
		intent.action = Intent.ACTION_GET_CONTENT
		intent.type = "image/*"
		val chooser = Intent.createChooser(intent, "Choose a Picture")
		launcherIntentGallery.launch(chooser)
	}
	
	private fun startCameraX() {
		val intent = Intent(this, CameraActivity::class.java)
		launcherIntentCameraX.launch(intent)
	}
	
	private val launcherIntentCameraX = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) {
		if (it.resultCode == CAMERA_X_RESULT) {
			val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				it.data?.getSerializableExtra("picture", File::class.java)
			} else {
				@Suppress("DEPRECATION")
				it.data?.getSerializableExtra("picture")
			} as? File

			val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
			
			myFile?.let { file ->
				rotateCamImage(file, isBackCamera)
				getFile = file
				binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
			}
		}
	}
	
	private val launcherIntentGallery = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) { result ->
		if (result.resultCode == RESULT_OK) {
			val selectedImg = result.data?.data as Uri
			
			selectedImg.let { uri ->
				val myFile = uriToFile(uri, this@AddStoryActivity)
				rotateGalleryImage(myFile)
				getFile = myFile
				binding.previewImageView.setImageURI(uri)
			}
		}
	}

	private fun uploadImage() {
		if(getFile != null && binding.edAddDescription.text.isNotEmpty()) {
			val description = binding.edAddDescription.text.toString()
			val file = reduceFileImage(getFile as File)

			val requestDescription =
				description.toRequestBody("text/plain".toMediaType())
			val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())

			val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
				"photo",
				file.name,
				requestImageFile
			)

			if(!isUseLocation)
				addStoryViewModel.getUploadResponse(imageMultipart, requestDescription)
			else {
				val requestLat = lastLocation?.latitude?.toFloat().toString().toRequestBody("text/plain".toMediaType())
				val requestLon = lastLocation?.longitude?.toFloat().toString().toRequestBody("text/plain".toMediaType())

				addStoryViewModel.getUploadResponse(
					imageMultipart,
					requestDescription,
					requestLat,
					requestLon
				)
			}

			addStoryViewModel.upload.observe(this) {
				if(!it?.error!!) {
					Toast.makeText(this, getString(R.string.uploadSuccess), Toast.LENGTH_SHORT).show()
					val intent = Intent(this, MainActivity::class.java)
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
					startActivity(intent)

				} else {
					Toast.makeText(this, getString(R.string.uploadFailed), Toast.LENGTH_SHORT)
						.show()
				}
			}

			addStoryViewModel.errorMessage.observe(this) { errorMessage ->
				if(errorMessage != null) {
					Toast.makeText(this, getString(R.string.uploadFailed), Toast.LENGTH_SHORT)
						.show()
					addStoryViewModel.clearErrorMessage()
				}
			}

			addStoryViewModel.isLoading.observe(this) {
				showLoading(it)
			}
		}
	}

	private fun showLoading(isLoading: Boolean) {
		binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
	}
	
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == REQUEST_CODE_PERMISSIONS) {
			if (!permissionsCheck()) {
				Toast.makeText(
					this,
					getString(R.string.permissionDenied),
					Toast.LENGTH_SHORT
				).show()
				finish()
			}
		}
	}

	private fun permissionsCheck() = REQUIRED_PERMISSIONS.all {
		ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
	}
	
	companion object {
		const val CAMERA_X_RESULT = 200
		private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
		private const val REQUEST_CODE_PERMISSIONS = 10
	}
}