package chenchen.engine.animate

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams

/**
 * 给原生API没有的对象定义属性
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
/**
 * 设置宽度
 */
fun View.setWidth(width: Int) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        this.width = width
    }
}

/**
 * 设置高度
 */
fun View.setHeight(height: Int) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        this.height = height
    }
}

/**
 * 设置适配LTR或RTL的margin
 */
fun View.updateMarginRelative(
    start: Int = marginStart, top: Int = marginTop,
    end: Int = marginEnd, bottom: Int = marginBottom
) {
    updateLayoutParams<MarginLayoutParams> {
        marginStart = start
        topMargin = top
        marginEnd = end
        bottomMargin = bottom
    }
}