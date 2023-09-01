package com.example.qrcodescanner.ui.scanned_history

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrcodescanner.R
import com.example.qrcodescanner.db.DbHelper
import com.example.qrcodescanner.db.DbHelperI
import com.example.qrcodescanner.db.database.QrResultDataBase
import com.example.qrcodescanner.db.entities.QrResult
import com.example.qrcodescanner.ui.adapters.ScannedResultListAdapter
import com.example.qrcodescanner.utlis.gone
import com.example.qrcodescanner.utlis.visible
import kotlinx.android.synthetic.main.fragment_scanned_history.view.*
import kotlinx.android.synthetic.main.layout_header_history.view.*


class ScannedHistoryFragment : Fragment() {

    enum class ResultListType {
        ALL_RESULT, FAVOURITE_RESULT
    }

    companion object {

        private const val ARGUMENT_RESULT_LIST_TYPE = "ArgumentResultType"

        fun newInstance(screenType: ResultListType): ScannedHistoryFragment {
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_RESULT_LIST_TYPE, screenType)
            val fragment = ScannedHistoryFragment()
            fragment.arguments = bundle
            return fragment
        }

    }

    private lateinit var mView: View

    private lateinit var dbHelperI: DbHelperI

    private var resultListType: ResultListType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleArguments()
    }

    private fun handleArguments() {
        resultListType = arguments?.getSerializable(ARGUMENT_RESULT_LIST_TYPE) as ResultListType
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_scanned_history, container, false)
        init()
        setSwipeRefresh()
        onClicks()
        showListOfResults()
        return mView.rootView
    }

    private fun init() {
        dbHelperI = DbHelper(QrResultDataBase.getAppDatabase(requireContext())!!)
        mView.layoutHeader.tvHeaderText.text = getString(R.string.recent_scanned_results)
    }

    private fun showListOfResults() {
        when (resultListType) {
            ResultListType.ALL_RESULT -> showAllResults()
            ResultListType.FAVOURITE_RESULT -> showFavouriteResults()
        }
    }

    private fun showAllResults() {
        val listOfAllResult = dbHelperI.getAllQRScannedResult()
        showResults(listOfAllResult)
        mView.layoutHeader.tvHeaderText.text = getString(R.string.recent_scanned)
    }

    private fun showFavouriteResults() {
        val listOfFavouriteResult = dbHelperI.getAllFavouriteQRScannedResult()
        showResults(listOfFavouriteResult)
        //mView.layoutHeader.tvHeaderText.text = getString(R.string.favourites_scanned_results)
    }


    private fun showResults(listOfQrResult: List<QrResult>) {
        if (listOfQrResult.isNotEmpty())
            initRecyclerView(listOfQrResult)
        else
            showEmptyState()
    }

    private fun initRecyclerView(listOfQrResult: List<QrResult>) {
        mView.scannedHistoryRecyclerView.layoutManager = LinearLayoutManager(context)
        mView.scannedHistoryRecyclerView.adapter =
            ScannedResultListAdapter(dbHelperI, requireContext(), listOfQrResult.toMutableList())
        showRecyclerView()
    }

    private fun setSwipeRefresh() {
        mView.swipeRefresh.setOnRefreshListener {
            mView.swipeRefresh.isRefreshing = false
            showListOfResults()
        }
    }


    private fun onClicks() {
        mView.layoutHeader.removeAll.setOnClickListener {
            showRemoveAllScannedResultDialog()
        }
    }

    private fun showRemoveAllScannedResultDialog() {
        AlertDialog.Builder(context, R.style.CustomAlertDialog).setTitle("Delete")
            .setMessage("Are you sure you want to delete all records?")
            .setPositiveButton("Delete All") { _, _ ->
                clearAllRecords()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.show()
    }

    private fun clearAllRecords() {
        when (resultListType) {
            ResultListType.ALL_RESULT -> dbHelperI.deleteAllQRScannedResult()
            ResultListType.FAVOURITE_RESULT -> dbHelperI.deleteAllFavouriteQRScannedResult()
        }
        mView.scannedHistoryRecyclerView.adapter?.notifyDataSetChanged()
        showListOfResults()
    }

    private fun showRecyclerView() {
        mView.layoutHeader.removeAll.visible()
        mView.scannedHistoryRecyclerView.visible()
        mView.noResultFound.gone()
    }

    private fun showEmptyState() {
        mView.layoutHeader.removeAll.gone()
        mView.scannedHistoryRecyclerView.gone()
        mView.noResultFound.visible()
    }


}