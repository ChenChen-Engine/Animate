package chenchen.engine.animate


import android.util.Property
import android.view.View
import android.view.Window
import androidx.core.view.isVisible

/**
 * 为View扩展一些基本属性
 * @author: chenchen
 * @since: 2022/4/28 22:27
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
