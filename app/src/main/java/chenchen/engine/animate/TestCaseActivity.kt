@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "CanBeParameter")

package chenchen.engine.animate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import chenchen.engine.animate.demo.databinding.ActivityTestCaseBinding

/**
 * @author: chenchen
 * @since: 2024/5/28 15:34
 *
 * testList:
 * [ ] 测试没有动画时AnimateScope是否可以正常运行。开始、结束回调是否正常回调
 * [ ] 测试重复次数是否生效。正向的或者翻转的开始、结束、进度、重复回调是否正常
 *
 * bugList:
 * [ ] reverse无法对repeat生效
 * [ ] rootAnimate设置了startDelay似乎对计算currentPlayTime异常，因为startDelay也算进了totalDuration
 *    可能需要减去startDelay
 */
class TestCaseActivity : AppCompatActivity() {
    private val TAG = "TestCaseActivity"
    private val binding by lazy { ActivityTestCaseBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btn.setOnClickListener {
            runCase()
        }
    }

    private fun runCase() {
        testEmptyAnimate()
        testRepeatCount()
    }

    /**
     * 测试执行空的动画
     */
    fun testEmptyAnimate() {
        animateScope { }
    }

    /**
     * 测试重复次数
     */
    fun testRepeatCount() {
        animateScope {
            repeatCount = 3
            onEnd {
                Log.d(TAG, "testRepeatCount: currentCount:${it.getCurrentRepeatCount()}, ${AnimatorCompat.repeatCount(it.animator)} ")
                assert(AnimatorCompat.repeatCount(it.animator) == it.getCurrentRepeatCount())
            }
        }
    }
}