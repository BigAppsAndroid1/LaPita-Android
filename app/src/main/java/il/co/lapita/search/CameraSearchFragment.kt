package il.co.lapita.search

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.dm6801.framework.infrastructure.foregroundActivity
import com.dm6801.framework.infrastructure.foregroundApplication
import com.dm6801.framework.utilities.catch
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import il.co.lapita.R
import il.co.lapita.infrastructure.BaseFragment
import il.co.lapita.utilities.onClick
import kotlinx.android.synthetic.main.fragment_camera_search.*


class CameraSearchFragment : BaseFragment() {

    companion object : Comp() {
        val KEY_BARCODE = "KEY_BARCODE"
        fun open(onBarcode: (String) -> Unit) {
            open(KEY_BARCODE to onBarcode)
        }
    }

    override val layout: Int
        get() = R.layout.fragment_camera_search

    private val surfaceView: SurfaceView? get() = camera_surface_view
    private val back: ImageView? get() = camera_back
    private var barcodeDetector: BarcodeDetector? = null
    private val REQUEST_CAMERA_PERMISSION = 201
    private var cameraSource: CameraSource? = null
    private var onBarcode: ((String) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back?.onClick {
            foregroundActivity?.popBackStack()
        }
        initialiseDetectorsAndSources()
    }

    override fun onArguments(arguments: Map<String, Any?>) {
        super.onArguments(arguments)
        (arguments[KEY_BARCODE] as? ((String) -> Unit))?.let { onBarcode = it }
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (resultCode == PackageManager.PERMISSION_GRANTED) {
                cameraSource?.start(surfaceView?.holder)
            }
        }
    }

    private fun initialiseDetectorsAndSources() {

        barcodeDetector = BarcodeDetector.Builder(context)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(context, barcodeDetector)
            .setAutoFocusEnabled(true)
            .build()
        surfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                catch {
                    if (ActivityCompat.checkSelfPermission(
                            context ?: foregroundApplication,
                            CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource?.start(surfaceView?.holder)
                    } else {
                        foregroundActivity?.let {
                            ActivityCompat.requestPermissions(
                                it,
                                arrayOf(CAMERA),
                                REQUEST_CAMERA_PERMISSION
                            )
                        }
                    }
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })
        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode?> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode?>) {
                val barcodes: SparseArray<Barcode?> = detections.detectedItems
                if (barcodes.size() != 0) {
                    barcodes.valueAt(0)?.displayValue.toString().takeIf { it.isNotBlank() }?.let {
                        close()
                        onBarcode?.invoke(it)
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        cameraSource?.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }
}