package com.zeafen.petwalker.data.helpers

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun isPermissionGranted(
    context : Context,
    permission : String
) : Boolean{
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}


fun arePermissionsGranted(
    context : Context,
    permissions  : List<String>
) : Boolean{
    return permissions.all {
        ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}