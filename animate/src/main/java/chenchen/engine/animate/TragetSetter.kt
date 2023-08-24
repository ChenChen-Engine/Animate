package chenchen.engine.animate

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams

/**
 * 给原生API没有的对象定义属性
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
fun View.setWidth(width: Int) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        this.width = width
    }
}

fun View.setHeight(height: Int) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        this.height = height
    }
}