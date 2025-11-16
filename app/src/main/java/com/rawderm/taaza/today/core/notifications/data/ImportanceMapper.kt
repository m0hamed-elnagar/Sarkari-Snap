package com.rawderm.taaza.today.core.notifications.data

import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.FrequencyMode

object ImportanceMapper {

    /* UI → Datastore */
    fun toImportance(mode: FrequencyMode): Importance = when (mode) {
        FrequencyMode.BREAKING -> Importance.NORMAL
        FrequencyMode.STANDARD -> Importance.HIGH
        FrequencyMode.Custom     -> Importance.HIGH
    }

    /* Datastore → UI */
    fun toFrequencyMode(imp: Importance): FrequencyMode = when (imp) {
        Importance.NORMAL   -> FrequencyMode.BREAKING
        Importance.HIGH -> FrequencyMode.STANDARD
        Importance.NONE   -> FrequencyMode.Custom
    }
}