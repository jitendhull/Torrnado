package tech.jitendhull.torrnado.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.jitendhull.torrnado.data.repository.TorrentRepositoryImpl
import tech.jitendhull.torrnado.domain.repository.TorrentRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTorrentRepository(
        torrentRepositoryImpl: TorrentRepositoryImpl
    ): TorrentRepository
}