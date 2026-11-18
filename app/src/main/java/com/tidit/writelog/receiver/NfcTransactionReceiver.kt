package com.tidit.writelog.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tidit.writelog.TiditApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Lắng nghe sự kiện quẹt NFC ngầm từ hệ điều hành.
 * Chỉ ghi nhận khi mã định danh AID trùng khớp với thẻ Tidit (com.lgcns.didid).
 * Tuyệt đối không nhận diện nhầm khi quẹt thẻ ngân hàng (Visa, Mastercard, Napas, v.v.).
 */
class NfcTransactionReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        // Danh sách AID chính thức trích xuất trực tiếp từ app Tidit (com.lgcns.didid):
        // 1. D410000039533153494944 : Thẻ ra vào S1 Card (S1SIID)
        // 2. D4106429200010         : Thẻ định danh IDP
        private val TIDIT_AIDS = listOf(
            "D410000039533153494944",
            "D4106429200010"
        )

        private var lastRecordedTime = 0L
        private const val DEBOUNCE_INTERVAL_MS = 15_000L
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val action = intent.action
        if (action == "android.nfc.action.TRANSACTION_DETECTED") {
            val aidBytes = intent.getByteArrayExtra("android.nfc.extra.AID")
            val aidHex = aidBytes?.joinToString("") { "%02X".format(it) }?.uppercase() ?: ""
            val dataStr = (intent.dataString ?: "").uppercase()

            // Kiểm tra nghiêm ngặt: Phải chứa AID của Tidit
            val isTiditAid = TIDIT_AIDS.any { aid ->
                aidHex.contains(aid) || dataStr.contains(aid)
            }

            // Nếu không phải AID của Tidit (ví dụ thẻ Visa A000000003..., Master A000000004...) -> bỏ qua hoàn toàn
            if (!isTiditAid) {
                return
            }

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastRecordedTime < DEBOUNCE_INTERVAL_MS) {
                return
            }
            lastRecordedTime = currentTime

            receiverScope.launch {
                TiditApp.instance.repository.recordLog(
                    source = "Tidit (NFC ngầm)",
                    note = "Quẹt thẻ qua cửa thành công (S1/IDP)"
                )
            }
        }
    }
}
