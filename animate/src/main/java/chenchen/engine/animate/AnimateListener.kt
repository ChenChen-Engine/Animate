package chenchen.engine.animate


/**
 * 动画回调
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
abstract class AnimateListener {
    /**
     * 动画开始/动画运行中
     * @param node 开始/运行中的对象
     */
    open fun onStart(node: AnimateContainer.AnimateNode) {}

    /**
     * resume是由根节点动画控制，自己是无法控制的
     * @param node 恢复的动画对象，虽然是根节点动画控制，但始终是当前动画恢复了，所以会提供当前恢复的动画对象
     */
    open fun onResume(node: AnimateContainer.AnimateNode) {}

    /**
     * pause是由根节点动画控制，自己是无法控制的
     * @param node 暂停的动画对象，虽然是根节点动画控制，但始终是当前动画暂停了，所以会提供当前暂停的动画对象
     */
    open fun onPause(node: AnimateContainer.AnimateNode) {}

    /**
     * cancel是由根节点动画控制，自己是无法控制的
     * @param node 取消的动画对象，虽然是根节点动画控制，但始终是当前动画取消了，所以会提供当前取消的动画对象
     */
    open fun onCancel(node: AnimateContainer.AnimateNode) {}

    /**
     * 动画结束/动画停止
     * @param node 结束/停止的动画对象
     */
    open fun onEnd(node: AnimateContainer.AnimateNode) {}

    /**
     * 是否翻转，是由根节点动画控制，自己是无法控制的
     * @param node 翻转的动画对象，虽然是根节点动画控制，但始终是当前动画翻转了，所以会提供当前翻转的动画对象
     */
    open fun onReverse(node: AnimateContainer.AnimateNode, isReverse: Boolean) {}

    /**
     * 动画进度更新
     * @param node 更新的动画对象
     * @param value 当前的value值
     * @param playTime 当前播放时间值
     */
    open fun onUpdate(node: AnimateContainer.AnimateNode, value: Any, playTime: Long) {}
}