package chenchen.engine.animate

import android.view.View
import android.view.Window
import androidx.annotation.ColorInt
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import chenchen.engine.animate.AnimateWrapper.LinkMode.Next
import chenchen.engine.animate.AnimateWrapper.LinkMode.Null
import chenchen.engine.animate.AnimateWrapper.LinkMode.With

/**
 * 提供View的快捷动画扩展方法
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */

/**
 * 快速构建动画链
 * - [ ] 自动启动
 */
class FastAnimateScope : AnimateScope() {
    internal var animateQueue = ArrayList<AnimateWrapper>(1)
}

/**
 * 动画包装类，记录动画是怎么连接起来的
 */
internal class AnimateWrapper(val animate: Animate, var mode: LinkMode = Null) {
    internal enum class LinkMode {
        Null,
        With,
        Next,
    }
}

/**
 * 给任意类型提供构建动画的api，但现在只想给View和Window提供，所以设置为internal
 */
internal fun <T> T.animateBuilder(buildAnimate: AnimateScope.() -> Animate): FastAnimateScope {
    val link = FastAnimateScope()
    val animate = link.buildAnimate()
    link.attachChild(animate)
    link.animateQueue.add(AnimateWrapper(animate))
    return link
}

/**
 * 给View提供构建动画的api
 */
fun View.buildAnimate(buildAnimate: AnimateScope.() -> Animate): FastAnimateScope {
    return this.animateBuilder(buildAnimate)
}

/**
 * 给Window提供构建动画的api
 */
fun Window.buildAnimate(buildAnimate: AnimateScope.() -> Animate): FastAnimateScope {
    return this.animateBuilder(buildAnimate)
}


/**
 * 串行
 */
infix fun FastAnimateScope.next(nextAnim: FastAnimateScope) = apply {
    for (animate in nextAnim.animateQueue) {
        when (animate.mode) {
            With -> {
                animateQueue.last().animate with animate.animate
            }
            Next -> {
                animateQueue.last().animate next animate.animate
            }
            else -> {
                animate.mode = Next
                animateQueue.last().animate next animate.animate
            }
        }
        animate.animate.parent = animateQueue.last().animate.parent
        animateQueue.add(animate)
    }
}


/**
 * 并行
 */
infix fun FastAnimateScope.with(withAnim: FastAnimateScope) = apply {
    for (animate in withAnim.animateQueue) {
        when (animate.mode) {
            Next -> {
                animateQueue.last().animate next animate.animate
            }
            With -> {
                animateQueue.last().animate with animate.animate
            }
            else -> {
                animate.mode = With
                animateQueue.last().animate with animate.animate
            }
        }
        animate.animate.parent = animateQueue.last().animate.parent
        animateQueue.add(animate)
    }
}

/**
 * 延迟，`delay`之后需要用`next`，否则无效，delay的原理就是`next`了一个空的动画
 */
infix fun FastAnimateScope.delay(delay: Long) = apply {
    val animate = createDelay(delay)
    animate.duration = delay
    animateQueue.last().animate next animate
    animateQueue.add(AnimateWrapper(animate, Next))
}

/**
 * View的宽变化动画
 */
fun View.animateWidth(width: Int, duration: Long = 300) = buildAnimate {
    animateWidth(this@animateWidth, width) {
        this.duration = duration
    }
}

/**
 * View的高变化动画
 */
fun View.animateHeight(height: Int, duration: Long = 300) = buildAnimate {
    animateHeight(this@animateHeight, height) {
        this.duration = duration
    }
}

/**
 * View的宽高变化动画
 */
fun View.animatedSize(width: Int = this.width, height: Int = this.height, duration: Long = 300) =
    animateWidth(width, duration) with animateHeight(height, duration)

/**
 * View的start外间距变化动画
 */
fun View.animateMarginStart(start: Int, duration: Long = 300) = buildAnimate {
    animateMarginStart(this@animateMarginStart, start) {
        this.duration = duration
    }
}

