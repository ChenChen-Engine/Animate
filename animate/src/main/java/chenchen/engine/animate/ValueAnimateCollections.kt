package chenchen.engine.animate


import android.animation.ValueAnimator
import androidx.annotation.ColorInt
import chenchen.engine.animate.Animate

/**
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */

/**
 * 数值类型的动画
 */
abstract class ValueAnimate<V>(
    protected val values: V,
    animator: ValueAnimator
) : Animate(animator)

/**
 * Int类型数值动画
 */
class IntAnimate(
    values: IntArray
) : ValueAnimate<IntArray>(
    values,
    ValueAnimator.ofInt(*values)
)

/**
 * Float类型数值动画
 */
class FloatAnimate(
    values: FloatArray
) : ValueAnimate<FloatArray>(
    values,
    ValueAnimator.ofFloat(*values)
)

/**
 * Float类型数值动画
 */
class ArgbAnimate(
    @ColorInt values: IntArray
) : ValueAnimate<IntArray>(
    values,
    ValueAnimator.ofArgb(*values)
)