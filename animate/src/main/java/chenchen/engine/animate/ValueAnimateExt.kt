package chenchen.engine.animate

import androidx.annotation.ColorInt
import chenchen.engine.animate.Animate


/**
 * 值类型的动画扩展
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */

/**
 * Int数值类型动画
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateInt(
    vararg values: Int,
    scopeContent: IntAnimate.() -> Unit = {}
): Animate {
    val itemScope = IntAnimate(values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * Float数值类型动画
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateFloat(
    vararg values: Float,
    scopeContent: FloatAnimate.() -> Unit = {}
): Animate {
    val itemScope = FloatAnimate(values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * 颜色数值类型动画
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateArgb(
    @ColorInt vararg values: Int,
    scopeContent: ArgbAnimate.() -> Unit = {}
): Animate {
    val itemScope = ArgbAnimate(values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}