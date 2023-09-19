package chenchen.engine.animate

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import chenchen.engine.animate.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var scope: AnimateScope? = null
    private var srcWidth = 0
    private var srcHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onInitReady()
    }

    private fun onInitReady() {
        binding.btnReverse.setOnClickListener {
            reverse()
        }
        binding.target.setOnClickListener {
            animate()
        }
    }

    private fun animate() {
        if (srcWidth == 0) {
            srcWidth = binding.target.width
            srcHeight = binding.target.height
        }
        //复位
        binding.target.apply {
            updateLayoutParams<MarginLayoutParams> {
                width = srcWidth
                height = srcHeight
            }
            scaleX = 1f
            scaleY = 1f
            rotation = 0f

            alpha = 1f
            setBackgroundColor(0xFF000000.toInt())
        }
        scope = animateScope {
            animateHeight(binding.target, srcHeight, 500) {
                duration = 2000
            } next animateRotation(binding.target, binding.target.rotation, 90f) {
                duration = 1000
            } with animateBackground(binding.target, 0xFF000000.toInt(), 0xFF836764.toInt()) {
                duration = 1000
            } next animateWidth(binding.target, srcWidth, 600) {
                duration = 3000
            }
        }
    }


    private fun reverse() {
        scope?.reverse()
    }
}