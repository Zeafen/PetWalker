package com.zeafen.petwalker.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createDataStore(): DataStore<Preferences> = createDataStore {
    val file = File(System.getProperty("java.io.tmpdir"), DATASTORE_FILE_NAME)
    file.absolutePath
}