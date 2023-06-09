package com.epf.MovieSearch.ui.qrcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epf.MovieSearch.R
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.epf.MovieSearch.databinding.PreviewViewBinding
import java.util.concurrent.Executors
import androidx.core.content.ContextCompat
import androidx.camera.core.Preview
import androidx.camera.core.*
import android.widget.Toast
import android.util.Log
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.epf.MovieSearch.ui.MovieDetailFragment


private const val CAMERA_PERMISSION_REQUEST_CODE = 1

class QRCodeFragment : Fragment() {
    private lateinit var binding: PreviewViewBinding
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var barcodeScanner: BarcodeScanner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PreviewViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasCameraPermission()) {
            bindCameraUseCases()
        } else {
            requestPermission()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bindCameraUseCases()
        } else {
            Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_LONG).show()
        }
    }

    private fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())


        cameraProviderFuture.addListener({

            cameraProvider = cameraProviderFuture.get()

            val previewUseCase = Preview.Builder().build().apply {
                setSurfaceProvider(binding.cameraView.surfaceProvider)
            }

            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_PDF417
            ).build()

            barcodeScanner = BarcodeScanning.getClient(options)

            val analysisUseCase = ImageAnalysis.Builder().build().apply {
                setAnalyzer(Executors.newSingleThreadExecutor(), ::processImageProxy)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, previewUseCase, analysisUseCase)
            } catch (e: IllegalStateException) {
                Log.e(TAG, e.message.orEmpty())
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, e.message.orEmpty())
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodeList ->
                    val barcode = barcodeList.getOrNull(0)
                    barcode?.rawValue?.let { value ->
                        Log.d(TAG, "Scanned QR code value: $value") // Log the value

                        val movieDetailFragment = MovieDetailFragment()
                        val bundle = Bundle()
                        bundle.putString("film_id", value)
                        movieDetailFragment.arguments = bundle
                        val intValue = value.toInt()
                        val navController = view?.let { Navigation.findNavController(it) }
                        if (navController != null) {
                            navController.navigate(R.id.navigation_detail_movie, bundleOf("movieId" to intValue))
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.orEmpty())
                }
                .addOnCompleteListener {
                    imageProxy.image?.close()
                    imageProxy.close()
                }
        }
    }

    companion object {
        private const val TAG = "EPF"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}