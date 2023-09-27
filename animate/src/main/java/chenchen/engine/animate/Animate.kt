package chenchen.engine.animate


import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

/**
 * 自定义Animator封装，实现快速构建动画关系的前提
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
abstract class Animate(
    /**
     * 当前动画的对象
     */
    internal val animator: ValueAnimator) {
    private var weakOnEnd: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var weakOnStart: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var weakOnCancel: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var weakOnReverse: ((animateNode: AnimateContainer.AnimateNode, Boolean) -> Unit)? = null
    private var weakOnResume: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var weakOnPause: ((animateNode: AnimateContainer.AnimateNode) -> Unit)? = null
    private var weakOnUpdate: ((animateNode: AnimateContainer.AnimateNode, value: Any, playTime: Long) -> Unit)? = null

    /**
     * 记录自己的上一级
     */
    internal var parent: Animate? = null

    /**
     * 设置动画的时长
     */
    open var duration: Long = 0L
        set(value) {
            field = value
            animator.duration = value
        }

    /**
     * 设置动画的插值器
     */
    var interpolator: Interpolator = LinearInterpolator()
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
    internal open fun notifyStart() {
        getAnimateNode()?.addListener(animateListener)
    }

    /**
     * 监听动画结束，用法
     * ```
     * animatorScope {
     *      widthAnim {
     *          doOnEnd {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun doOnEnd(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        weakOnEnd = block
    }

    /**
     * 监听动画开始，用法
     * ```
     * animatorScope {
     *      widthAnim {
     *          doOnStart {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun doOnStart(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        weakOnStart = block
    }

    /**
     * 监听动画取消，用法
     * ```
     * animatorScope {
     *      widthAnim {
     *          doOnCancel {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun doOnCancel(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        weakOnCancel = block
    }

    /**
     * 监听动画重复，用法
     * ```
     * animatorScope {
     *      widthAnim {
     *          doOnReverse {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun doOnReverse(block: (animateNode: AnimateContainer.AnimateNode, isReverse: Boolean) -> Unit) {
        weakOnReverse = block
    }

    /**
     * 监听动画恢复，用法
     * ```
     * animatorScope {
     *      widthAnim {
     *          doOnResume {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun doOnResume(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        weakOnResume = block
    }

    /**
     * 监听动画暂停，用法
     * ```
     * animatorScope {
     *      widthAnim {
     *          doOnPause {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun doOnPause(block: (animateNode: AnimateContainer.AnimateNode) -> Unit) {
        weakOnPause = block
    }

    /**
     * 监听动画进度，用法
     * ```
     * animatorScope {
     *      widthAnim {
     *          doOnUpdate {
     *              //do Something
     *          }
     *      }
     * }
     * ```
     */
    fun doOnUpdate(block: (animateNode: AnimateContainer.AnimateNode, value: Any, playTime: Long) -> Unit) {
        weakOnUpdate = block
    }

    internal val animateListener = object : AnimateListener() {
        override fun onStart(node: AnimateContainer.AnimateNode) {
            weakOnStart?.invoke(node)
        }

        override fun onResume(node: AnimateContainer.AnimateNode) {
            weakOnResume?.invoke(node)
        }

        override fun onPause(node: AnimateContainer.AnimateNode) {
            weakOnPause?.invoke(node)
        }

        override fun onCancel(node: AnimateContainer.AnimateNode) {
            weakOnCancel?.invoke(node)
        }

        override fun onEnd(node: AnimateContainer.AnimateNode) {
            weakOnEnd?.invoke(node)
        }

        override fun onReverse(node: AnimateContainer.AnimateNode, isReverse: Boolean) {
            weakOnReverse?.invoke(node, isReverse)
        }

        override fun onUpdate(node: AnimateContainer.AnimateNode, value: Any, playTime: Long) {
            weakOnUpdate?.invoke(node, value, playTime)
        }
    }
}
