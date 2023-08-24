package chenchen.engine.animate

import android.view.View
import chenchen.engine.animate.animateHeight
import chenchen.engine.animate.animateScope
import chenchen.engine.animate.animateWidth
import chenchen.engine.animate.subAnimateScope

/**
 * Animate串行使用
 * @author: chenchen
 * @since: 2022/4/28 22:27
 */
fun animateCase1(view1: View, view2: View) {
    animateScope {
        animateWidth(view1) {
            duration = 2000
        } next animateHeight(view2) {
            duration = 2000
        }
    }
}

/**
 * Animate并行使用
 */
fun animateCase2(view1: View, view2: View) {
    animateScope {
        animateWidth(view1) {
            duration = 2000
        } with animateHeight(view2) {
            duration = 2000
        }
    }
}

/**
 * Animate延迟使用
 */
fun animateCase3(view1: View, view2: View) {
    animateScope {
        animateWidth(view1) {
            duration = 2000
        } delay (1000) next animateHeight(view2) {
            duration = 2000
        }
    }
}

/**
 * Animate子动画使用
 */
fun animateCase4(view1: View, view2: View) {
    animateScope {
        subAnimateScope {
            animateWidth(view1) {
                duration = 2000
            } delay (1000) next animateHeight(view2) {
                duration = 2000
            }
        } next subAnimateScope {
            animateWidth(view1) {
                duration = 2000
            } delay (1000) next animateHeight(view2) {
                duration = 2000
            }
        }
    }
}

/**
 * View的扩展动画用法
 */
fun viewAnimateCase(view1: View, view2: View) {
    //方式一，不同的View动画拼接，不用中缀表达式
    view1.animateWidth(350, 2000).delay(1000).with(view2.animateHeight(350, 2000)).start()

    //方式一，不同的View动画拼接，使用中缀表达式
    (view1.animateWidth(350, 2000) delay (1000) with (view2.animateHeight(350, 2000))).start()

    //方式二：同一个View动画拼接，省去重复调用view
    view1.run {
        animateWidth(350, 2000) delay 100 next animateHeight(350, 2000)
    }.start()

    //方式二：同一个View动画拼接，重复调用View
    (view1.animateWidth(350, 2000) delay 100 next view1.animateHeight(350, 2000)).start()
}