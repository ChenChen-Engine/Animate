package chenchen.engine.animate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.util.ArrayMap
import kotlin.math.max

/**
 * 动画管理类，代替[android.animation.AnimatorSet]
 * 使用注意事项：
 * 1. 整个结构部中不能出现重复的动画对象，否则执行效果会异常
 * 2. [setRepeatCount]、[setRepeatMode]暂时没有测试过，不知道会不会异常，是因为24才能调用[getTotalDuration]
 * 3. 目前无法clone
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
class AnimateContainer : ValueAnimator() {

    /**
     * 根节点
     */
    internal val rootNode by lazy { AnimateNode(this) }

    /**
     * 节点缓存
     */
    private var nodeMap = ArrayMap<Animator, AnimateNode>(1)

    /**
     * 延时开始
     */
    fun start(delay: Long = 0) {
        startDelay = delay
        start()
    }

    /**
     * 立即开始
     */
    override fun start() {
        if (rootNode.getParentNode() != null) {
            throw UnsupportedOperationException("child animate cannot start, please use root animate(子动画无法操作start，请使用根动画)")
        }
        initAnimation()
        super.start()
    }

    /**
     * 不支持设置总时长，让子动画控制自己的时长
     */
    override fun setDuration(duration: Long): ValueAnimator {
        throw IllegalArgumentException("unsupported set total duration, Please let the sub-animation control its own duration, 不支持设置总时长，让子动画控制自己的时长")
    }

    /**
     * 初始化动画配置
     */
    private fun initAnimation() {
        rootNode.initAnimation()
    }

    /**
     * 设置时长，只能内部调用
     */
    private fun superSetDuration(duration: Long) {
        super.setDuration(duration)
    }

    /**
     * 翻转动画：
     * 1. 假设动画时长1000，当动画执行400的时候翻转，执行400后结束。
     * 2. 当动画结束后仍调用翻转，则翻转并播放动画
     * 与[superReverse]不同点在于，[superReverse]在任意时间段翻转都将直接从终点开始翻转
     */
    override fun reverse() {
        if (rootNode.getParentNode() != null) {
            throw UnsupportedOperationException("child animate cannot reverse, please use root animate(子动画无法操作reverse，请使用根动画)")
        }
        initAnimation()
        val toPlayTime = calculateReverseToPlayTime()
        rootNode.dispatchReverse(!rootNode.isReverse())
        //这里只能用原生的reverse逻辑，cancel再start会造成currentPlayTime异常
        super.reverse()
        //需要等原生逻辑执行完再设置进度，才不会异常
        currentPlayTime = toPlayTime
    }

    /**
     * 计算翻转的位置，可以让动画从当前的点开始翻转，而不是从某一端开始翻转
     */
    private fun calculateReverseToPlayTime(): Long {
        //每次获取最新的时长
        val totalDuration = AnimatorCompat.getTotalDuration(this)
        return if (isStarted || isRunning) {
            //已经开始或正在运行中reverse，只需要简单计算(这里先不处理运行中时长发生变化，要禁止这种行为)
            totalDuration - minOf(currentPlayTime, totalDuration)
        } else {
            //不在运行中，要么结束，要么被取消。如果无论结束或被取消，playTime都会重置为0，
            //如果结束，返回0是正常的，表示从某一端开始翻转。
            //但取消可能是在中途被取消，如果取消要记录，而不是从某一端重新开始，则与Pause没有什么不同，所以这里不做这样的处理
            0
        }
    }

    /**
     * 原始的reverse，从终点开始翻转
     */
    fun superReverse() {
        if (rootNode.getParentNode() != null) {
            throw UnsupportedOperationException("child animate cannot reverse, please use root animate(子动画无法操作reverse，请使用根动画)")
        }
        rootNode.dispatchReverse(!rootNode.isReverse())
        super.reverse()
    }

    /**
     * 是否翻转，
     */
    fun isReverse(): Boolean {
        return rootNode.isReverse()
    }

    override fun resume() {
        if (rootNode.getParentNode() != null) {
            throw UnsupportedOperationException("child animate cannot reverse, please use root animate(子动画无法操作reverse，请使用根动画)")
        }
        super.resume()
    }

    override fun pause() {
        if (rootNode.getParentNode() != null) {
            throw UnsupportedOperationException("child animate cannot reverse, please use root animate(子动画无法操作reverse，请使用根动画)")
        }
        super.pause()
    }

    override fun cancel() {
        if (rootNode.getParentNode() != null) {
            throw UnsupportedOperationException("child animate cannot reverse, please use root animate(子动画无法操作reverse，请使用根动画)")
        }
        super.cancel()
    }

    /**
     * 添加到当前节点的子级，链式调用不是添加到当前节点，需要注意
     * @return 返回最新节点
     * ```
     * val NodeB = NodeA.from(B)
     * val NodeC = NodeB.from(C)
     * val NodeD = NodeC.from(D)
     * ```
     * 用法，如果只是想添加到当前节点并返回最新节点，需要多次调用from
     * ```
     * val container = AnimatorContainer()
     * container.from(ObjectAnimator.ofFloat(binding.f1, "translationX", 0f, 100f))
     * container.from(ObjectAnimator.ofFloat(binding.f1, "translationY", 0f, 100f))
     * //{container}
     * //  {translationX}
     * //  {translationY}
     * ```
     * 此时`translationX`和`translationY`会并行
     */
    fun from(animator: ValueAnimator): Builder {
        return Builder(rootNode).from(animator)
    }

    /**
     * 移除动画，如果有链表关系，也处理链表之间的引用关系
     * @return 返回当前节点
     * ```
     * val translationX = ObjectAnimator.ofFloat(binding.f1, "translationX", 0f, 100f)
     * val translationY = ObjectAnimator.ofFloat(binding.f1, "translationY", 0f, 100f)
     * val scaleX = ObjectAnimator.ofFloat(binding.f1, "scaleX", 1f, 1.2f)
     * val scaleY = ObjectAnimator.ofFloat(binding.f1, "scaleY", 1f, 1.2f)
     * val rotationX = ObjectAnimator.ofFloat(binding.f1, "rotationX", 1f, 1.2f)
     * val container = AnimatorContainer()
     * container.from(translationX).next(translationY).next(scaleX).next(rotationX)
     * container.from(translationY).child(scaleY)
     * //{container}
     * //  {translationX}--->{translationY}--->{scaleX}--->{rotationX}
     * //                      {scaleY}
     * container.remove(translationY)
     * //{container}
     * //  {translationX--->{scaleX}--->{rotationX}
     * //
     * container.start()
     * ```
     */
    fun remove(animator: ValueAnimator): Builder {
        return Builder(rootNode).remove(animator)
    }

    /**
     * 延迟执行，本质是一个[Builder.next]
     * @return 返回最新节点
     * ```
     * val NodeB = NodeA.delay(B)
     * val NodeC = NodeB.delay(C)
     * val NodeD = NodeC.delay(D)
     * ```
     * 用法，开始延迟或等待某个动画执行完再延迟
     * ```
     * val container = AnimatorContainer()
     * container.delay(1000).next(ObjectAnimator.ofInt(target,"translationX",0,100))
     * container.from(ObjectAnimator.ofInt(target,"translationY",0,100)).delay(1000).next(ObjectAnimator.ofInt(target,"translationX",0,100))
     * container.start()
     * ```
     */
    fun delay(delay: Long): Builder {
        return Builder(rootNode).delay(delay)
    }

    /**
     * 获取动画节点，只是查询作用，可能返回空，一般用于查找动画的状态，添加监听，获取进度等
     * @param animator 需要查询的动画
     * @param isUpper 是否从根节点开始查询，true 从根节点开始查询，false 从当前节点开始查询
     */
    fun getNodeForAnimator(animator: ValueAnimator, isUpper: Boolean = false): AnimateNode? {
        if (isUpper) {
            var currentNode: AnimateNode? = rootNode
            while (currentNode?.getParentNode() != null) {
                currentNode = currentNode.getParentNode()
            }
            return (currentNode?.animator as? AnimateContainer)?.getNodeForAnimator(animator)
        }
        var node = nodeMap[animator]
        if (node == null) {
            for (value in nodeMap.values) {
                if (value.animator is AnimateContainer) {
                    node = value.animator.getNodeForAnimator(animator)
                    if (node != null) {
                        return node
                    }
                }
            }
        }
        return node
    }

    /**
     * 获取动画节点，如果没有则生成一个
     */
    private fun findNodeForAnimator(animator: ValueAnimator): AnimateNode {
        var node = nodeMap[animator]
        if (node == null) {
            //这里是否要递归查询？
            node = if (animator is AnimateContainer) {
                animator.rootNode
            } else {
                AnimateNode(animator)
            }
            nodeMap[animator] = node
        }
        return node
    }

    /**
     * 添加动画监听，不可重复添加同一个对象
     */
    fun addListener(listener: AnimateListener) {
        rootNode.addListener(listener)
    }

    /**
     * 移除动画监听
     */
    fun removeListener(listener: AnimateListener) {
        rootNode.removeListener(listener)
    }

    /**
     * 不支持
     */
    @Deprecated("unsupported", ReplaceWith("replace addListener(AnimateListener)"))
    override fun addListener(listener: Animator.AnimatorListener?) {
        throw UnsupportedOperationException("replace addListener(AnimateListener)")
    }

    /**
     * 不支持
     */
    @Deprecated("unsupported", ReplaceWith("replace addListener(AnimateListener)"))
    override fun addUpdateListener(listener: AnimatorUpdateListener?) {
        throw UnsupportedOperationException("replace addListener(AnimateListener)")
    }

    /**
     * 不支持
     *
     */
    @Deprecated("unsupported", ReplaceWith("replace addListener(AnimateListener)"))
    override fun addPauseListener(listener: AnimatorPauseListener?) {
        throw UnsupportedOperationException("replace addListener(AnimateListener)")
    }

    /**
     * 内部使用，添加动画进度监听
     */
    private fun superAddUpdateListener(listener: AnimatorUpdateListener?) {
        super.addUpdateListener(listener)
    }

    /**
     * 内部使用，添加动画状态监听
     */
    private fun superAddListener(listener: AnimatorListener?) {
        super.addListener(listener)
    }

    /**
     * 构建动画关系
     * 目前构建的调用api不够满意，可以随时修改，整体逻辑不变
     */
    inner class Builder(private val currentNode: AnimateNode) {

        /**
         * 添加到当前节点的子级，链式调用不是添加到当前节点，需要注意
         * @return 返回最新节点
         * ```
         * val NodeB = NodeA.from(B)
         * val NodeC = NodeB.from(C)
         * val NodeD = NodeC.from(D)
         * ```
         * 用法，如果只是想添加到当前节点并返回最新节点，需要多次调用from
         * ```
         * val container = AnimatorContainer()
         * container.from(ObjectAnimator.ofFloat(binding.f1, "translationX", 0f, 100f))
         * container.from(ObjectAnimator.ofFloat(binding.f1, "translationY", 0f, 100f))
         * //{container}
         * //  {translationX}
         * //  {translationY}
         * ```
         * 此时`translationX`和`translationY`会并行
         */
        internal fun from(animator: ValueAnimator): Builder {
            val node = findNodeForAnimator(animator)
            currentNode.addChildNode(node)
            return Builder(node)
        }

        /**
         * 移除动画，如果有链表关系，也处理链表之间的引用关系
         * @return 返回当前节点
         * ```
         * val NodeA = NodeA.remove(B)
         * val NodeA = NodeB.remove(C)
         * val NodeA = NodeC.remove(D)
         * ```
         * 用法：
         * ```
         * val translationX = ObjectAnimator.ofFloat(binding.f1, "translationX", 0f, 100f)
         * val translationY = ObjectAnimator.ofFloat(binding.f1, "translationY", 0f, 100f)
         * val scaleX = ObjectAnimator.ofFloat(binding.f1, "scaleX", 1f, 1.2f)
         * val scaleY = ObjectAnimator.ofFloat(binding.f1, "scaleY", 1f, 1.2f)
         * val rotationX = ObjectAnimator.ofFloat(binding.f1, "rotationX", 1f, 1.2f)
         * val container = AnimatorContainer()
         * container.from(translationX).next(translationY).next(scaleX).next(rotationX)
         * container.from(translationY).child(scaleY)
         * //{container}
         * //  {translationX}--->{translationY}--->{scaleX}--->{rotationX}
         * //                      {scaleY}
         * container.remove(translationY)
         * //{container}
         * //  {translationX--->{scaleX}--->{rotationX}
         * //
         * container.start()
         * ```
         */
        internal fun remove(animator: ValueAnimator): Builder {
            val node = findNodeForAnimator(animator)
            currentNode.removeChildNode(node)
            return this
        }

        /**
         * 添加到当前节点的父节点的子级，链式调用不是添加到当前节点，需要注意
         * @return 返回父节点
         * 用法，添加到父节点并行执行，可以链式调用with
         * ```
         * val container = AnimatorContainer()
         * container.from(ObjectAnimator.ofInt(target,"translationX",0,100))
         *          .with(ObjectAnimator.ofInt(target,"translationY",0,100))
         * //等价于连续调用两次from
         * //container.from(ObjectAnimator.ofInt(target,"translationX",0,100))
         * //container.from(ObjectAnimator.ofInt(target,"translationY",0,100))
         * container.start()
         * ```
         * 此时`translationX`和`translationY`会并行
         */
        fun with(animator: ValueAnimator): Builder {
            val node = findNodeForAnimator(animator)
            currentNode.getParentNode()?.addChildNode(node)
            currentNode.getPreviousNode()?.addNextNode(node)
            return Builder(node)
        }

        /**
         * 添加到当前节点的子级，链式调用不是添加到当前节点，需要注意
         * @return 返回最新节点
         * ```
         * val NodeB = NodeA.child(B)
         * val NodeC = NodeB.child(C)
         * val NodeD = NodeC.child(D)
         * ```
         * 用法：添加到当前节点并生成子节点，用于构建`AnimationTree`(父子结构)
         * ```
         * val container = AnimatorContainer()
         * container.from(ObjectAnimator.ofInt(target,"translationX",0,100))
         *          .child(ObjectAnimator.ofInt(target,"translationY",0,100))
         *          .child(ObjectAnimator.ofInt(target,"rotation",0,100))
         * container.start()
         * ```
         * 此时，`translationX`是`container`的直接子级，`translationY`是`translationX`的直接子级，`rotation`是`translationY`的直接子级
         * `container`[`translationX`[`translationY`[`rotation`]]]
         */
        fun child(animator: ValueAnimator): Builder {
            val node = findNodeForAnimator(animator)
            currentNode.addChildNode(node)
            return Builder(node)
        }

        /**
         * 设置到当前节点的下一个节点，用于有先后关系的，链式调用不是添加到当前节点，需要注意
         * @return 返回最新节点
         * ```
         * val NodeB = NodeA.next(B)
         * val NodeC = NodeB.next(C)
         * val NodeD = NodeC.next(D)
         * ```
         * 用法：设置到当前节点的下一个节点，并生成新节点，用于构建串行动画
         * ```
         * val container = AnimatorContainer()
         * container.from(ObjectAnimator.ofInt(target,"translationX",0,100))
         *          .next(ObjectAnimator.ofInt(target,"translationY",0,100))
         *          .next(ObjectAnimator.ofInt(target,"rotation",0,100))
         * container.start()
         * ```
         * `container`[`translationX`->`translationY`->`rotation`]`
         */
        fun next(animator: ValueAnimator): Builder {
            val node = findNodeForAnimator(animator)
            currentNode.getParentNode()?.addChildNode(node)
            currentNode.addNextNode(node)
            return Builder(node)
        }

        /**
         * 延迟执行，本质是一个[Builder.next]
         * @return 返回最新节点
         * ```
         * val NodeB = NodeA.delay(B)
         * val NodeC = NodeB.delay(C)
         * val NodeD = NodeC.delay(D)
         * ```
         * 用法，开始延迟或等待某个动画执行完再延迟
         * ```
         * val container = AnimatorContainer()
         * container.delay(1000).next(ObjectAnimator.ofInt(target,"translationX",0,100))
         * container.from(ObjectAnimator.ofInt(target,"translationY",0,100)).delay(1000).next(ObjectAnimator.ofInt(target,"translationX",0,100))
         * container.start()
         * ```
         */
        fun delay(delay: Long): Builder {
            val delayAnimator = DelayTimeAnimator()
            delayAnimator.setFloatValues(0f, 1f)
            delayAnimator.duration = delay
            val node = findNodeForAnimator(delayAnimator)
            currentNode.getParentNode()?.addChildNode(node)
            currentNode.addNextNode(node)
            return Builder(node)
        }
    }

    /**
     * 动画节点，组建动画关系代码注释
     */
    class AnimateNode(internal val animator: ValueAnimator) {
        /**
         * 记录当前动画时长，如果时长发生变化，最终与[longestDuration]值应当保持一致
         */
        private var rememberInitDuration = AnimatorCompat.getTotalDuration(animator)

        /**
         * 子级
         */
        private val childNodes by lazy { ArrayList<AnimateNode>(1) }

        /**
         * 父级
         */
        private var parentNode: AnimateNode? = null

        /**
         * 上一个节点
         */
        private var previousNode: AnimateNode? = null

        /**
         * 下一个节点
         */
        private var nextNodes = ArrayList<AnimateNode>()

        /**
         * 记录最长时长
         */
        private var longestDuration = 0L

        /**
         * 记录前置时长
         */
        private var frontDuration = 0L

        /**
         * 记录[isReverse]的前置时间
         */
        private var backDuration = 0L

        /**
         * 是否翻转
         */
        private var isReverse = false

        /**
         * 是否运行中
         */
        private var isRunning = false

        /**
         * 上一次记录的运行状态，用于避免重复回调相同状态
         */
        private var lastRunning = false

        /**
         * 记录当前重复的次数
         */
        private var rememberRepeatCount = 0

        /**
         * 自己的时长或子节点是否发生了变化，增、删之类
         */
        private var isDurationChange = true

        /**
         * 动画监听
         */
        private val listeners by lazy { ArrayList<AnimateListener>(1) }

        /*TEST CASE API START REGION*/
        //获取当前重复次数
        internal fun getCurrentRepeatCount() = rememberRepeatCount
        /*TEST CASE API END REGION*/

        /**
         * 添加动画监听，不可重复添加同一个对象
         */
        internal fun addListener(listener: AnimateListener) {
            if (listener !in listeners) {
                listeners.add(listener)
            }
        }

        /**
         * 移除动画监听
         */
        internal fun removeListener(listener: AnimateListener) {
            listeners.remove(listener)
        }

        /**
         * 添加子级
         */
        internal fun addChildNode(node: AnimateNode) {
            if (node !in childNodes) {
                childNodes.add(node)
                node.setParentNode(this)
            }
        }

        /**
         * 移除子级元素，如果有链表关系，也处理链表之间的引用关系
         */
        internal fun removeChildNode(node: AnimateNode) {
            if (childNodes.remove(node)) {
                markDurationChange()
                node.getPreviousNode()?.addNextNodes(node.getNextNodes())
                node.setParentNode(null)
                node.setPreviousNode(null)
            }
        }

        /**
         * 设置父级
         */
        internal fun setParentNode(node: AnimateNode?) {
            markDurationChange()
            parentNode = node
        }

        /**
         * 设置上一个节点
         */
        internal fun setPreviousNode(node: AnimateNode?) {
            markDurationChange()
            previousNode = node
        }

        /**
         * 添加下一个节点
         */
        internal fun addNextNode(node: AnimateNode) {
            markDurationChange()
            nextNodes.add(node)
            node.setPreviousNode(this)
            node.setParentNode(this.getParentNode())
        }

        /**
         * 添加下一个节点
         */
        internal fun addNextNodes(nodes: ArrayList<AnimateNode>) {
            markDurationChange()
            nextNodes.addAll(nodes)
            for (node in nodes) {
                node.setPreviousNode(this)
                node.setParentNode(this.getParentNode())
            }
        }

        /**
         * 获取所有子节点
         */
        internal fun getChildNodes(): ArrayList<AnimateNode> {
            return childNodes
        }

        /**
         * 获取父节点
         */
        internal fun getParentNode(): AnimateNode? {
            return parentNode
        }

        /**
         * 获取下一个节点
         */
        internal fun getNextNodes(): ArrayList<AnimateNode> {
            return nextNodes
        }

        /**
         * 获取上一个节点
         */
        internal fun getPreviousNode(): AnimateNode? {
            return previousNode
        }

        /**
         * 标记时长发生了变化(这里只会在子节点增删被动触发，而自己的时长发生变化要等初始化才能判断)
         */
        private fun markDurationChange() {
            isDurationChange = true
        }

        /**
         * 消费时长发了变化(可能是自己，也可能是子节点)
         */
        private fun consumedDurationChange() {
            isDurationChange = false
            rememberInitDuration = AnimatorCompat.getTotalDuration(animator)
        }

        /**
         * 时长是否发生变化(自己或子节点)
         */
        internal fun isDurationChange(): Boolean {
            return isDurationChange || rememberInitDuration != AnimatorCompat.getTotalDuration(animator)
        }

        private fun dispatchAction(beforeAction: ((AnimateNode) -> Unit)? = null, afterAction: ((AnimateNode) -> Unit)? = null) {
            beforeAction?.invoke(this)
            for (childNod in getChildNodes()) {
                childNod.dispatchAction(beforeAction, afterAction)
            }
            afterAction?.invoke(this)
        }

        /**
         * 分发reverse
         */
        internal fun dispatchReverse(isReverse: Boolean) {
            dispatchAction(beforeAction = { node ->
                node.isReverse = isReverse
                node.listeners.forEach { it.onReverse(node, node.isReverse()) }
            })
        }

        /**
         * 分发resume
         */
        internal fun dispatchResume() {
            dispatchAction(beforeAction = { node ->
                node.listeners.forEach { it.onResume(node) }
            })
        }

        /**
         * 分发pause
         */
        internal fun dispatchPause() {
            dispatchAction(beforeAction = { node ->
                node.listeners.forEach { it.onPause(node) }
            })
        }

        /**
         * 分发cancel
         */
        internal fun dispatchCancel() {
            dispatchAction(beforeAction = { node ->
                node.listeners.forEach { it.onCancel(node) }
            })
        }

        /**
         * 检查子节点是否发生了变化
         */
        private fun checkChildChange(): Boolean {
            var isChange = false
            dispatchAction(beforeAction = before@{ node ->
                //初始化动画时，将所有需要的参数重置
                isChange = isChange or node.isDurationChange()
                if (isChange) {
                    return@before
                }
            })
            return isChange
        }

        /**
         * 检查完子节点的变化
         */
        private fun checkedChildChange() {
            dispatchAction(beforeAction = { node ->
                //初始化动画时，将所有需要的参数重置
                node.consumedDurationChange()
            })
        }

        /**
         * 是否翻转
         */
        fun isReverse(): Boolean {
            return isReverse
        }

        /**
         * 是否运行中
         */
        fun isRunning(): Boolean {
            return isRunning
        }

        /**
         * 是否暂停
         */
        fun isPause(): Boolean {
            return getRootNode().animator.isPaused
        }

        /**
         * 动画的值进度
         */
        fun animatedValue(): Any {
            return animator.animatedValue
        }

        /**
         * 动画的时间进度
         */
        fun animatedPlayTime(): Long {
            return animator.currentPlayTime
        }

        /**
         * 初始化动画的一些配置，主要还是设置[AnimateContainer]时长
         * 仅限根节点使用
         * 1.根节点的时长取直接子节点最长的时长
         * 2.其他节点如果是[AnimateContainer]则取所有子节点的总时长
         */
        internal fun initAnimation() {
            assert(parentNode == null) { "只有根节点可以调用这个方法" }
            if (checkChildChange()) {
                initState()
                //初始化时长按顺序执行initDuration->initFrontDuration->initBackDuration
                initDuration()
                initFrontDuration()
                initBackDuration()
                checkedChildChange()
            }
        }

        /**
         * 初始化状态
         */
        private fun initState() {
            dispatchAction(beforeAction = { node ->
                //初始化动画时，将所有需要的参数重置
                node.rememberRepeatCount = 0
            })
        }

        /**
         * 初始化节点的时长
         */
        private fun initDuration() {
            for (childNode in getChildNodes()) {
                childNode.initDuration()
            }
            if (animator is AnimateContainer) {
                if (parentNode == null) {
                    //只有根动画需要添加状态监听
                    animator.removeListener(animatorListener)
                    animator.superAddListener(animatorListener)
                }
                animator.removeUpdateListener(animatorUpdateListener)
                animator.superAddUpdateListener(animatorUpdateListener)
                //重置时长，否则递归获取时长时会错乱
                animator.superSetDuration(0)
                val longestDuration = rememberLongestDuration()
                //如果是根节点，获取max()的时长
                animator.superSetDuration(longestDuration)
                //给动画设置数值，以便有进度
                animator.setIntValues(0, longestDuration.toInt())
            } else {
                rememberLongestDuration()
            }
        }

        /**
         * 初始化每个节点的前置时长
         */
        private fun initFrontDuration() {
            for (childNode in getChildNodes()) {
                childNode.initFrontDuration()
            }
            rememberFrontDuration()
        }

        /**
         * 初始化每个节点的后置时长
         */
        private fun initBackDuration() {
            for (childNode in getChildNodes()) {
                childNode.initBackDuration()
            }
            rememberBackDuration()
        }

        /**
         * 以当前节点为起点，获取整个树最长的时长，包含当前的时长
         */
        private fun rememberLongestDuration(): Long {
            if (animator is AnimateContainer) {
                var childNodeDuration = 0L
                if (getChildNodes().isNotEmpty()) {
                    for (childNode in getChildNodes()) {
                        if (childNode.getPreviousNode() != null) {
                            //优化算法，如果有前节点，那这个节点一定不是最长的
                            continue
                        }
                        childNodeDuration = max(childNodeDuration, childNode.rememberWidthLongestDuration())
                    }
                } else {
                    //AnimateContainer的时长是根据子节点算的，如果没有子节点就为0
                    childNodeDuration = 0
                }
                longestDuration = childNodeDuration
            } else {
                longestDuration = AnimatorCompat.getTotalDuration(animator)
            }
            return longestDuration
        }

        /**
         * 以当前节点为起点，计算整个链的总时长，包含当前的时长
         */
        private fun rememberWidthLongestDuration(): Long {
            var widthLongestDuration = 0L
            for (childNode in getNextNodes()) {
                val childrenLongestDuration = childNode.rememberWidthLongestDuration()
                widthLongestDuration = max(widthLongestDuration, childrenLongestDuration)
            }
            return widthLongestDuration + AnimatorCompat.getTotalDuration(animator)
        }

        /**
         * 记录当前节点的前置时间
         */
        private fun rememberFrontDuration() {
            //这里似乎跟添加子节点的顺序有关，如果添加顺序为[0]，[1]，而遍历时是[1]，[0]则错误。
            //如果这里发生了错误，只能从最末端的节点往前遍历
            frontDuration = if (getPreviousNode() == null) {
                getParentNode()?.frontDuration ?: 0
            } else {
                (getPreviousNode()?.frontDuration ?: 0) + (getPreviousNode()?.longestDuration ?: 0)
            }
        }

        /**
         * 记录当前节点的后置时间
         */
        private fun rememberBackDuration() {
            backDuration = getRootNode().longestDuration - frontDuration - AnimatorCompat.getTotalDuration(animator)
        }

        /**
         * 是否轮到自己执行了
         */
        private fun isTime(playTime: Long): Boolean {
            return if (isReverse()) {
                playTime in backDuration..backDuration + AnimatorCompat.getTotalDuration(animator)
            } else {
                playTime in frontDuration..frontDuration + AnimatorCompat.getTotalDuration(animator)
            }
        }

        /**
         * 是否在执行动画
         */
        private fun isRunning(playTime: Long): Boolean {
            return if (isReverse()) {
                playTime in backDuration until backDuration + AnimatorCompat.getTotalDuration(animator)
            } else {
                playTime in frontDuration until frontDuration + AnimatorCompat.getTotalDuration(animator)
            }
        }

        /**
         * 获取整个动画树的根节点
         */
        private fun getRootNode(): AnimateNode {
            var rootNode: AnimateNode? = getParentNode() ?: return this
            while (rootNode?.getParentNode() != null) {
                rootNode = rootNode.getParentNode()
            }
            return rootNode!!
        }

        /**
         * 分发时间，按动画执行时间设置动画进度
         * 这里有个优化点：
         *              1.目前是按照向上递归获取前置时间，动画层级过多或者子级过多，每个都遍历会耗性能。
         *              有能力的话可以计算好每个节点消耗的时间，有剩余的时间再给子级分发
         * @param playTime 根动画的执行时间
         */
        private fun dispatchPlayTime(playTime: Long) {
            isRunning = isRunning(playTime)
            retryChangeStartState()
            retryChangeRepeat(playTime)
            val calculate = when {
                //是否已经到当前动画执行了
                isTime(playTime) -> {
                    calculateRunningDuration(playTime)
                }
                //如果不是当前动画执行，但是currentPlayTime既不是未开始也不是结束，则计算补偿时长
                animator.currentPlayTime in 0L..AnimatorCompat.getTotalDuration(animator) -> {
                    calculateCompensatoryDuration(playTime)
                }
                //未知情况
                else -> CalculateDuration.Invalid
            }
            when (calculate) {
                is CalculateDuration.Effective -> {
                    if (animator is AnimateContainer) {
                        setContainerPlayTime(calculate.duration)
                    } else {
                        setCurrentPlayTime(calculate.duration)
                    }
                }
                is CalculateDuration.Compensatory -> {
                    setCompensatoryPlayTime(calculate.duration)
                }
                else -> Unit
            }
            dispatchPlayTime(this, playTime)
            retryChangeEndState()
            lastRunning = isRunning
        }

        /**
         * 计算运行中的值
         */
        private fun calculateRunningDuration(playTime: Long): CalculateDuration.Effective {
            val surplusTime = if (isReverse()) {
                //我自己也看不懂这段怎么计算的
                AnimatorCompat.getTotalDuration(getRootNode().animator) - playTime - frontDuration
            } else {
                playTime - frontDuration
            }
            val duration = if (surplusTime - AnimatorCompat.getTotalDuration(animator) > 0) {
                AnimatorCompat.getTotalDuration(animator)
            } else {
                surplusTime
            }
            return CalculateDuration.Effective(duration)
        }

        /**
         * 计算补偿的时长。
         * 例如一个动画的时长为300毫秒，但动画最终执行完可能是299毫秒或301毫秒这样无法对齐的时长，
         * 301毫秒这种无需处理，因为它已经执行完整只是超过了，而299毫秒在数据层面是不完整的，会导致属性无法正确的计算
         * 所以这里做一个补偿计算，如果动画未开始则[ValueAnimator.setCurrentPlayTime]置为0，
         * 如果动画已经结束，则[ValueAnimator.setCurrentPlayTime]置为动画本身的[ValueAnimator.getDuration]
         * @return 如果返回的是[CalculateDuration.Invalid]则无需处理
         */
        private fun calculateCompensatoryDuration(playTime: Long): CalculateDuration {
            return if (isReverse()) {
                if (playTime <= backDuration) {
                    CalculateDuration.Compensatory.Max(AnimatorCompat.getTotalDuration(animator))
                } else if (playTime >= backDuration + AnimatorCompat.getTotalDuration(animator)) {
                    CalculateDuration.Compensatory.Min
                } else {
                    CalculateDuration.Invalid
                }
            } else {
                if (playTime <= frontDuration) {
                    CalculateDuration.Compensatory.Min
                } else if (playTime >= frontDuration + AnimatorCompat.getTotalDuration(animator)) {
                    CalculateDuration.Compensatory.Max(AnimatorCompat.getTotalDuration(animator))
                } else {
                    CalculateDuration.Invalid
                }
            }
        }

        /**
         * 给子节点和next节点分发时间
         */
        private fun dispatchPlayTime(node: AnimateNode, playTime: Long) {
            //给子级分发时间
            node.getChildNodes().forEach { it.dispatchPlayTime(playTime) }
        }

        /**
         * 给普通动画设置时间进度并且回调
         */
        private fun setCurrentPlayTime(playTime: Long) {
            //因为代码执行总需要一些时间所以playTime可能会比duration多几毫秒，这里做个修正
            val fixPlayTime = if (playTime > AnimatorCompat.getTotalDuration(animator)) {
                AnimatorCompat.getTotalDuration(animator)
            } else if (playTime < 0) {
                0
            } else {
                playTime
            }
            animator.currentPlayTime = fixPlayTime
            listeners.forEach { it.onUpdate(this, animatedValue(), fixPlayTime) }
        }

        /**
         * 设置补偿时间
         */
        private fun setCompensatoryPlayTime(fixPlayTime: Long) {
            animator.currentPlayTime = fixPlayTime
        }

        /**
         * 给容器节点回调进度，因为容器本身没有数值，所以以百分比作为数值
         */
        private fun setContainerPlayTime(playTime: Long) {
            listeners.forEach {
                val duration = AnimatorCompat.getTotalDuration(animator)
                val value = if (duration <= 0) 0 else playTime.toFloat() / duration
                it.onUpdate(this, value, playTime)
            }
        }

        /**
         * 尝试更新开始状态
         */
        private fun retryChangeStartState() {
            //根动画不在这里处理回调
            if (parentNode != null && lastRunning != isRunning) {
                if (isRunning) {
                    listeners.forEach { it.onStart(this) }
                }
            }
        }

        /**
         * 尝试更新结束状态
         */
        private fun retryChangeEndState() {
            //根动画不在这里处理回调
            if (parentNode != null && lastRunning != isRunning) {
                if (!isRunning) {
                    listeners.forEach { it.onEnd(this) }
                }
            }
        }

        /**
         * 尝试更新重复次数
         * 根动画需要特殊处理，在原生更新动画时长的时候就需要调用一次。
         */
        private fun retryChangeRepeat(playTime: Long) {
            if (!isTime(playTime)) {
                return
            }
            val totalDuration = AnimatorCompat.getTotalDuration(animator)
            val currentPlayTime = calculateRunningDuration(playTime).duration
            val repeatCount = AnimatorCompat.repeatCount(animator)
            if (totalDuration <= 0) {
                if(rememberRepeatCount < repeatCount) {
                    //如果时长为0，也要保证回调的次数以及记录的次数与正常时无异
                    for (i in 0 until animator.repeatCount + 1) {
                        rememberRepeatCount++
                        listeners.forEach { it.onRepeat(this, rememberRepeatCount, repeatCount) }
                    }
                }
                return
            }
            val currentRepeatCount = if (currentPlayTime < totalDuration) {
                (currentPlayTime / animator.duration).toInt() + 1
            } else {
                (currentPlayTime / animator.duration).toInt()
            }
            if (currentRepeatCount > rememberRepeatCount && currentPlayTime <= totalDuration) {
                rememberRepeatCount = currentRepeatCount
                listeners.forEach { it.onRepeat(this, currentRepeatCount, repeatCount) }
            }
        }

        /**
         * 动画更新
         * ```
         * 1. 从根动画监听到的时间，主动给子动画分发，驱动子动画执行
         * ```
         * ```
         * 2. 根动画的重复次数执行逻辑和子动画重复次数执行逻辑不一样。例如：
         * 子动画时长为300，重复次数为2，时长就是300*2=600，那根动画的时长也就是600
         * 如果这时候给根动画也设置重复次数为2，根动画的总时长就是600*2=1200
         * 如果按照1200的时长去分发，子动画执行到600之后就没有可执行的动画了
         * 所以每600要重新归零分发
         * ```
         */
        private val animatorUpdateListener = AnimatorUpdateListener { animation ->
            retryChangeRepeat(animation.currentPlayTime)
            val totalDuration = AnimatorCompat.getTotalDuration(animation)
            if (animation.currentPlayTime >= totalDuration) {
                dispatchPlayTime(totalDuration)
            } else {
                dispatchPlayTime(animation.currentPlayTime % animation.duration)
            }
        }

        /**
         * 根动画状态监听
         */
        private val animatorListener = object : AnimatorListenerAdapter() {
            private var isCancel = false
            override fun onAnimationStart(animation: Animator) {
                isCancel = false
                listeners.forEach { it.onStart(this@AnimateNode) }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (isCancel) {
                    //判断是否通过cancel结束动画的，是则不继续调用End
                    isCancel = false
                    return
                }
                //如果主动调用end，onUpdate收到的currentPlayTime不会变，变的是duration，
                //整个动画是靠currentPlayTime驱动的，所以在这里补充一下，置为结束时间，但这样正常的结束就会多收到一次onUpdate，应该没关系吧
                dispatchPlayTime(AnimatorCompat.getTotalDuration(animation))
                listeners.forEach { it.onEnd(this@AnimateNode) }
            }

            override fun onAnimationCancel(animation: Animator) {
                isCancel = true
                dispatchCancel()
            }

            override fun onAnimationPause(animation: Animator) {
                dispatchPause()
            }

            override fun onAnimationResume(animation: Animator) {
                dispatchResume()
            }
        }
    }
}