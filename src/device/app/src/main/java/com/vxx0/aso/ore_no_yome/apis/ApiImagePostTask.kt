package com.vxx0.aso.ore_no_yome.apis

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.AsyncTask
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class ApiImagePostTask(var callback: (JSONObject?) -> Unit) : AsyncTask<ApiParams, Unit, JSONObject>() {

    override fun doInBackground(vararg apiParams: ApiParams): JSONObject? {
        try {
            val params = apiParams[0]
            val api = params.api

            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
            params.params.forEach { (name, value) -> multipartBody.addFormDataPart(name, value) }

            if (params.image01 != null) {
                val byteArray = bitmapToByteArray(params.image01)

                multipartBody.addFormDataPart(
                    "image01",
                    "image01.jpg",
                    RequestBody.create(MediaType.parse("image/*jpg"), byteArray)
                )
            }

            if (params.image02 != null) {
                val byteArray = bitmapToByteArray(params.image02)

                multipartBody.addFormDataPart(
                    "image02",
                    "image02.jpg",
                    RequestBody.create(MediaType.parse("image/*jpg"), byteArray)
                )
            }

            val requestBody = multipartBody.build()

            val request = Request.Builder().url(api).post(requestBody).build()
            val okHttpClient = OkHttpClient()
            val call = okHttpClient.newCall(request)
            val response = call.execute()
            val body = response.body()
            val responseBody = body!!.string()

            return JSONObject(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun onPostExecute(result: JSONObject?) {
        super.onPostExecute(result)

        callback(result)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}