/**
 * View的end外间距变化动画
 */
fun View.animateMarginEnd(end: Int, duration: Long = 300) = buildAnimate {
    animateMarginEnd(this@animateMarginEnd, end) {
        this.duration = duration
    }
}

/**
 * View的顶部外间距变化动画
 */
fun View.animateMarginTop(top: Int, duration: Long = 300) = buildAnimate {
    animateMarginTop(this@animateMarginTop, top) {
        this.duration = duration
    }
}

/**
 * View的底部外间距变化动画
 */
fun View.animateMarginBottom(bottom: Int, duration: Long = 300) = buildAnimate {
    animateMarginBottom(this@animateMarginBottom, bottom) {
        this.duration = duration
    }
}

/**
 * View的外间距变化动画
 */
fun View.animateMargin(
    start: Int = marginStart, top: Int = marginTop,
    end: Int = marginEnd, bottom: Int = marginBottom, duration: Long = 300
) = animateMarginStart(start, duration) with animateMarginEnd(end, duration) with
        animateMarginTop(top, duration) with animateMarginBottom(bottom, duration)


/**
 * View的start内间距变化动画
 */
fun View.animatePaddingStart(start: Int, duration: Long = 300) = buildAnimate {
    animatePaddingStart(this@animatePaddingStart, start) {
        this.duration = duration
    }
}

/**
 * View的end内间距变化动画
 */
fun View.animatePaddingEnd(end: Int, duration: Long = 300) = buildAnimate {
    animatePaddingEnd(this@animatePaddingEnd, end) {
        this.duration = duration
    }
}

/**
 * View的顶部内间距变化动画
 */
fun View.animatePaddingTop(top: Int, duration: Long = 300) = buildAnimate {
    animatePaddingTop(this@animatePaddingTop, top) {
        this.duration = duration
    }
}

/**
 * View的底部内间距变化动画
 */
fun View.animatePaddingBottom(bottom: Int, duration: Long = 300) = buildAnimate {
    animatePaddingBottom(this@animatePaddingBottom, bottom) {
        this.duration = duration
    }
}

/**
 * View的内间距变化动画
 */
fun View.animatePadding(
    start: Int = paddingStart, top: Int = paddingTop,
    end: Int = paddingEnd, bottom: Int = paddingBottom, duration: Long = 300
) = animatePaddingStart(start, duration) with animatePaddingEnd(end, duration) with
        animatePaddingTop(top, duration) with animatePaddingBottom(bottom, duration)


/**
 * View的透明度变化动画
 */
fun View.animateAlpha(alpha: Float, duration: Long = 300) = buildAnimate {
    animateAlpha(this@animateAlpha, alpha) {
        this.duration = duration
    }
}

/**
 * View的x轴变化动画
 */
fun View.animateX(x: Float, duration: Long = 300) = buildAnimate {
    animateX(this@animateX, x) {
        this.duration = duration
    }
}

/**
 * View的y轴变化动画
 */
fun View.animateY(y: Float, duration: Long = 300) = buildAnimate {
    animateY(this@animateY, y) {
        this.duration = duration
    }
}

/**
 * View的z轴变化动画
 */
fun View.animateZ(z: Float, duration: Long = 300) = buildAnimate {
    animateZ(this@animateZ, z) {
        this.duration = duration
    }
}

/**
 * View的xyz轴变化动画
 */
fun View.animatedXYZ(
    x: Float = this.x,
    y: Float = this.y,
    z: Float = this.z,
    duration: Long = 300
) = animateX(x, duration) with animateY(y, duration) with animateZ(z, duration)


/**
 * View的x移动变化动画
 */
fun View.animateTranslationX(x: Float, duration: Long = 300) = buildAnimate {
    animateTranslationX(this@animateTranslationX, x) {
        this.duration = duration
    }
}

/**
 * View的x移动变化动画
 */
