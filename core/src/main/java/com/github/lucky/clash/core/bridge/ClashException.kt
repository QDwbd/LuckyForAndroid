package com.github.lucky.clash.core.bridge

import androidx.annotation.Keep

@Keep
class ClashException(msg: String) : IllegalArgumentException(msg)