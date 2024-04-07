package chenchen.engine.animate


/**
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
/**
 * 顶级作用域动画，在这个作用域下构建动画关系
 */
open class AnimateScope(animator: AnimateContainer = AnimateContainer()) : Animate(animator/*这个动画随便传，不会使用到的*/) {

    /**
     * 用于组合动画的link，默认每个动画都是一个单独的link，只有两个link组合的时候，就会替换默认的link，而共同持有link头节点的link
     * 比如：
     * ```
     * animatorScope {
     *      //这种情况widthAnim单独是一个link
     *      widthAnim(view,100)
     *      //这种情况heightAnim单独是一个link
     *      heightAnim(view,100)
     *      //这种情况，heightAnim会获取头节点widthAnim的link替换自己默认的link
     *      widthAnim(view,100) with heightAnim(view,100)
     * }
     * ```
     */
    internal var link: AnimateLink = AnimateLink(animator)

    final override var duration: Long = 0
        set(_) = throw throw IllegalArgumentException("unsupported set total duration, Please let the sub-animation control its own duration, 不支持设置总时长，让子动画控制自己的时长")

    /**
     * 加入一个子级
     * @param child 动画
     */
    fun attachChild(child: Animate) {
        child.parent = this
        //每当有动画添加到父级的时候，默认都添加到动画管理中，再构建动画链的时候再去移除
        link.from(child)
    }

    /**
     * 做好开始前的准备工作，作为Scope，需要通知子级
     */
    final override fun notifyReady() {
        super.notifyReady()
        for (animate in link.getAnimates()) {
            animate.notifyReady()
        }
    }

    /**
     * 开始动画
     * @param delay 延迟开始
     */
    open fun start(delay: Long = 0) {
        assert(parent == null) { "Only the topmost parent can start(只有最顶层父级可以启动)" }
        notifyReady()
        //大部分场景都是在作用域中去构造动画的，这里面不允许单个动画单独启动，统一由顶层管理
        link.start(delay)
    }

    /**
     * 取消自己和所有子级的动画
     * 如果还没有start过，cancel自然无效
     * 对于已经start过的，animatorSet内部持有所有层级的animation，会统一管理
     */
    open fun cancel() {
        assert(parent == null) { "Only the topmost parent can start(只有最顶层父级可以启动)" }
        link.cancel()
    }

    /**
     * 结束自己和所有子级的动画
     * 如果还没有start过，end自然无效
     * 对于已经start过的，animatorSet内部持有所有层级的animation，会统一管理
     */
    open fun end() {
        assert(parent == null) { "Only the topmost parent can start(只有最顶层父级可以启动)" }
        notifyReady()
        link.end()
    }

    /**
     * 暂停自己和所有子级的动画
     * 如果还没有start过，pause自然无效
     * 对于已经start过的，animatorSet内部持有所有层级的animation，会统一管理
     */
    open fun pause() {
        assert(parent == null) { "Only the topmost parent can start(只有最顶层父级可以启动)" }
        link.pause()
    }

    /**
     * 恢复自己和所有子级的动画
     * 如果还没有start过，resume自然无效
     * 对于已经start过的，animatorSet内部持有所有层级的animation，会统一管理
     */
    open fun resume() {
        assert(parent == null) { "Only the topmost parent can start(只有最顶层父级可以启动)" }
        link.resume()
    }

    /**
     * 翻转动画
     */
    open fun reverse() {
        assert(parent == null) { "Only the topmost parent can start(只有最顶层父级可以启动)" }
        notifyReady()
        link.reverse()
    }

    /**
     * 设置动画时间进度
     */
    open fun setCurrentPlayTime(playTime: Long) {
        assert(parent == null) { "Only the topmost parent can start(只有最顶层父级可以启动)" }
        link.setCurrentPlayTime(playTime)
    }
}

/**
 * 子级作用域动画，在这个作用域下构建动画关系，子级作用域可灵活构建一组动画，也可以串行并行
 */
class SubAnimateScope(animator: AnimateContainer = AnimateContainer())
    : AnimateScope(animator/*这个动画随便传，不会使用到的*/)

/**
 * 延迟动画，在串行动画关系中，延迟执行下一个动画
 */
class DelayAnimate : Animate(DelayTimeAnimator())