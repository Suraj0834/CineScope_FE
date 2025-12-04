package com.cinescope.app

import android.app.Application
import com.cinescope.app.data.api.ApiClient

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        ApiClient.initialize(this)
        com.cinescope.app.ailang.AiLang.init(this)
    }
}
