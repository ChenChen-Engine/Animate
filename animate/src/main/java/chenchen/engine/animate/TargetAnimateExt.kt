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
 * 给任意类型提供构建动画的api
 */
fun <T> T.animateBuilder(buildAnimate: AnimateScope.() -> Animate): FastAnimateScope {
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
fun View.animateWidth(
    width: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateWidth(this@animateWidth, width) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的宽变化动画
 */
fun View.animateWidth(
    startWidth: Int, endWidth: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateWidth(this@animateWidth, startWidth, endWidth) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的高变化动画
 */
fun View.animateHeight(
    height: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateHeight(this@animateHeight, height) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的高变化动画
 */
fun View.animateHeight(
    startHeight: Int,
    endHeight: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateHeight(this@animateHeight, startHeight, endHeight) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的宽高变化动画
 */
fun View.animatedSize(
    width: Int = this.width,
    height: Int = this.height,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateWidth(width, duration, scopeContent) with animateHeight(height, duration, scopeContent)

/**
 * View的宽高变化动画
 */
fun View.animatedSize(
    startWidth: Int = this.width,
    endWidth: Int = this.width,
    startHeight: Int = this.height,
    endHeight: Int = this.height,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateWidth(startWidth, endWidth, duration, scopeContent) with
        animateHeight(startHeight, endHeight, duration, scopeContent)

/**
 * View的start外间距变化动画
 */
fun View.animateMarginStart(
    start: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateMarginStart(this@animateMarginStart, start) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的start外间距变化动画
 */
fun View.animateMarginStart(
    startStart: Int,
    endStart: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateMarginStart(this@animateMarginStart, startStart, endStart) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的end外间距变化动画
 */
fun View.animateMarginEnd(
    end: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateMarginEnd(this@animateMarginEnd, end) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的end外间距变化动画
 */
fun View.animateMarginEnd(
    startEnd: Int,
    endEnd: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateMarginEnd(this@animateMarginEnd, startEnd, endEnd) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的顶部外间距变化动画
 */
fun View.animateMarginTop(
    top: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateMarginTop(this@animateMarginTop, top) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的顶部外间距变化动画
 */
fun View.animateMarginTop(
    starTop: Int,
    endTop: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateMarginTop(this@animateMarginTop, starTop, endTop) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的底部外间距变化动画
 */
fun View.animateMarginBottom(
    bottom: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateMarginBottom(this@animateMarginBottom, bottom) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的底部外间距变化动画
 */
fun View.animateMarginBottom(
    startBottom: Int,
    endBottom: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateMarginBottom(this@animateMarginBottom, startBottom, endBottom) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的外间距变化动画
 */
fun View.animateMargin(
    start: Int = marginStart, top: Int = marginTop,
    end: Int = marginEnd, bottom: Int = marginBottom, duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateMarginStart(start, duration, scopeContent) with
        animateMarginEnd(end, duration, scopeContent) with
        animateMarginTop(top, duration, scopeContent) with
        animateMarginBottom(bottom, duration, scopeContent)

/**
 * View的外间距变化动画
 */
fun View.animateMargin(
    startStart: Int = marginStart,
    endStart: Int = marginStart,
    startTop: Int = marginTop,
    endTop: Int = marginTop,
    startEnd: Int = marginEnd,
    endEnd: Int = marginEnd,
    startBottom: Int = marginBottom,
    endBottom: Int = marginBottom,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateMarginStart(startStart, endStart, duration, scopeContent) with
        animateMarginEnd(startEnd, endEnd, duration, scopeContent) with
        animateMarginTop(startTop, endTop, duration, scopeContent) with
        animateMarginBottom(startBottom, endBottom, duration, scopeContent)

/**
 * View的start内间距变化动画
 */
fun View.animatePaddingStart(
    start: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animatePaddingStart(this@animatePaddingStart, start) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的start内间距变化动画
 */
fun View.animatePaddingStart(
    startStart: Int,
    endStart: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animatePaddingStart(this@animatePaddingStart, startStart, endStart) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的end内间距变化动画
 */
fun View.animatePaddingEnd(
    end: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animatePaddingEnd(this@animatePaddingEnd, end) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的end内间距变化动画
 */
fun View.animatePaddingEnd(
    startEnd: Int,
    endEnd: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animatePaddingEnd(this@animatePaddingEnd, startEnd, endEnd) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的顶部内间距变化动画
 */
fun View.animatePaddingTop(
    top: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animatePaddingTop(this@animatePaddingTop, top) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的顶部内间距变化动画
 */
fun View.animatePaddingTop(
    startTop: Int,
    endTop: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animatePaddingTop(this@animatePaddingTop, startTop, endTop) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的底部内间距变化动画
 */
fun View.animatePaddingBottom(
    bottom: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animatePaddingBottom(this@animatePaddingBottom, bottom) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的底部内间距变化动画
 */
fun View.animatePaddingBottom(
    startBottom: Int,
    endBottom: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animatePaddingBottom(this@animatePaddingBottom, startBottom, endBottom) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的内间距变化动画
 */
fun View.animatePadding(
    start: Int = paddingStart, top: Int = paddingTop,
    end: Int = paddingEnd, bottom: Int = paddingBottom, duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animatePaddingStart(start, duration, scopeContent) with
        animatePaddingEnd(end, duration, scopeContent) with
        animatePaddingTop(top, duration, scopeContent) with
        animatePaddingBottom(bottom, duration, scopeContent)

/**
 * View的内间距变化动画
 */
fun View.animatePadding(
    startStart: Int = paddingStart,
    endStart: Int = paddingStart,
    startTop: Int = paddingTop,
    endTop: Int = paddingTop,
    startEnd: Int = paddingEnd,
    endEnd: Int = paddingEnd,
    startBottom: Int = paddingBottom,
    endBottom: Int = paddingBottom,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animatePaddingStart(startStart, endStart, duration, scopeContent) with
        animatePaddingEnd(startEnd, endEnd, duration, scopeContent) with
        animatePaddingTop(startTop, endTop, duration, scopeContent) with
        animatePaddingBottom(startBottom, endBottom, duration, scopeContent)

/**
 * View的透明度变化动画
 */
fun View.animateAlpha(
    alpha: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateAlpha(this@animateAlpha, alpha) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的透明度变化动画
 */
fun View.animateAlpha(
    startAlpha: Float,
    endAlpha: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateAlpha(this@animateAlpha, startAlpha, endAlpha) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的x轴变化动画
 */
fun View.animateX(
    x: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateX(this@animateX, x) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的x轴变化动画
 */
fun View.animateX(
    startX: Float,
    endX: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateX(this@animateX, startX, endX) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的y轴变化动画
 */
fun View.animateY(
    y: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateY(this@animateY, y) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的y轴变化动画
 */
fun View.animateY(
    startY: Float,
    endY: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateY(this@animateY, startY, endY) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的z轴变化动画
 */
fun View.animateZ(
    z: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateZ(this@animateZ, z) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的z轴变化动画
 */
fun View.animateZ(
    startZ: Float,
    endZ: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateZ(this@animateZ, startZ, endZ) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的xyz轴变化动画
 */
fun View.animatedXYZ(
    x: Float = this.x,
    y: Float = this.y,
    z: Float = this.z,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateX(x, duration, scopeContent) with
        animateY(y, duration, scopeContent) with
        animateZ(z, duration, scopeContent)

/**
 * View的xyz轴变化动画
 */
fun View.animatedXYZ(
    startX: Float = this.x,
    endX: Float = this.x,
    startY: Float = this.y,
    endY: Float = this.y,
    startZ: Float = this.z,
    endZ: Float = this.z,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateX(startX, endX, duration, scopeContent) with
        animateY(startY, endY, duration, scopeContent) with
        animateZ(startZ, endZ, duration, scopeContent)

/**
 * View的x移动变化动画
 */
fun View.animateTranslationX(
    x: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateTranslationX(this@animateTranslationX, x) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的x移动变化动画
 */
fun View.animateTranslationX(
    startX: Float,
    endX: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateTranslationX(this@animateTranslationX, startX, endX) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的x移动变化动画
 */
fun View.animateTranslationY(
    y: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateTranslationY(this@animateTranslationY, y) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的x移动变化动画
 */
fun View.animateTranslationY(
    startY: Float,
    endY: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateTranslationY(this@animateTranslationY, startY, endY) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的x移动变化动画
 */
fun View.animateTranslationZ(
    z: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateTranslationZ(this@animateTranslationZ, z) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的x移动变化动画
 */
fun View.animateTranslationZ(
    startZ: Float,
    endZ: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateTranslationZ(this@animateTranslationZ, startZ, endZ) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的xyz移动变化动画
 */
fun View.animatedTranslationXYZ(
    x: Float = this.translationX,
    y: Float = this.translationY,
    z: Float = this.translationZ,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateTranslationX(x, duration, scopeContent) with
        animateTranslationY(y, duration, scopeContent) with
        animateTranslationZ(z, duration, scopeContent)

/**
 * View的xyz移动变化动画
 */
fun View.animatedTranslationXYZ(
    startX: Float = this.translationX,
    endX: Float = this.translationX,
    startY: Float = this.translationY,
    endY: Float = this.translationY,
    startZ: Float = this.translationZ,
    endZ: Float = this.translationZ,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateTranslationX(startX, endX, duration, scopeContent) with
        animateTranslationY(startY, endY, duration, scopeContent) with
        animateTranslationZ(startZ, endZ, duration, scopeContent)

/**
 * View的旋转变化动画
 */
fun View.animateRotation(
    rotation: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateRotation(this@animateRotation, rotation) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的旋转变化动画
 */
fun View.animateRotation(
    startRotation: Float,
    endRotation: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateRotation(this@animateRotation, startRotation, endRotation) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的X旋转变化动画
 */
fun View.animateRotationX(
    rotationX: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateRotationX(this@animateRotationX, rotationX) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的X旋转变化动画
 */
fun View.animateRotationX(
    startRotationX: Float,
    endRotationX: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateRotationX(this@animateRotationX, startRotationX, endRotationX) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的Y旋转变化动画
 */
fun View.animateRotationY(
    rotationY: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateRotationY(this@animateRotationY, rotationY) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的Y旋转变化动画
 */
fun View.animateRotationY(
    startRotationY: Float,
    endRotationY: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateRotationY(this@animateRotationY, startRotationY, endRotationY) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的XY旋转变化动画
 */
fun View.animatedRotationXY(
    rotationX: Float = this.rotationX,
    rotationY: Float = this.rotationY,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateRotationX(rotationX, duration, scopeContent) with
        animateRotationY(rotationY, duration, scopeContent)

/**
 * View的XY旋转变化动画
 */
fun View.animatedRotationXY(
    startRotationX: Float = this.rotationX,
    endRotationX: Float = this.rotationX,
    startRotationY: Float = this.rotationY,
    endRotationY: Float = this.rotationY,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateRotationX(startRotationX, endRotationX, duration, scopeContent) with
        animateRotationY(startRotationY, endRotationY, duration, scopeContent)


/**
 * View的X缩放变化动画
 */
fun View.animateScaleX(
    scaleX: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateScaleX(this@animateScaleX, scaleX) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的X缩放变化动画
 */
fun View.animateScaleX(
    startScaleX: Float,
    endScaleX: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateScaleX(this@animateScaleX, startScaleX, endScaleX) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的X缩放变化动画
 */
fun View.animateScaleY(
    scaleY: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateScaleY(this@animateScaleY, scaleY) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的X缩放变化动画
 */
fun View.animateScaleY(
    startScaleY: Float,
    endScaleY: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateScaleY(this@animateScaleY, startScaleY, endScaleY) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的XY缩放变化动画
 */
fun View.animatedScaleXY(
    scaleX: Float = this.scaleX,
    scaleY: Float = this.scaleY,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateScaleX(scaleX, duration, scopeContent) with
        animateScaleY(scaleY, duration, scopeContent)

/**
 * View的XY缩放变化动画
 */
fun View.animatedScaleXY(
    startScaleX: Float = this.scaleX,
    endScaleX: Float = this.scaleX,
    startScaleY: Float = this.scaleY,
    endScaleY: Float = this.scaleY,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateScaleX(startScaleX, endScaleX, duration, scopeContent) with
        animateScaleY(startScaleY, endScaleY, duration, scopeContent)


/**
 * View的X轴滑动变化动画
 */
fun View.animateScrollX(
    scrollX: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateScrollX(this@animateScrollX, scrollX) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的X轴滑动变化动画
 */
fun View.animateScrollX(
    startScrollX: Int,
    endScrollX: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateScrollX(this@animateScrollX, startScrollX, endScrollX) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的Y轴滑动变化动画
 */
fun View.animateScrollY(
    scrollY: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateScrollY(this@animateScrollY, scrollY) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的Y轴滑动变化动画
 */
fun View.animateScrollY(
    startScrollY: Int,
    endScrollY: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateScrollY(this@animateScrollY, startScrollY, endScrollY) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的XY滑动变化动画
 */
fun View.animatedScrollXY(
    startScrollX: Int = this.scrollX,
    endScrollX: Int = this.scrollX,
    startScrollY: Int = this.scrollY,
    endScrollY: Int = this.scrollY,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = animateScrollX(startScrollX, endScrollX, duration, scopeContent) with
        animateScrollY(startScrollY, endScrollY, duration, scopeContent)


/**
 * View的背景颜色变化
 */
fun View.animateBackground(
    @ColorInt toColor: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateBackground(this@animateBackground, toColor) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的背景颜色变化
 */
fun View.animateBackground(
    @ColorInt startColor: Int,
    @ColorInt endColor: Int,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateBackground(this@animateBackground, startColor, endColor) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * View的可见变化动画
 */
fun View.animateVisibility(
    isVisible: Boolean,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateVisibility(this@animateVisibility, isVisible) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * Window的透明度变化动画
 */
fun Window.animateDimAmount(
    alpha: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateDimAmount(this@animateDimAmount, alpha) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}

/**
 * Window的透明度变化动画
 */
fun Window.animateDimAmount(
    startAlpha: Float,
    endAlpha: Float,
    duration: Long = 300,
    scopeContent: (Animate.() -> Unit)? = null
) = buildAnimate {
    animateDimAmount(this@animateDimAmount, startAlpha, endAlpha) {
        this.duration = duration
        scopeContent?.invoke(this)
    }
}