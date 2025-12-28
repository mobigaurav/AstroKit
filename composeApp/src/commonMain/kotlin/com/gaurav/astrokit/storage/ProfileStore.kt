package com.gaurav.astrokit.storage

import com.gaurav.astrokit.core.AppDI
import com.gaurav.astrokit.platform.nowEpochMillis
import com.gaurav.astrokit.ui.Dob
import com.gaurav.astrokit.ui.UserProfile
import com.gaurav.astrokit.ui.defaultProfiles

private const val KEY_SELECTED = "profiles_selected"
private const val KEY_IDS = "profiles_ids" // pipe-separated
private const val KEY_PREFIX = "profiles_" // profiles_<id> -> name|y|m|d

object ProfilesStore {

    fun ensureSeeded() {
        val idsRaw = AppDI.settings.getStringOrNull(KEY_IDS)
        if (!idsRaw.isNullOrBlank()) return

        val seeded = defaultProfiles()
        AppDI.settings.putString(KEY_IDS, seeded.joinToString("|") { it.id })
        seeded.forEach { saveProfile(it) }
        AppDI.settings.putString(KEY_SELECTED, seeded.first().id)
    }

    fun getSelectedId(): String? = AppDI.settings.getStringOrNull(KEY_SELECTED)

    fun setSelectedId(id: String) {
        AppDI.settings.putString(KEY_SELECTED, id)
    }

    fun loadProfiles(): List<UserProfile> {
        ensureSeeded()
        val ids = AppDI.settings.getStringOrNull(KEY_IDS)
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        return ids.mapNotNull { loadProfile(it) }
    }

    fun loadSelectedOrFirst(): UserProfile {
        val profiles = loadProfiles()
        val selected = getSelectedId()
        return profiles.firstOrNull { it.id == selected } ?: profiles.first()
    }

    fun updateDob(id: String, dob: Dob) {
        val p = loadProfile(id) ?: return
        saveProfile(p.copy(dob = dob))
    }

    fun rename(id: String, newName: String) {
        val p = loadProfile(id) ?: return
        val trimmed = newName.trim()
        if (trimmed.isBlank()) return
        saveProfile(p.copy(name = trimmed))
    }

    fun addProfile(name: String, dob: Dob): UserProfile {
        ensureSeeded()

        // ✅ KMP-safe id generation (no java.lang.System in commonMain)
        val id = "p_${nowEpochMillis()}"

        val newP = UserProfile(
            id = id,
            name = name.trim().ifBlank { "Profile" },
            dob = dob
        )

        val ids = (AppDI.settings.getStringOrNull(KEY_IDS) ?: "")
            .split("|")
            .filter { it.isNotBlank() }
            .toMutableList()

        ids.add(0, id)
        AppDI.settings.putString(KEY_IDS, ids.joinToString("|"))
        saveProfile(newP)
        setSelectedId(id)

        return newP
    }

    fun deleteProfile(id: String) {
        ensureSeeded()
        val ids = (AppDI.settings.getStringOrNull(KEY_IDS) ?: "")
            .split("|")
            .filter { it.isNotBlank() }
            .toMutableList()

        if (!ids.remove(id)) return

        AppDI.settings.putString(KEY_IDS, ids.joinToString("|"))
        AppDI.settings.remove(KEY_PREFIX + id)

        // if deleting selected, pick first remaining
        if (getSelectedId() == id) {
            ids.firstOrNull()?.let { setSelectedId(it) }
        }
    }

    private fun saveProfile(p: UserProfile) {
        val raw = listOf(
            p.name.replace("|", " "),
            p.dob.year.toString(),
            p.dob.month.toString(),
            p.dob.day.toString()
        ).joinToString("|")

        AppDI.settings.putString(KEY_PREFIX + p.id, raw)
    }

    private fun loadProfile(id: String): UserProfile? {
        val raw = AppDI.settings.getStringOrNull(KEY_PREFIX + id) ?: return null
        val parts = raw.split("|")
        if (parts.size < 4) return null

        val name = parts[0]
        val y = parts[1].toIntOrNull() ?: return null
        val m = parts[2].toIntOrNull() ?: return null
        val d = parts[3].toIntOrNull() ?: return null

        return UserProfile(id = id, name = name, dob = Dob(y, m, d))
    }
}
