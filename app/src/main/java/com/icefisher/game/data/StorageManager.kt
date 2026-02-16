package com.icefisher.game.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ice_fisher_storage")

class StorageManager(private val context: Context) {

    private val tokenKey = stringPreferencesKey("auth_token")
    private val linkKey = stringPreferencesKey("content_link")
    private val policyKey = stringPreferencesKey("policy_link")

    suspend fun getToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[tokenKey]
        }.first()
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun getLink(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[linkKey]
        }.first()
    }

    suspend fun saveLink(link: String) {
        context.dataStore.edit { preferences ->
            preferences[linkKey] = link
        }
    }

    suspend fun getPolicyLink(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[policyKey]
        }.first()
    }

    suspend fun savePolicyLink(link: String) {
        context.dataStore.edit { preferences ->
            preferences[policyKey] = link
        }
    }

    suspend fun getMaxUnlockedLevel(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[intPreferencesKey("max_unlocked_level")] ?: 0
        }.first()
    }

    suspend fun unlockLevel(level: Int) {
        val current = getMaxUnlockedLevel()
        if (level > current) {
            context.dataStore.edit { preferences ->
                preferences[intPreferencesKey("max_unlocked_level")] = level
            }
        }
    }

    suspend fun getBestScore(levelIndex: Int): Int {
        return context.dataStore.data.map { preferences ->
            preferences[intPreferencesKey("best_score_$levelIndex")] ?: 0
        }.first()
    }

    suspend fun saveBestScore(levelIndex: Int, score: Int) {
        val current = getBestScore(levelIndex)
        if (score > current) {
            context.dataStore.edit { preferences ->
                preferences[intPreferencesKey("best_score_$levelIndex")] = score
            }
        }
    }

    suspend fun getAllBestScores(): List<Int> {
        return (0..4).map { getBestScore(it) }
    }

    suspend fun isLevelCompleted(levelIndex: Int): Boolean {
        return getBestScore(levelIndex) > 0
    }
}
