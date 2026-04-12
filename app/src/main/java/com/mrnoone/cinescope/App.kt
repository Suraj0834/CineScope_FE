package com.mrnoone.cinescope

import android.app.Application
import com.mrnoone.cinescope.data.api.ApiClient

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        ApiClient.initialize(this)
        com.mrnoone.cinescope.ailang.AiLang.init(this)
    }
}
