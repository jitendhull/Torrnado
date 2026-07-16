package tech.jitendhull.torrnado.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import tech.jitendhull.torrnado.data.settings.SettingsManager
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideProxySelector(settingsManager: SettingsManager): ProxySelector {
        return object : ProxySelector() {
            override fun select(uri: URI?): List<Proxy> {
                val enabled = runBlocking { settingsManager.proxyEnabled.first() }
                if (!enabled) return listOf(Proxy.NO_PROXY)

                val host = runBlocking { settingsManager.proxyHost.first() }
                val port = runBlocking { settingsManager.proxyPort.first() }
                val type = runBlocking { settingsManager.proxyType.first() }

                if (host.isBlank()) return listOf(Proxy.NO_PROXY)

                return try {
                    val proxyType = if (type.equals("SOCKS", ignoreCase = true)) {
                        Proxy.Type.SOCKS
                    } else {
                        Proxy.Type.HTTP
                    }
                    listOf(Proxy(proxyType, InetSocketAddress(host, port)))
                } catch (e: Exception) {
                    listOf(Proxy.NO_PROXY)
                }
            }

            override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
                // No-op
            }
        }
    }

    @Provides
    @Singleton
    fun provideProxyAuthenticator(settingsManager: SettingsManager): Authenticator {
        return Authenticator { _, response ->
            val enabled = runBlocking { settingsManager.proxyEnabled.first() }
            if (!enabled) return@Authenticator null

            val user = runBlocking { settingsManager.proxyUser.first() }
            val pass = runBlocking { settingsManager.proxyPass.first() }

            if (user.isBlank()) return@Authenticator null

            val credential = Credentials.basic(user, pass)
            response.request.newBuilder()
                .header("Proxy-Authorization", credential)
                .build()
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        proxySelector: ProxySelector,
        proxyAuthenticator: Authenticator
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .proxySelector(proxySelector)
            .proxyAuthenticator(proxyAuthenticator)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}