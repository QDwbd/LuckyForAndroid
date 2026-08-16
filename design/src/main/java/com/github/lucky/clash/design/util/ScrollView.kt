package com.github.lucky.clash.design.util

import com.github.lucky.clash.design.view.ObservableScrollView

val ObservableScrollView.isTop: Boolean
    get() = scrollX == 0 && scrollY == 0
