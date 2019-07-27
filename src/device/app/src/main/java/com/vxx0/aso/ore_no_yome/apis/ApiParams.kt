package com.vxx0.aso.ore_no_yome.apis

import android.graphics.Bitmap

class ApiParams(
    apiName: String,
    val params: HashMap<String, String> = hashMapOf(),
    val image01: Bitmap? = null,
    val image02: Bitmap? = null
) {
    val api = "https://aso.vxx0.com/ore-no-yome/api/$apiName"
}