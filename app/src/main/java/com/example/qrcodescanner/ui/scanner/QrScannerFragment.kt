package com.example.qrcodescanner.ui.scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.qrcodescanner.R
import com.example.qrcodescanner.db.DbHelper
import com.example.qrcodescanner.db.DbHelperI
import com.example.qrcodescanner.db.database.QrResultDataBase
import com.example.qrcodescanner.ui.dialogs.QrCodeResultDialog
import kotlinx.android.synthetic.main.fragment_qr_scanner.view.*
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView


class QrScannerFragment : Fragment(), ZBarScannerView.ResultHandler {

    private lateinit var resultDialog: QrCodeResultDialog
    private lateinit var dbHelperI: DbHelperI
    private lateinit var mView: View
    private lateinit var scannerView: ZBarScannerView

    companion object {
        fun newInstance(): QrScannerFragment {
            return QrScannerFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_qr_scanner, container, false)
        initialize()
        initViews()
        setupClicks()
        return mView.rootView
    }

    private fun initialize() {
        dbHelperI = DbHelper(QrResultDataBase.getAppDatabase(requireContext())!!)
    }

    private fun initViews() {
        initializeQRCamera()
        setResultDialog()
    }

    private fun initializeQRCamera() {
        scannerView = ZBarScannerView(context)
        with(scannerView) {
            setResultHandler(this@QrScannerFragment)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorTranslucent))
            setBorderColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            setLaserColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            setBorderStrokeWidth(10)
            setSquareViewFinder(true)
            setupScanner()
            setAutoFocus(true)
        }
        startQRCamera()
        mView.containerScanner.addView(scannerView)
    }

    private fun setResultDialog() {
        resultDialog = QrCodeResultDialog(requireContext())
        resultDialog.setOnDismissListener(object : QrCodeResultDialog.OnDismissListener {
            override fun onDismiss() {
                resetPreview()
            }
        })
    }

    override fun handleResult(rawResult: Result?) {
        onQrResult(rawResult?.contents)
    }

    private fun startQRCamera() {
        scannerView.startCamera()
    }

    private fun resetPreview() {
        scannerView.apply {
            stopCamera()
            startCamera()
            stopCameraPreview()
            resumeCameraPreview(this@QrScannerFragment)
        }
    }

    private fun setupClicks() {
        mView.flashToggle.setOnClickListener {
            toggleFlashLight()
        }
    }

    private fun toggleFlashLight() {
        val isSelected = mView.flashToggle.isSelected
        mView.flashToggle.isSelected = !isSelected
        scannerView.flash = !isSelected
    }

    private fun onQrResult(contents: String?) {
        if (contents.isNullOrEmpty()) {
            showToast("Empty Qr Result")
        } else {
            saveToDatabase(contents)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun saveToDatabase(contents: String) {
        val insertedResultId = dbHelperI.insertQRResult(contents)
        val qrResult = dbHelperI.getQRResult(insertedResultId)
        resultDialog.show(qrResult)
    }
}
