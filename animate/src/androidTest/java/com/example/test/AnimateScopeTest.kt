package com.example.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import chenchen.engine.animate.animateScope
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author: chenchen
 * @since: 2024/4/16 9:48
 */
@RunWith(AndroidJUnit4::class)
class AnimateScopeTest {
    /**
     * 空子动画/空子节点
     */
    @Test
    fun emptySubAnimate() {
        animateScope { }
    }
}