fun View.animateTranslationY(y: Float, duration: Long = 300) = buildAnimate {
    animateTranslationY(this@animateTranslationY, y) {
        this.duration = duration
    }
}

/**
 * View的x移动变化动画
 */
fun View.animateTranslationZ(y: Float, duration: Long = 300) = buildAnimate {
    animateTranslationZ(this@animateTranslationZ, y) {
        this.duration = duration
    }
}

/**
 * View的xyz移动变化动画
 */
fun View.animatedTranslationXYZ(
    x: Float = this.translationX,
    y: Float = this.translationY,
    z: Float = this.translationZ,
    duration: Long = 300
) =
    animateTranslationX(x, duration) with animateTranslationY(y, duration) with animateTranslationZ(z, duration)


/**
 * View的旋转变化动画
 */
fun View.animateRotation(rotation: Float, duration: Long = 300) = buildAnimate {
    animateRotation(this@animateRotation, rotation) {
        this.duration = duration
    }
}

/**
 * View的X旋转变化动画
 */
fun View.animateRotationX(rotationX: Float, duration: Long = 300) = buildAnimate {
    animateRotationX(this@animateRotationX, rotationX) {
        this.duration = duration
    }
}

/**
 * View的Y旋转变化动画
 */
fun View.animateRotationY(rotationY: Float, duration: Long = 300) = buildAnimate {
    animateRotationY(this@animateRotationY, rotationY) {
        this.duration = duration
    }
}

/**
 * View的XY旋转变化动画
 */
fun View.animatedRotationXY(
    rotationX: Float = this.rotationX,
    rotationY: Float = this.rotationY,
    duration: Long = 300
) = animateRotationX(rotationX, duration) with animateRotationY(rotationY, duration)


/**
 * View的X缩放变化动画
 */
fun View.animateScaleX(scaleX: Float, duration: Long = 300) = buildAnimate {
    animateScaleX(this@animateScaleX, scaleX) {
        this.duration = duration
    }
}

/**
 * View的X缩放变化动画
 */
fun View.animateScaleY(scaleY: Float, duration: Long = 300) = buildAnimate {
    animateScaleY(this@animateScaleY, scaleY) {
        this.duration = duration
    }
}

/**
 * View的XY缩放变化动画
 */
fun View.animatedScaleXY(
    scaleX: Float = this.scaleX,
    scaleY: Float = this.scaleY,
    duration: Long = 300
) = animateScaleX(scaleX, duration) with animateScaleY(scaleY, duration)


/**
 * View的X轴滑动变化动画
 */
fun View.animateScrollX(scrollX: Int, duration: Long = 300) = buildAnimate {
    animateScrollX(this@animateScrollX, scrollX) {
        this.duration = duration
    }
}

/**
 * View的Y轴滑动变化动画
 */
fun View.animateScrollY(scrollY: Int, duration: Long = 300) = buildAnimate {
    animateScrollY(this@animateScrollY, scrollY) {
        this.duration = duration
    }
}

/**
 * View的XY滑动变化动画
 */
fun View.animatedScrollXY(
    scrollX: Int = this.scrollX,
    scrollY: Int = this.scrollY,
    duration: Long = 300
) = animateScrollX(scrollX, duration) with animateScrollY(scrollY, duration)


/**
 * View的背景颜色变化
 */
fun View.animateBackground(@ColorInt toColor: Int, duration: Long = 300) = buildAnimate {
    animateBackground(this@animateBackground, toColor) {
        this.duration = duration
    }
}

/**
 * View的可见变化动画
 */
fun View.animateVisibility(isVisible: Boolean, duration: Long = 300) = buildAnimate {
    animateVisibility(this@animateVisibility, isVisible) {
        this.duration = duration
    }
}

/**
 * Window的透明度变化动画
 */
fun Window.animateDimAmount(alpha: Float, duration: Long = 300) = buildAnimate {
    animateDimAmount(this@animateDimAmount, alpha) {
        this.duration = duration
    }
}