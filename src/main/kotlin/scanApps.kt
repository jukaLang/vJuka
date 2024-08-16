import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class AppInfo(val label: String, val icon: String, val launch: String, val description: String)

fun scanApps(directoryPath: String): List<AppInfo> {
    val appInfoList = mutableListOf<AppInfo>()
    val appsDir = File(directoryPath)

    if (appsDir.exists() && appsDir.isDirectory) {
        appsDir.listFiles()?.forEach { subDir ->
            if (subDir.isDirectory) {
                val configFile = File(subDir, "config.json")
                if (configFile.exists() && configFile.isFile) {
                    try {
                        val jsonString = configFile.readText()
                        val jsonObject = Json.parseToJsonElement(jsonString).jsonObject
                        val label = jsonObject["label"]?.jsonPrimitive?.content ?: ""
                        val icon = jsonObject["icon"]?.jsonPrimitive?.content ?: ""
                        val launch = jsonObject["launch"]?.jsonPrimitive?.content ?: ""
                        val description = jsonObject["description"]?.jsonPrimitive?.content ?: ""
                        val relativeIconPath = "Apps/${subDir.name}/$icon"
                        val relativeLaunchPath = "Apps/${subDir.name}/$launch"
                        appInfoList.add(AppInfo(label, relativeIconPath, relativeLaunchPath, description))

                        //FOR DEBUGGING
                        if(debug) {
                            appInfoList.add(AppInfo(label, relativeIconPath, relativeLaunchPath, description))
                            appInfoList.add(AppInfo(label, relativeIconPath, relativeLaunchPath, description))
                            appInfoList.add(AppInfo(label, relativeIconPath, relativeLaunchPath, description))
                            appInfoList.add(AppInfo(label, relativeIconPath, relativeLaunchPath, description))
                            appInfoList.add(AppInfo(label, relativeIconPath, relativeLaunchPath, description))
                        }
                    } catch (e: Exception) {
                        // Handle exceptions while reading or parsing JSON
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    return appInfoList.toList()
}