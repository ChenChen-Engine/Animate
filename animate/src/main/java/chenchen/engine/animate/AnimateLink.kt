package chenchen.engine.animate

import android.animation.Animator
import chenchen.engine.animate.AnimateScope
import chenchen.engine.animate.createDelay
import java.util.WeakHashMap

/**
 * 用于快速构建动画关系
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
class AnimateLink(private val combiner: AnimateContainer) {

    private val animateMap = WeakHashMap<Animate, Animator>(1)

    /**
     * 普通动画默认会加入combiner
     */
    fun from(animate: Animate) {
        animateMap[animate] = animate.animator
        combiner.from(animate.animator)
    }

    /**
     * 移除普通动画
     */
    fun remove(animate: Animate) {
        animateMap.remove(animate)
        combiner.remove(animate.animator)
    }

    fun next(fromAnimate: Animate, nextAnimate: Animate) {
        animateMap[nextAnimate] = nextAnimate.animator
        combiner.from(fromAnimate.animator).next(nextAnimate.animator)
    }

    fun with(fromAnimate: Animate, withAnimate: Animate) {
        animateMap[withAnimate] = withAnimate.animator
        combiner.from(fromAnimate.animator).with(withAnimate.animator)
    }

    fun start(delay: Long = 0) {
        combiner.start(delay)
    }

    fun cancel() {
        combiner.cancel()
    }

    fun end() {
        combiner.end()
    }

    fun pause() {
        combiner.pause()
    }

    fun resume() {
        combiner.resume()
    }

    fun reverse() {
        combiner.reverse()
    }

    fun setCurrentPlayTime(playTime: Long) {
        combiner.currentPlayTime = playTime
    }

    fun getAnimateNode(animate: Animate): AnimateContainer.AnimateNode? {
        return combiner.getNodeForAnimator(animate.animator)
    }

    fun getAnimates(): Set<Animate> = animateMap.keys
}

/**
 * 串行
 */
infix fun Animate.next(nextAnim: Animate): Animate {
    assert(parent is AnimateScope)
    val parentScope = parent as AnimateScope
    //因为构造动画时默认就会添加到父级中，这里需要先移除再处理next关系
    (nextAnim.parent as? AnimateScope)?.link?.remove(nextAnim)
    parentScope.link.next(this, nextAnim)
    return nextAnim
}

/**
 * 并行
 */
infix fun Animate.with(withAnim: Animate): Animate {
    assert(parent is AnimateScope)
    val parentScope = parent as AnimateScope
    //因为构造动画时默认就会添加到父级中，这里需要先移除再处理with关系
    (withAnim.parent as? AnimateScope)?.link?.remove(withAnim)
    parentScope.link.with(this, withAnim)
    return withAnim
}

/**
 * 延迟，`delay`之后需要用`next`，否则无效，delay的原理就是`next`了一个空的动画
 * 注意，`delay`之后不要用`with`，用`with`表示和delay一起执行
 */
infix fun Animate.delay(delay: Long): Animate {
    assert(this.parent is AnimateScope)
    val anim = (this.parent as AnimateScope).createDelay(delay)
    anim.duration = delay
    return next(anim)
}