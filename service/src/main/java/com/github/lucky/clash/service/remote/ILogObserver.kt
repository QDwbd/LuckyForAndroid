package com.github.lucky.clash.service.remote

import com.github.lucky.clash.core.model.LogMessage
import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface ILogObserver {
    fun newItem(log: LogMessage)
}