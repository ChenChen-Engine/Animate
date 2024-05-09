package chenchen.engine.animate


import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window

/**
 * 对象属性类型的动画扩展
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
/**
 * View的宽度动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateWidth(
    target: View,
    vararg values: Int,
    scopeContent: ViewWidthAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewWidthAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的高度动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateHeight(
    target: View,
    vararg values: Int,
    scopeContent: ViewHeightAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewHeightAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的start外间距动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateMarginStart(
    target: View,
    vararg values: Int,
    scopeContent: ViewMarginStartAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewMarginStartAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的end外间距动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateMarginEnd(
    target: View,
    vararg values: Int,
    scopeContent: ViewMarginEndAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewMarginEndAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的顶部外间距动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateMarginTop(
    target: View,
    vararg values: Int,
    scopeContent: ViewMarginTopAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewMarginTopAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的底部外间距动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateMarginBottom(
    target: View,
    vararg values: Int,
    scopeContent: ViewMarginBottomAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewMarginBottomAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的start内间距动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animatePaddingStart(
    target: View,
    vararg values: Int,
    scopeContent: ViewPaddingStartAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewPaddingStartAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的end内间距动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animatePaddingEnd(
    target: View,
    vararg values: Int,
    scopeContent: ViewPaddingEndAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewPaddingEndAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的顶部内间距动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animatePaddingTop(
    target: View,
    vararg values: Int,
    scopeContent: ViewPaddingTopAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewPaddingTopAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的底部内间距动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animatePaddingBottom(
    target: View,
    vararg values: Int,
    scopeContent: ViewPaddingBottomAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewPaddingBottomAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的透明度动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateAlpha(
    target: View,
    vararg values: Float,
    scopeContent: ViewAlphaAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewAlphaAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的X轴偏移动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateX(
    target: View,
    vararg values: Float,
    scopeContent: ViewOffsetXAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewOffsetXAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的Y轴偏移动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateY(
    target: View,
    vararg values: Float,
    scopeContent: ViewOffsetYAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewOffsetYAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的z轴偏移动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateZ(
    target: View,
    vararg values: Float,
    scopeContent: ViewOffsetZAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewOffsetZAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的X轴移动动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateTranslationX(
    target: View,
    vararg values: Float,
    scopeContent: ViewTranslationXAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewTranslationXAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的Y轴移动动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateTranslationY(
    target: View,
    vararg values: Float,
    scopeContent: ViewTranslationYAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewTranslationYAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}


/**
 * View的Z轴移动动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateTranslationZ(
    target: View,
    vararg values: Float,
    scopeContent: ViewTranslationZAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewTranslationZAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的rotation旋转动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateRotation(
    target: View,
    vararg values: Float,
    scopeContent: ViewRotationAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewRotationAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的X轴旋转动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateRotationX(
    target: View,
    vararg values: Float,
    scopeContent: ViewRotationXAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewRotationXAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的Y轴旋转动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateRotationY(
    target: View,
    vararg values: Float,
    scopeContent: ViewRotationYAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewRotationYAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的X轴缩放动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateScaleX(
    target: View,
    vararg values: Float,
    scopeContent: ViewScaleXAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewScaleXAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的Y轴缩放动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0f → ∞f
 */
fun AnimateScope.animateScaleY(
    target: View,
    vararg values: Float,
    scopeContent: ViewScaleYAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewScaleYAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的X轴滚动动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateScrollX(
    target: View,
    vararg values: Int,
    scopeContent: ViewScrollXAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewScrollXAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的Y轴滚动动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateScrollY(
    target: View,
    vararg values: Int,
    scopeContent: ViewScrollYAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewScrollYAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}


/**
 * View的背景色过度动画
 *
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0x00000000 → 0xFFFFFFFF
 */
fun AnimateScope.animateBackground(
    target: View,
    vararg values: Int,
    scopeContent: ViewBackgroundColorAnimate.() -> Unit = {}
): Animate {
    //特殊处理，如果只传一个值做不到平滑过渡，需要从View中获取起始的Color
    val newValues = if (values.size == 1) {
        val startColor = (target.background as? ColorDrawable)?.color ?: 0
        intArrayOf(startColor, *values)
    } else {
        values
    }
    val itemScope = ViewBackgroundColorAnimate(target, newValues)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * View的背景色过度动画
 * @param target 作用到哪个View上
 * @param visible 是否可见
 */
fun AnimateScope.animateVisibility(
    target: View,
    visible: Boolean,
    fromAlpha: Float? = null,
    scopeContent: ViewVisibilityAnimate.() -> Unit = {}
): Animate {
    val itemScope = ViewVisibilityAnimate(target, visible,fromAlpha)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * Window的透明背景动画
 * @param target 作用到哪个View上
 * @param values 修改的值，可以是：0 → ∞
 */
fun AnimateScope.animateDimAmount(
    target: Window,
    vararg values: Float,
    scopeContent: WindowDimAmountAnimate.() -> Unit = {}
): Animate {
    val itemScope = WindowDimAmountAnimate(target, values)
    itemScope.scopeContent()
    this.attachChild(itemScope)
    return itemScope
}
