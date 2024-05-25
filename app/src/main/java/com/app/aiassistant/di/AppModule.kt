package com.app.aiassistant.di

import com.app.aiassistant.NetworkConnectionManager
import com.app.aiassistant.NetworkConnectionManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {
    companion object {
        @Provides
        @Singleton
        fun provideCoroutineScope() =
            CoroutineScope(Dispatchers.Default + SupervisorJob())
    }

    @Binds
    abstract fun bindNetworkConnectionManager(networkConnectionManagerImpl: NetworkConnectionManagerImpl): NetworkConnectionManager
}