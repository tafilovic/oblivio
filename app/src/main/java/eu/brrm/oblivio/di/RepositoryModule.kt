package eu.brrm.oblivio.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import eu.brrm.oblivio.data.repository.AuthRepositoryImpl
import eu.brrm.oblivio.data.local.AuthSessionStore
import eu.brrm.oblivio.data.local.AuthTokenDataSource
import eu.brrm.oblivio.data.repository.HomeRepositoryImpl
import eu.brrm.oblivio.data.repository.NotificationRepositoryImpl
import eu.brrm.oblivio.data.repository.ProfileRepositoryImpl
import eu.brrm.oblivio.data.repository.SplashRepositoryImpl
import eu.brrm.oblivio.data.repository.UserPreferencesRepositoryImpl
import eu.brrm.oblivio.domain.repository.AuthRepository
import eu.brrm.oblivio.domain.repository.HomeRepository
import eu.brrm.oblivio.domain.repository.NotificationRepository
import eu.brrm.oblivio.domain.repository.ProfileRepository
import eu.brrm.oblivio.domain.repository.SplashRepository
import eu.brrm.oblivio.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindSplashRepository(impl: SplashRepositoryImpl): SplashRepository

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    abstract fun bindAuthSessionStore(impl: AuthTokenDataSource): AuthSessionStore
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { File(context.filesDir, "oblivio.preferences_pb") },
        )
    }
}
