package chenchen.engine.animate

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Build

/**
 * @author: chenchen
 * @since: 2023/11/15 9:41
 */
object AnimatorCompat {
    fun getTotalDuration(animator: Animator): Long = with(animator) {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            totalDuration
        } else {
            if (this is ValueAnimator) {
                startDelay + (duration * (repeatCount + 1))
            } else {
                startDelay + duration
            }
        }
    }
}