package chenchen.engine.animate


import android.animation.ObjectAnimator
import android.view.View
import android.view.Window

/**
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
/**
 * 对象属性类型的动画
 */
abstract class ObjectAnimate<T, V>(
    protected val target: T,
    protected val values: V,
    animator: ObjectAnimator
) : Animate(animator)

/**
 * View的宽度动画
 */
class ViewWidthAnimate(
    target: View, values: IntArray
) : ObjectAnimate<View, IntArray>(
    target, values,
    ObjectAnimator.ofInt(target, ViewWidthProperty.INSTANCE, *values)
)

/**
 * View的高度动画
 */
class ViewHeightAnimate(
    target: View, values: IntArray
) : ObjectAnimate<View, IntArray>(
    target, values,
    ObjectAnimator.ofInt(target, ViewHeightProperty.INSTANCE, *values)
)

/**
 * View的透明度动画
 */
class ViewAlphaAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.alpha, *values)
)

/**
 * View的X移动动画
 */
class ViewOffsetXAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.x, *values)
)

/**
 * View的Y移动动画
 */
class ViewOffsetYAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.y, *values)
)

/**
 * View的Z移动动画
 */
class ViewOffsetZAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.z, *values)
)

/**
 * View的TranslationZ移动动画
 */
class ViewTranslationZAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.translationZ, *values)
)

/**
 * View的TranslationX移动动画
 */
class ViewTranslationXAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.translationX, *values)
)


/**
 * View的TranslationY移动动画
 */
class ViewTranslationYAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.translationY, *values)
)

/**
 * View的Rotation旋转动画
 */
class ViewRotationAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.rotation, *values)
)

/**
 * View的RotationX旋转动画
 */
class ViewRotationXAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.rotationX, *values)
)

/**
 * View的RotationY旋转动画
 */
class ViewRotationYAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.rotationY, * values)
)

/**
 * View的ScaleX缩放动画
 */
class ViewScaleXAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.scaleX, * values)
)

/**
 * View的ScaleY缩放动画
 */
class ViewScaleYAnimate(
    target: View, values: FloatArray
) : ObjectAnimate<View, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, PropertyNames.scaleY, * values)
)

/**
 * View的ScrollX缩放动画
 */
class ViewScrollXAnimate(
    target: View, values: IntArray
) : ObjectAnimate<View, IntArray>(
    target, values,
    ObjectAnimator.ofInt(target, PropertyNames.scrollX, *values)
)

/**
 * View的ScrollY缩放动画
 */
class ViewScrollYAnimate(
    target: View, values: IntArray
) : ObjectAnimate<View, IntArray>(
    target, values,
    ObjectAnimator.ofInt(target, PropertyNames.scrollY, * values)
)


/**
 * View的背景颜色动画
 */
class ViewBackgroundColorAnimate(
    target: View, values: IntArray
) : ObjectAnimate<View, IntArray>(
    target, values,
    ObjectAnimator.ofArgb(target, PropertyNames.backgroundColor, *values)
)

/**
 * View的可见动画，这里是利用alpha和visibility实现的
 */
class ViewVisibilityAnimate(
    target: View, values: Boolean
) : ObjectAnimate<View, Boolean>(
    target, values,
    ObjectAnimator.ofFloat(target, ViewVisibilityProperty.INSTANCE, if (values) 1f else 0f)
)

/**
 * Window的背景透明度动画，适用于Dialog这样的，当然其他的Window也可以用
 */
class WindowDimAmountAnimate(
    target: Window, values: FloatArray
) : ObjectAnimate<Window, FloatArray>(
    target, values,
    ObjectAnimator.ofFloat(target, WindowDimAmountProperty.INSTANCE, *values)
)