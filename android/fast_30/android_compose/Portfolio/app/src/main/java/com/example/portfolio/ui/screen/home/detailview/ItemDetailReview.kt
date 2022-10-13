package com.example.portfolio.ui.screen.home.detailview

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.portfolio.di.modules.firebasemodule.FirebaseObject
import com.example.portfolio.model.uidatamodels.DisPlayReview
import com.example.portfolio.ui.common.EMPTY_IMAGE_URI
import com.example.portfolio.ui.common.HardwareName
import com.example.portfolio.ui.common.PermissionName
import com.example.portfolio.ui.common.StarRatingBar
import com.example.portfolio.ui.screen.util.executor
import com.example.portfolio.ui.screen.util.getCameraProvider
import com.example.portfolio.ui.screen.util.permission.PermissionCheck
import com.example.portfolio.ui.screen.util.permission.PermissionDialog
import com.example.portfolio.ui.screen.util.takePicture
import kotlinx.coroutines.launch
import java.io.File

const val DETAIL_REVIEW_VIEW = "리뷰"

@Composable
fun DetailReviewView(
    goReview: () -> Unit
) {
    val getReviewData = remember { mutableStateListOf<DisPlayReview>() }

    LaunchedEffect(true) {
        FirebaseObject.getTestReview {
            getReviewData.addAll(it)
        }
    }

    Column {
        Row(
            modifier = Modifier.clickable {
                goReview()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.DriveFileRenameOutline,
                contentDescription = "writeReview"
            )
            Text(text = "리뷰쓰기")
        }

        for ((reviewInfo, userInfo) in getReviewData) {
            DrawReview(
                userNickName = userInfo.name,
                userProfileImg = userInfo.profileImage,
                ratingValue = reviewInfo.rating.toFloat(),
                reviewDate = reviewInfo.date,
                contentText = reviewInfo.content
            )
        }
    }
}

@Composable
fun DrawReview(
    userNickName: String,
    userProfileImg: String,
    ratingValue: Float,
    reviewDate: String,
    takePictureData: String? = null,
    contentText: String,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.wrapContentHeight()
        ) {

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(userProfileImg)
                    .build(),
                contentDescription = "userProfileImage",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(100)),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(text = userNickName)
                StarRatingBar(rateCount = ratingValue)
            }
            Text(text = reviewDate)
        }
        if (takePictureData != null) {
            AsyncImage(model = takePictureData, contentDescription = "contextImage")
        }
        Text(text = contentText)
    }
}

@Composable
fun CameraView(
    upPress: () -> Unit
) {
    val context = LocalContext.current

    var cameraPermissionCheck by remember { mutableStateOf(false) }

    PermissionCheck(
        permissionName = PermissionName.CAMERA,
        hardwareName = HardwareName.CAMERA,
        grantedCheck = { state -> cameraPermissionCheck = state }
    )

    if (cameraPermissionCheck) {
        MainContent(upPress = upPress)
    } else {
        PermissionDialog(
            onDismissClickEvent = { },
            confirmButtonEvent = {
                val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                settingIntent.data = uri
                startActivity(context, settingIntent, null)
            },
            dismissButtonEvent = upPress
        )
    }
}

@Composable
fun CameraCapture(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onImageFile: (File) -> Unit = {}
) {
    val imageCaptureUseCase by remember {
        mutableStateOf(
            ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
        )
    }

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }

        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onUseCase = {
                previewUseCase = it
            }
        )

        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            onClick = {
                coroutineScope.launch {
                    imageCaptureUseCase.takePicture(context.executor).let {
                        onImageFile(it)
                    }
                }
            }
        ) {
            Text(text = "사진촬영")
        }

        LaunchedEffect(previewUseCase) {
            val cameraProvider = context.getCameraProvider()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
                )
            } catch (ex: Exception) {
                Log.d("CameraPreview", "Use case binding failed", ex)
            }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    onUseCase: (UseCase) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            val previewView = PreviewView(viewContext).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            onUseCase(Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            )
            previewView
        }
    )
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    upPress: () -> Unit
) {
    val emptyImageUri = Uri.parse("file://dev/null")
    var imageUri by remember { mutableStateOf(emptyImageUri) }
    var showGallerySelect by remember {
        mutableStateOf(false)
    }

    if (imageUri != emptyImageUri) {
        Box(modifier = modifier) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = imageUri,
                contentDescription = "capture image"
            )

            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    imageUri = emptyImageUri
                }
            ) {
                Text(text = "Remove image")
            }
        }
    } else {

        if (showGallerySelect) {
            GallerySelect(
                onImageUri = { uri ->
                    showGallerySelect = false
                    imageUri = uri
                },
                upPress = upPress
            )
        } else {
            Box(modifier = modifier) {
                CameraCapture(
                    modifier = modifier,
                    onImageFile = { file ->
                        imageUri = file.toUri()
                    }
                )

                Button(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(4.dp),
                    onClick = {
                        showGallerySelect = true
                    }
                ) {
                    Text("사진선택")
                }
            }
        }
    }
}

@Composable
fun GallerySelect(
    onImageUri: (Uri) -> Unit = {},
    upPress: () -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            onImageUri(uri ?: EMPTY_IMAGE_URI)
        }
    )
    var permissionCheck by remember { mutableStateOf(false) }

    @Composable
    fun LaunchGallery() {
        SideEffect {
            launcher.launch("image/*")
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        PermissionCheck(
            permissionName = PermissionName.MEDIA,
            hardwareName = HardwareName.MEDIA,
            grantedCheck = { state ->
                permissionCheck = state
            }
        )

        if (permissionCheck.not()) {
            PermissionDialog(
                onDismissClickEvent = { },
                confirmButtonEvent = {
                    val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    settingIntent.data = uri
                    startActivity(context, settingIntent, null)
                },
                dismissButtonEvent = upPress
            )
        } else {
            LaunchGallery()
        }
    } else {
        LaunchGallery()
    }
}