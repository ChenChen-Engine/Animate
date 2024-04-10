package chenchen.engine.animate


import android.animation.TimeInterpolator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import java.lang.Integer.max

/**
 * 自定义Animator封装，实现快速构建动画关系的前提
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
abstract class Animate(
    internal var animator: ValueAnimator
) : Cloneable {
    private var onEnd: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var onStart: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var onCancel: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var onReverse: ((animateNode: AnimateContainer.AnimateNode, Boolean) -> Unit)? = null
    private var onResume: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var onPause: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var onUpdate: ((animateNode: AnimateContainer.AnimateNode, value: Any, playTime: Long) -> Unit)? = null
    private var onRepeat: ((animateNode: AnimateContainer.AnimateNode, count: Int, totalCount: Int) -> Unit)? = null

    /**
     * 记录自己的上一级
     */
    internal var parent: Animate? = null

    /**
     * 设置动画的时长
     */
    open var duration: Long = animator.duration
        set(value) {
            field = value
            animator.duration = value
        }

    /**
     * 设置动画的插值器
     */
    var interpolator: TimeInterpolator = animator.interpolator
        set(value) {
            field = value
            animator.interpolator = value
        }

    /**
     * 设置动画的估值器
     */
    var evaluator: TypeEvaluator<Any>? = null
        set(value) {
            field = value
            animator.setEvaluator(value)
        }

    /**
     * 设置动画执行次数
     * 原生动画从0开始，为了明确动画执行次数，从1开始，设置多少就是执行多少次，默认为1
     * 且执行次数必须大于等于1，小于等于1则为1。
     * 不可以按照原生动画设置-1无限循环，如果需要使用无限循环，则可以在根动画[onEnd]的时候再次[AnimateScope.start]
     */
    var repeatCount: Int = 1
        set(value) {
            field = value
            animator.repeatCount = max(value - 1, 0)
        }

    init {
        //初始化动画基本属性
        interpolator = LinearInterpolator()
    }

    /**
     * 是否运行中
     */
    fun isRunning() = getAnimateNode()?.isRunning() ?: false

    /**
     * 是否暂停
     */
    fun isPause() = getAnimateNode()?.isPause() ?: false

    /**
     * 是否翻转
     */
    fun isReverse() = getAnimateNode()?.isReverse() ?: false

    /**
     * 当前值
     */
    fun animatedValues() = getAnimateNode()?.animatedValue() ?: false

    /**
     * 当前播放时间
     */
    fun animatedPlayTime() = getAnimateNode()?.animatedPlayTime() ?: false

    /**
     * 获取当前动画节点，一般是向父节点获取
     */
    internal open fun getAnimateNode(): AnimateContainer.AnimateNode? {
        return (parent as? AnimateScope)?.link?.getAnimateNode(this)
            ?: ((this as? AnimateScope)?.animator as? AnimateContainer)?.rootNode
    }

    /**
     * 做好开始前的准备工作
     */
    internal open fun notifyReady() {
        //不支持其他模式，在开始的时候统一置为循环开始
        animator.repeatMode = ValueAnimator.RESTART
        getAnimateNode()?.addListener(animateListener)
    }

    /**
     * 监听动画结束，用法
     * ```
     * animatorScope {
     *      animateWidth {
     *          onEnd {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun onEnd(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        onEnd = block
    }

    /**
     * 监听动画开始，用法
     * ```
     * animatorScope {
     *      animateWidth {
     *          onStart {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun onStart(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        onStart = block
    }

    /**
     * 监听动画取消，用法
     * ```
     * animatorScope {
     *      animateWidth {
     *          onCancel {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun onCancel(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        onCancel = block
    }

    /**
     * 监听动画重复，用法
     * ```
     * animatorScope {
     *      animateWidth {
     *          onReverse {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun onReverse(block: (animateNode: AnimateContainer.AnimateNode, isReverse: Boolean) -> Unit) {
        onReverse = block
    }

    /**
     * 监听动画恢复，用法
     * ```
     * animatorScope {
     *      animateWidth {
     *          onResume {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun onResume(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        onResume = block
    }

    /**
     * 监听动画暂停，用法
     * ```
     * animatorScope {
     *      animateWidth {
     *          onPause {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun onPause(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        onPause = block
    }

    /**
     * 监听动画进度，用法
     * ```
     * animatorScope {
     *      animateWidth {
     *          onUpdate {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun onUpdate(block: (animateNode: AnimateContainer.AnimateNode, value: Any, playTime: Long) -> Unit) {
        onUpdate = block
    }

    /**
     * 监听动画进度，用法
     * ```
     * animatorScope {
     *      animateWidth {
     *          onRepeat {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun onRepeat(block: (animateNode: AnimateContainer.AnimateNode, count: Int, totalCount: Int) -> Unit) {
        onRepeat = block
    }

    private val animateListener = object : AnimateListener {
        override fun onStart(node: AnimateContainer.AnimateNode) {
            onStart?.invoke(node)
        }

        override fun onResume(node: AnimateContainer.AnimateNode) {
            onResume?.invoke(node)
        }

        override fun onPause(node: AnimateContainer.AnimateNode) {
            onPause?.invoke(node)
        }

        override fun onCancel(node: AnimateContainer.AnimateNode) {
            onCancel?.invoke(node)
        }

        override fun onEnd(node: AnimateContainer.AnimateNode) {
            onEnd?.invoke(node)
        }

        override fun onReverse(node: AnimateContainer.AnimateNode, isReverse: Boolean) {
            onReverse?.invoke(node, isReverse)
        }

        override fun onUpdate(node: AnimateContainer.AnimateNode, value: Any, playTime: Long) {
            onUpdate?.invoke(node, value, playTime)
        }

        override fun onRepeat(node: AnimateContainer.AnimateNode, count: Int, totalCount: Int) {
            onRepeat?.invoke(node, count, totalCount)
        }
    }

    public override fun clone(): Animate {
        val clone = super.clone() as Animate
        clone.animator = animator.clone()
        return clone
    }
}
