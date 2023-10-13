# 动画库

可以便捷的组合、嵌套、串行并行各种类型的动画，提供了接口化的形式简化自定义动画的复杂程度

支持：
- [-] 支持查询动画是否在运行`isRunning`
- [-] 支持每个动画监听自己的动画进度，以及开始和结束，其他状态的监听则是从顶级动画作用域分发下来的
- [-] 顶级动画作用域支持`start`、`pause`、`resume`、`resverse`、`cancel`、`end`、`setCurrentPlayTime`等控制
- [-] 支持线性的组合动画顺序，通过`next`、`with`组合，原生动画是非线性的，需要理解成本
- [-] 支持子级动画作用域，可细分动画作用域，灵活构建
- [-] 对`View`进行了通用的扩展，可以直接通过`View`构建动画
不支持：
- 不支持子动画单独控制，统一由顶级动画作用域控制
- 不支持子动画查询状态，除了`isRunning`外，`isPause`或其他状态都为顶级动画作用域的状态
- 不支持子动画复用，构建一个动画作用域，里面的子动画都应该是声明式的，声明完不单独持有引用
- 不支持设置总时长，所有时长均由子动画独立设置，最后统计所有时长则为总时长
- 不支持子动画单独设置延时，可通过`delay`控制
- 不支持`repeat`

## 使用方式

#### 并行多个动画方式1：

```kotlin
fun testAnimated(target: View) {
    animatedScope {
        //最直接的方式
        animatedWidth(target, 1000)
        animatedHeight(traget, 1000)
    }
}
```

#### 并行多个动画方式2：

```kotlin
fun testAnimated(target: View) {
    animatedScope {
        //使用「with」连接符并行
        animatedWidth(target, 1000) with animatedHeight(traget, 1000)
    }
}
```

#### 串行多个动画方式：

```kotlin
fun testAnimated(target: View) {
    animatedScope {
        //使用「next」连接符串行
        animatedWidth(target, 1000) next animatedHeight(traget, 1000)
    }
}
```

#### 连接中延时执行动画：

```kotlin
fun testAnimated(target: View) {
    animatedScope {
        //使用「delay」连接符延时
        animatedWidth(target, 1000) delay 1000 next animatedHeight(traget, 1000)
    }
}
```

#### 嵌套动画：

```kotlin
fun testAnimated(target: View) {
    animatorScope {
        //「animatedBackground」和「animatedTranslationX」会同时执行，但是要等「animatedTranslationX」执行完之后才执行「animatedRotation」
        animatedBackground(target, 0xFF000000.toInt(), 0xFFFFFF00.toInt()) {
            duration = 2000
            animatedTranslationX(target, 0f, 500f) {
                duration = 5000
            }
        } next animatedRotation(target, 0f, 500f)
    }
}
```

#### 组合多个动画链

```kotlin
fun testAnimated(target: View) {
    animatorScope {
        link1 next link2
    }
}

fun AnimatorScope.link1(view: View) = subAnimateScope {
    animatedWidth(view, 100) with animatedHeight(view, 100)
}

fun AnimatorScope.link2(view: View) = subAnimateScope {
    animatedTranslationX(view, 100f) with animatedTranslationY(view, 100f)
}
```

#### 内置View扩展动画

```kotlin
fun testAnimated(target: View) {
    //所有内置动画都会对View扩展，所以直接使用就行
    target.animatedWidth(width = 100, duration = 1000)
}
```

## 注意事项

#### 不要复用动画对象

```kotlin
fun testAnimated(target: View) {
    val anim1 = animatedWidth(target, 1000)
    val anim2 = animatedHeight(target, 1000)
    val anim3 = animatedTranslationX(traget, 1000f)

    anim1 next anim2
    //不能复用，anim1和anim2已经持有同一个link，接下来anim1又会持有anim3的link，会导致动画顺序错乱
    //而且原生的API也不支持一个动画同一时间多次使用
    anim3 with anim1
}
```

#### 顶级动画作用域不可连接

```kotlin
fun testAnimated(target: View) {
    val animatorScope1 = animatorScope {
        animatedWidth(target, 1000)
    }
    val animatorScope2 = animatorScope {
        animatedHeight(target, 1000)
    }
    //作用域不可以连接作用域，每个作用于都是独立的启动器，每完成一个作用于的构造，就会自动启动，
    animatorScope1 next animatorScope2

    //有一种例外没有测试过
    val animatorScope3 = animatorScope(false) {
        animatedWidth(target, 1000)
    }
    val animatorScope4 = animatorScope(false) {
        animatedHeight(target, 1000)
    }
    //不让作用域自动启动或许可以连接
    animatorScope3 next animatorScope4
}
```

#### View扩展动画不可连接

```kotlin
fun testAnimated(target: View) {
    //因为View的扩展动画都是立即启动的，没法连接
    target.animatedWidth(1000) next animatorScope {
        animatedHeight(target, 1000)
    }
}
```

#### 使用延迟连接符后需要使用`next`连接符
```kotlin
fun testAnimated(target: View) {
    //因为「delay」的实现其实是使用了属性动画完成的，「delay」之后不「next」，则会一起并行
    animatedWidth(target, 1000) delay 1000 next animatedHeight(traget, 1000)
}
```

## 连接符

|api|作用|
|:-:|:-:|
|`next`         |动画与动画之间可以组合为**串行关系**|
|`with`         |动画与动画之间可以组合为**并行关系**|
|`delay`        |在上一个动画执行完后**延时执行**下一个动画|

## 动画API

|api|作用|
|:-:|:-:|
|`start`            |开始动画|
|`resume`           |恢复动画|
|`pause`            |暂停动画|
|`end`              |跳到动画结束|
|`cancel`           |取消动画|
|`reverse`          |翻转动画，暂未实现|
|`isStart`          |动画是否开始|
|`isPause`          |动画是否暂停|
|`isRunning`        |动画是否运行|
|`interpolator`     |设置插值器，默认`LinearInterpolator`|
|`duration`         |设置动画时长，默认300|
|`onCancel`       |监听动画取消执行，只能设置一个|
|`onResume`       |监听动画恢复执行，只能设置一个|
|`onPause`        |监听动画暂停执行，只能设置一个|
|`onStart`        |监听动画开始执行，只能设置一个|
|`onEnd`          |监听动画结束执行，只能设置一个|

## 自定义属性动画

参考`WindowDimAmountAnimateScope`和`IntAnimateScope`

## AnimatorContainer使用示例
```kotlin
val translationX = ObjectAnimator.ofFloat(binding.f1, "translationX", 0f, 200f).apply {
    duration = 2000
}
val translationY = ObjectAnimator.ofFloat(binding.f1, "translationY", 0f, 200f).apply {
    duration = 1000
}
val rotation = ObjectAnimator.ofFloat(binding.f1, "rotation", 0f, 200f).apply {
    duration = 1000
}
val scaleX = ObjectAnimator.ofFloat(binding.f1, "scaleX", 1f, 1.2f).apply {
    duration = 2000
}
val scaleY = ObjectAnimator.ofFloat(binding.f1, "scaleY", 1f, 1.2f).apply {
    duration = 2000
}
val container = AnimatorContainer()
val child = container.from(translationX)
child.child(translationY).child(rotation)
container.from(scaleX).next(scaleY)
container.start()
```