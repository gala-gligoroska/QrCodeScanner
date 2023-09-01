package com.example.qrcodescanner.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodescanner.R
import com.example.qrcodescanner.db.DbHelperI
import com.example.qrcodescanner.db.entities.QrResult
import com.example.qrcodescanner.ui.dialogs.QrCodeResultDialog
import com.example.qrcodescanner.utlis.gone
import com.example.qrcodescanner.utlis.toFormattedDisplay
import com.example.qrcodescanner.utlis.visible
import kotlinx.android.synthetic.main.layout_qr_result_show.view.*
import kotlinx.android.synthetic.main.layout_qr_result_show.view.favouriteIcon
import kotlinx.android.synthetic.main.layout_single_item_qr_result.view.*
import java.nio.file.Files.delete


class ScannedResultListAdapter(
    private val dbHelperI: DbHelperI,
    private val context: Context,
    private val listOfScannedResult: MutableList<QrResult>
) : RecyclerView.Adapter<ScannedResultListAdapter.ScannedResultListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannedResultListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_single_item_qr_result,
            parent,
            false
        )
        return ScannedResultListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfScannedResult.size
    }

    override fun onBindViewHolder(holder: ScannedResultListViewHolder, position: Int) {
        holder.bind(listOfScannedResult[position])
    }

    inner class ScannedResultListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            setupClickListeners()
        }

        fun bind(qrResult: QrResult) {
            itemView.result.text = qrResult.result
            itemView.tvTime.text = qrResult.calendar.toFormattedDisplay()
            setFavouriteIconVisibility(qrResult.favourite)
        }

        private fun setupClickListeners() {
            itemView.setOnClickListener {
                showResultDialog(listOfScannedResult[adapterPosition])
            }

            itemView.setOnLongClickListener {
                showDeleteConfirmationDialog(listOfScannedResult[adapterPosition])
                true
            }
        }

        private fun showResultDialog(qrResult: QrResult) {
            val resultDialog = QrCodeResultDialog(context)
            resultDialog.show(qrResult)
        }

        private fun showDeleteConfirmationDialog(qrResult: QrResult) {
            AlertDialog.Builder(context, R.style.CustomAlertDialog)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteResult(qrResult)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }

        private fun deleteResult(qrResult: QrResult) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                dbHelperI.deleteQrResult(qrResult.id!!)
                listOfScannedResult.removeAt(position)
                notifyItemRemoved(position)
            }
        }

        private fun setFavouriteIconVisibility(isFavourite: Boolean) {
            itemView.favouriteIcon.visibility = if (isFavourite) View.VISIBLE else View.GONE
        }
    }
}
