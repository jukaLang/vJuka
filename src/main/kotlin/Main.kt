
import java.io.File

data class MyObject(val name: String, val properties: MutableMap<String, Any>)

val debug = true

fun main(args: Array<String>) {

    println("Starting...")
    var filePath = if (args.isNotEmpty()) args[0] else "config.vjuka"
    val file = File(filePath)

    if (!file.exists() || !filePath.endsWith(".vjuka")) {
        println("File not found or invalid extension. Using default file: config.vjuka")
        filePath = "config.vjuka"
    }

    val input = File(filePath).readText()

    val objects = parseInput(input)

    for ((sceneName, sceneObj) in objects) {
        println("Scene: $sceneName")
        for ((objName, obj) in sceneObj.properties) {
            if (obj is MyObject) {
                println("  Object: $objName")
                for ((propName, propValue) in obj.properties) {
                    println("    $propName: $propValue")
                }
            } else {
                println("S  $objName: $obj")
            }
        }
    }

    val appInfos = scanApps("./Apps")
    appInfos.forEach { appInfo ->
        println("Label: ${appInfo.label}")
        println("Icon: ${appInfo.icon}")
        println("Launch: ${appInfo.launch}")
        println("Description: ${appInfo.description}")
        println("-----")
    }

    runme(appInfos)
}
