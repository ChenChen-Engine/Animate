package chenchen.engine.animate

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import chenchen.engine.animate.demo.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTestBinding.inflate(layoutInflater) }
    private var scope: AnimateScope? = null
    private var container: AnimateContainer? = null
    private var animatorSet: AnimatorSet? = null
    private val translationX by lazy {
        ObjectAnimator.ofFloat(binding.f1, "translationX", 0f, 100f).apply {
            duration = 2000
        }
    }
    private val translationY by lazy {
        ObjectAnimator.ofFloat(binding.f1, "translationY", 0f, 100f).apply {
            duration = 2000
        }
    }
    private val scaleX by lazy {
        ObjectAnimator.ofFloat(binding.f1, "scaleX", 1f, 1.2f).apply {
            duration = 1000
        }
    }
    private val scaleY by lazy {
        ObjectAnimator.ofFloat(binding.f1, "scaleY", 1f, 1.2f).apply {
            duration = 1000
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onInitReady()
    }

    private fun onInitReady() {
        binding.f1.setOnClickListener {
//            testAnimatorScope1()
//            testAnimatorScope2()
            testAnimatorScope3()
//            testAnimatorContainer1()
//            testAnimatorContainer2()
//            testAnimatorContainer3()
//            testAnimatorSet1()
//            testAnimatorSet2()
//            testAnimatorSet3()
        }
        binding.tvReverse.setOnClickListener {
            reverse()
        }
    }

    private fun testAnimatorScope1() {
        if (scope == null) {
            scope = animateScope(true) {
                animateTranslationX(binding.f1, 0f, 100f) {
                    duration = 2000
                } delay (500) next animateScaleX(binding.f1, 1f, 1.2f) {
                    duration = 1000
                }
                animateTranslationY(binding.f1, 0f, 100f) {
                    duration = 2000
                } delay (500) next animateScaleY(binding.f1, 1f, 1.2f) {
                    duration = 1000
                }
            }
        }
        scope?.start()
    }

    private fun testAnimatorScope2() {
        if (scope == null) {
            scope = animateScope(true) {
                subAnimateScope {
                    animateTranslationX(binding.f1, 0f, 100f) {
                        duration = 2000
                    } delay (500) next animateScaleX(binding.f1, 1f, 1.2f) {
                        duration = 1000
                    }
                } next subAnimateScope {
                    animateTranslationY(binding.f1, 0f, 100f) {
                        duration = 2000
                    } delay (500) next animateScaleY(binding.f1, 1f, 1.2f) {
                        duration = 1000
                    }
                }
            }
        }
        scope?.start()
    }

    private fun testAnimatorScope3() {
        if (scope == null) {
            scope = animateScope(true) {
                subAnimateScope {
                    subAnimateScope {
                        animateTranslationX(binding.f1, 0f, 100f) {
                            duration = 2000
                        } delay (500) next animateScaleX(binding.f1, 1f, 1.2f) { duration = 1000 }
                    }
                } next subAnimateScope {
                    subAnimateScope {
                        animateTranslationY(binding.f1, 0f, 100f) {
                            duration = 2000
                        } delay (500) next animateScaleY(binding.f1, 1f, 1.2f) { duration = 1000 }
                    }
                }
            }
        }
        scope?.start()
    }


    private fun testAnimatorContainer1() {
        if (container == null) {
            container = AnimateContainer()
            container?.apply {
                from(translationX).delay(500).next(scaleX)
                from(translationY).delay(500).next(scaleY)
            }
        }
        container?.start()
    }

    private fun testAnimatorContainer2() {
        if (container == null) {
            container = AnimateContainer()
            container?.apply {
                from(AnimateContainer().apply {
                    from(translationX).delay(500).next(scaleX)
                    from(translationY).delay(500).next(scaleY)
                }).delay(500).next(AnimateContainer().apply {
                    from(translationX).delay(500).next(scaleX)
                    from(translationY).delay(500).next(scaleY)
                })
            }
        }
        container?.start()
    }

    private fun testAnimatorContainer3() {
        if (container == null) {
            container = AnimateContainer()
            container?.apply {
                from(AnimateContainer().apply {
                    from(AnimateContainer().apply {
                        from(translationX.clone()).delay(500).next(scaleX.clone())
                        from(translationY.clone()).delay(500).next(scaleY.clone())
                    })
                }).delay(500).next(AnimateContainer().apply {
                    from(AnimateContainer().apply {
                        from(translationX.clone()).delay(500).next(scaleX.clone())
                        from(translationY.clone()).delay(500).next(scaleY.clone())
                    })
                })
            }
        }
        container?.start()
    }

    private fun testAnimatorSet1() {
        if (animatorSet == null) {
            animatorSet = AnimatorSet().apply {
                play(AnimatorSet().apply {
                    play(translationX).after(500).after(scaleX)
                    play(translationY).after(500).after(scaleY)
                }).after(500).after(AnimatorSet().apply {
                    play(translationX).after(500).after(scaleX)
                    play(translationY).after(500).after(scaleY)
                })
                var start = 0L
                addListener(onEnd = {
                    Log.d(
                        "AnimatorContainer",
                        "testAnimatorSet3: duration = ${System.currentTimeMillis() - start}"
                    )
                }, onStart = {
                    start = System.currentTimeMillis()
                })
            }
        }
        animatorSet?.start()
    }

    private fun testAnimatorSet2() {
        if (animatorSet == null) {
            animatorSet = AnimatorSet().apply {
                play(AnimatorSet().apply {
                    play(translationX).after(500).after(scaleX)
                    play(translationY).after(500).after(scaleY)
                }).after(500).after(AnimatorSet().apply {
                    play(translationX).after(500).after(scaleX)
                    play(translationY).after(500).after(scaleY)
                })
                var start = 0L
                addListener(onEnd = {
                    Log.d(
                        "AnimatorContainer",
                        "testAnimatorSet3: duration = ${System.currentTimeMillis() - start}"
                    )
                }, onStart = {
                    start = System.currentTimeMillis()
                })
            }
        }
        animatorSet?.start()
    }

    private fun testAnimatorSet3() {
        if (animatorSet == null) {
            animatorSet = AnimatorSet().apply {
                play(AnimatorSet().apply {
                    play(AnimatorSet().apply {
                        play(translationX).after(500).after(scaleX)
                        play(translationY).after(500).after(scaleY)
                    })
                }).after(500).after(AnimatorSet().apply {
                    play(AnimatorSet().apply {
                        play(translationX).after(500).after(scaleX)
                        play(translationY).after(500).after(scaleY)
                    })
                })
                var start = 0L
                addListener(onEnd = {
                    Log.d(
                        "AnimatorContainer",
                        "testAnimatorSet3: duration = ${System.currentTimeMillis() - start}"
                    )
                }, onStart = {
                    start = System.currentTimeMillis()
                })
            }
        }
        animatorSet?.start()
    }

    private fun reverse() {
        container?.reverse()
        scope?.reverse()
    }
}