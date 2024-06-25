package com.mapsted.mapstedtceight.utils

import android.animation.Animator

open class AnimatorListenerImpl : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator) = Unit
    override fun onAnimationEnd(animation: Animator) = Unit
    override fun onAnimationCancel(animation: Animator) = Unit
    override fun onAnimationRepeat(animation: Animator) = Unit
}