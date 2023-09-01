package com.example.qrcodescanner.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.qrcodescanner.ui.scanned_history.ScannedHistoryFragment
import com.example.qrcodescanner.ui.scanner.QrScannerFragment


class MainPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val fragmentMap = mapOf(
        0 to { QrScannerFragment.newInstance() },
        1 to { ScannedHistoryFragment.newInstance(ScannedHistoryFragment.ResultListType.ALL_RESULT) },
        2 to { ScannedHistoryFragment.newInstance(ScannedHistoryFragment.ResultListType.FAVOURITE_RESULT) }
    )

    override fun getItem(position: Int): Fragment {
        return fragmentMap[position]?.invoke() ?: QrScannerFragment()
    }

    override fun getCount(): Int {
        return fragmentMap.size
    }
}
