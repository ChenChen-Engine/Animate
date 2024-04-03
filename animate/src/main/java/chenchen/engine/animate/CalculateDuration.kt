package chenchen.engine.animate

/**
 * @author: chenchen
 * @since: 2024/4/3 19:28
 */
sealed class CalculateDuration {

    /**
     * 计算出有效的时长
     */
    class Effective(val duration: Long) : CalculateDuration()

    /**
     * 计算出补偿的时长
     */
    sealed class Compensatory(val duration: Long) : CalculateDuration() {
        /**
         * 补偿最小的时长
         */
        object Min : Compensatory(0)

        /**
         * 补偿最大的时长
         */
        class Max(duration: Long) : Compensatory(duration)
    }

    /**
     * 计算出无效的时长
     */
    object Invalid : CalculateDuration()
}