package com.icefisher.game.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.telephony.TelephonyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Locale
import java.util.concurrent.TimeUnit

class NetworkManager(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    suspend fun fetchServerResponse(): String? = withContext(Dispatchers.IO) {
        try {
            val requestLink = buildRequestLink()
            val request = Request.Builder()
                .url(requestLink)
                .cacheControl(CacheControl.Builder().noCache().noStore().build())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .build()
            
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun buildRequestLink(): String {
        val osVersion = "Android ${Build.VERSION.RELEASE}"
        val language = Locale.getDefault().language
        val region = Locale.getDefault().country
        val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
        val batteryStatus = getBatteryStatus()
        val batteryLevel = getBatteryLevel()
        val networkCountry = getNetworkCountry()
        val simState = getSimState()
        
        return "https://gtappinfo.site/a-ice-fisher/server.php?" +
                "p=Jh675eYuunk85&" +
                "os=$osVersion&" +
                "lng=$language&" +
                "loc=$region&" +
                "devicemodel=$deviceModel&" +
                "bs=$batteryStatus&" +
                "bl=$batteryLevel&" +
                "nc=$networkCountry&" +
                "sim=$simState"
    }
    
    private fun getBatteryStatus(): String {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryIntent = context.registerReceiver(null, intentFilter)
        
        return when (batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NotCharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            else -> "Unknown"
        }
    }
    
    private fun getNetworkCountry(): String {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            tm.networkCountryIso ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    private fun getSimState(): String {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.simState) {
                TelephonyManager.SIM_STATE_ABSENT -> "absent"
                TelephonyManager.SIM_STATE_READY -> "ready"
                TelephonyManager.SIM_STATE_PIN_REQUIRED -> "pin_required"
                TelephonyManager.SIM_STATE_PUK_REQUIRED -> "puk_required"
                TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "network_locked"
                TelephonyManager.SIM_STATE_NOT_READY -> "not_ready"
                TelephonyManager.SIM_STATE_PERM_DISABLED -> "perm_disabled"
                TelephonyManager.SIM_STATE_CARD_IO_ERROR -> "card_io_error"
                TelephonyManager.SIM_STATE_CARD_RESTRICTED -> "card_restricted"
                else -> "unknown"
            }
        } catch (_: Exception) {
            "error"
        }
    }

    private fun getBatteryLevel(): String {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryIntent = context.registerReceiver(null, intentFilter)
        
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        
        if (level == -1 || scale == -1) return "0"
        
        val batteryPct = level.toFloat() / scale.toFloat()
        
        return if (batteryPct >= 1f) {
            "1"
        } else {
            String.format(Locale.US, "%.2f", batteryPct)
        }
    }
}
