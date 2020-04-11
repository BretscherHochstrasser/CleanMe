package ch.bretscherhochstrasser.cleanme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val deviceUsageStats = DeviceUsageStats(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_start_service).setOnClickListener {
            invokeService(DeviceUsageService.ACTION_START_OBSERVE_DEVICE_STATE)
        }
        findViewById<Button>(R.id.button_stop_service).setOnClickListener {
            invokeService(DeviceUsageService.ACTION_STOP_OBSERVE_DEVICE_STATE)
        }
        findViewById<Button>(R.id.button_reset_stats).setOnClickListener {
            invokeService(DeviceUsageService.ACTION_RESET_USAGE_STATS)
        }
    }

    private fun invokeService(action: String) {
        val intent = Intent(this, DeviceUsageService::class.java)
        intent.action = action
        startService(intent)
    }
}