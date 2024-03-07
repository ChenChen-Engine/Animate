package chenchen.engine.animate


import android.util.Property
import android.view.View
import android.view.Window
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.view.updatePaddingRelative

/**
 * 为View扩展一些基本属性
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
/**
 * 宽度动画属性
 */
class ViewWidthProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.width
    }

    override fun set(target: View, value: Int) {
        target.setWidth(value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewWidthProperty(PropertyNames.width)
    }
}

/**
 * 高度动画属性
 */
class ViewHeightProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.height
    }

    override fun set(target: View, value: Int) {
        target.setHeight(value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewHeightProperty(PropertyNames.height)
    }
}

/**
 * start外间距动画属性
 */
class ViewMarginStartProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.marginStart
    }

    override fun set(target: View, value: Int) {
        target.updateMarginRelative(start = value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewMarginStartProperty(PropertyNames.marginStart)
    }
}

/**
 * end外间距动画属性
 */
class ViewMarginEndProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.marginEnd
    }

    override fun set(target: View, value: Int) {
        target.updateMarginRelative(end = value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewMarginEndProperty(PropertyNames.marginEnd)
    }
}

/**
 * 顶部外间距动画属性
 */
class ViewMarginTopProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.marginTop
    }

    override fun set(target: View, value: Int) {
        target.updateMarginRelative(top = value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewMarginTopProperty(PropertyNames.marginTop)
    }
}

/**
 * 底部外间距动画属性
 */
class ViewMarginBottomProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.marginBottom
    }

    override fun set(target: View, value: Int) {
        target.updateMarginRelative(bottom = value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewMarginBottomProperty(PropertyNames.marginBottom)
    }
}

/**
 * start内间距动画属性
 */
class ViewPaddingStartProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.paddingStart
    }

    override fun set(target: View, value: Int) {
        target.updatePaddingRelative(start = value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewPaddingStartProperty(PropertyNames.paddingStart)
    }
}

/**
 * end内间距动画属性
 */
class ViewPaddingEndProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.paddingEnd
    }

    override fun set(target: View, value: Int) {
        target.updatePaddingRelative(end = value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewPaddingEndProperty(PropertyNames.paddingEnd)
    }
}

/**
 * 顶部内间距动画属性
 */
class ViewPaddingTopProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.paddingTop
    }

    override fun set(target: View, value: Int) {
        target.updatePaddingRelative(top = value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewPaddingTopProperty(PropertyNames.paddingTop)
    }
}

/**
 * 底部内间距动画属性
 */
class ViewPaddingBottomProperty private constructor(name: String) :
    Property<View, Int>(Int::class.java, name) {
    override fun get(target: View): Int {
        return target.paddingBottom
    }

    override fun set(target: View, value: Int) {
        target.updatePaddingRelative(bottom = value)
    }

    companion object {
        val INSTANCE: Property<View, Int> = ViewPaddingBottomProperty(PropertyNames.paddingBottom)
    }
}

/**
 * 可见性动画属性
 */
class ViewVisibilityProperty private constructor(name: String) :
    Property<View, Float>(Float::class.java, name) {
    override fun get(target: View): Float {
        return target.alpha
    }

    override fun set(target: View, value: Float) {
        val curVisible = target.isVisible
        target.alpha = value
        val visible = value > 0f
        if (curVisible != visible) {
            target.isVisible = visible
        }
    }

    companion object {
        val INSTANCE: Property<View, Float> = ViewVisibilityProperty(PropertyNames.visibility)
    }
}

/**
 * Windows透明度动画属性
 */
class WindowDimAmountProperty private constructor(name: String) :
    Property<Window, Float>(Float::class.java, name) {
    override fun get(target: Window): Float {
        return target.attributes.dimAmount
    }

    override fun set(target: Window, value: Float) {
        val lp = target.attributes
        target.attributes.dimAmount = value
        target.attributes = lp
    }

    companion object {
        val INSTANCE: Property<Window, Float> = WindowDimAmountProperty(
            PropertyNames.windowDimAmount)
    }
}
