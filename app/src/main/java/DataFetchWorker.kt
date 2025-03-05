import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app.AppWidget
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.FormBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataFetchWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val client = OkHttpClient()
        return try {
            // 获取当前日期
            val timestamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // 创建HTTP请求
            val requestBody = FormBody.Builder()
                .add("timestamp", timestamp)
                .build()
            val request = Request.Builder()
                .url("https://dxjesever.icu/android_app/get_data")
                .post(requestBody)
                .build()
            val response = client.newCall(request).execute()

            return if (response.isSuccessful) {
                val responseData = response.body?.string()
                response.close() // 确保响应关闭
                if (responseData != null) {
                    val jsonResponse = JSONObject(responseData)
                    val status = jsonResponse.getString("status")
                    if (status == "found") {
                        // 处理返回的数据
                        val resultArray = jsonResponse.getJSONArray("data")
                        val hours = jsonResponse.getInt("hours")
                        val minutes = jsonResponse.getInt("minutes")
                        val result = IntArray(resultArray.length())
                        for (i in 0 until resultArray.length()) {
                            result[i] = resultArray.getInt(i)
                        }
                        Log.i("result", "doWork: " + result[0])

                        // 更新小组件
                        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                        val thisAppWidget = ComponentName(applicationContext.packageName, AppWidget::class.java.name)
                        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
                        if (appWidgetIds.isNotEmpty()) {
                            AppWidget.updateAppWidgets(applicationContext, appWidgetManager, appWidgetIds, hours,minutes,result.toList())
                        }

                        Result.success()
                    } else {
                        Result.failure()
                    }
                } else {
                    Result.failure()
                }
            } else {
                response.close() // 确保响应关闭
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        } finally {
            client.dispatcher.executorService.shutdown() // 关闭OkHttpClient
            client.connectionPool.evictAll() // 清除连接池中的所有连接
//            client.cache?.close() // 关闭缓存
        }
    }
}
