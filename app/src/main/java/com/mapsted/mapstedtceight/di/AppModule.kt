package com.mapsted.mapstedtceight.di

import android.content.Context
import com.mapsted.mapstedtceight.BuildConfig
import com.mapsted.mapstedtceight.network.NetworkAPI
import com.mapsted.mapstedtceight.session.AppDataStore
import com.mapsted.mapstedtceight.session.SessionPreferences
import com.mapsted.mapstedtceight.session.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Named("baseURL")
    fun baseUrl(): String = BuildConfig.APP_SERVER_URL

    @Provides
    @Singleton
    fun provideNetworkAPI(@Named("baseURL") baseUrl: String, @ApplicationContext context: Context, appDataStore: AppDataStore): NetworkAPI {
        return NetworkAPI(baseUrl, context, appDataStore)
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun provideSessionPreferences(@ApplicationContext context: Context): SessionPreferences {
        return SessionPreferences(context)
    }

    @Provides
    @Singleton
    fun provideAppDataStore(@ApplicationContext context: Context): AppDataStore {
        return AppDataStore(context, AppPreferences(context), SessionPreferences(context))
    }
}