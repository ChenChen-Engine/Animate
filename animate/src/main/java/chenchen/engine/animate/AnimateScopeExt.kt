package chenchen.engine.animate



/**
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
/**
 * 顶级animatorScope，可以选择直接启动或者手动启动
 */
fun animateScope(
    lazyStart: Boolean = false,
    action: AnimateScope.() -> Unit
): AnimateScope {
    val scope = AnimateScope()
    scope.action()
    if (!lazyStart) {
        scope.start()
    }
    return scope
}

/**
 * animatorScope内部再启动的子animatorScope，这样定义不会直接start
 */
fun AnimateScope.subAnimateScope(
    action: SubAnimateScope.() -> Unit
): Animate {
    val itemScope = SubAnimateScope()
    itemScope.action()
    this.attachChild(itemScope)
    return itemScope
}

/**
 * 延迟执行
 */
internal fun AnimateScope.createDelay(delay: Long): Animate {
    val itemScope = DelayAnimate()
    itemScope.duration = delay
    this.attachChild(itemScope)
    return itemScope
}