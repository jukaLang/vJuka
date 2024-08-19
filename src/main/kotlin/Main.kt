
import java.io.File

data class MyObject(val name: String, val properties: MutableMap<String, Any>)

val debug = true


fun main(args: Array<String>) {

    // Scan the JukaApp folder
    /*val configFiles = scanJukaAppFolder("./JukaApps")

    // Parse each configuration file
    val objects = mutableMapOf<String, MyObject>()
    configFiles.forEach { configFileContent ->
        val parsedObjects = parseInput(configFileContent)
        objects.putAll(parsedObjects)
    }

    // Print the parsed objects
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
    }*/

    val appInfos = scanApps("./Apps","tsp")
    val configFiles = scanApps("./JukaApps","vjuka")
    appInfos.forEach { appInfo ->
        println("Label: ${appInfo.label}")
        println("Icon: ${appInfo.icon}")
        println("Launch: ${appInfo.launch}")
        println("Description: ${appInfo.description}")
        println("-----")
    }

    runme(appInfos)
}
