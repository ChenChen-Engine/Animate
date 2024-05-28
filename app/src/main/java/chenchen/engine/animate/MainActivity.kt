package chenchen.engine.animate

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import chenchen.engine.animate.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onInitReady()
    }

    private fun onInitReady() = with(binding) {
        btnFrameApi.setOnClickListener {
            startActivity(Intent(this@MainActivity, FrameApiTestActivity::class.java))
        }
        btnSourceApi.setOnClickListener {
            startActivity(Intent(this@MainActivity, SourceApiTestActivity::class.java))
        }
        btnTestCase.setOnClickListener {
            startActivity(Intent(this@MainActivity, TestCaseActivity::class.java))
        }
    }
}