package com.agar.commander.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.agar.commander.model.ConnectionProfile
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.connectionProfileDataStore by preferencesDataStore(
    name = "connection_profile",
)

class ConnectionProfileRepository(context: Context) {
    private val dataStore = context.applicationContext.connectionProfileDataStore

    val profile: Flow<ConnectionProfile> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences ->
            ConnectionProfile(
                host = preferences[Keys.Host] ?: ConnectionProfile().host,
                port = preferences[Keys.Port] ?: ConnectionProfile().port,
                autoConnect = preferences[Keys.AutoConnect] ?: ConnectionProfile().autoConnect,
            )
        }

    suspend fun save(profile: ConnectionProfile) {
        require(profile.isValid) { "Connection profile is invalid" }
        dataStore.edit { preferences ->
            preferences[Keys.Host] = profile.host.trim()
            preferences[Keys.Port] = profile.port
            preferences[Keys.AutoConnect] = profile.autoConnect
        }
    }

    private object Keys {
        val Host = stringPreferencesKey("host")
        val Port = intPreferencesKey("port")
        val AutoConnect = booleanPreferencesKey("auto_connect")
    }
}
