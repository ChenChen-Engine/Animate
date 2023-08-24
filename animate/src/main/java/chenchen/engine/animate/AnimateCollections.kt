package chenchen.engine.animate

import android.animation.ValueAnimator

/**
 * 用于需要传动画但不使用的动画，主要用于调试区分类型
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
open class EmptyAnimator : ValueAnimator() {
    init {
        duration = 0
        setFloatValues(0f, 1f)
    }
}

/**
 * 用于延时的动画，主要用于调试区分类型
 */
class DelayTimeAnimator : EmptyAnimator()