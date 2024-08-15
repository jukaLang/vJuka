fun parseInput(input: String): Map<String, MyObject> {
    val propertyPattern = Regex("(\\w+): (.+)")

    val variables = mutableMapOf<String, Any>()
    val objects = mutableMapOf<String, MyObject>()

    fun parseObject(name: String, lines: List<String>, varMap: Map<String, Any>): MyObject {
        val properties = mutableMapOf<String, Any>()
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            val matcher = propertyPattern.find(line)
            if (matcher != null) {
                val key = matcher.groupValues[1]
                val value = matcher.groupValues[2]
                properties[key] = if (value.startsWith("#")) {
                    varMap[value.substring(1)] ?: error("Undefined reference: $value")
                } else {
                    value
                }
            } else if (line.endsWith(":")) {
                val nestedObjectName = line.substringBefore(":").trim()
                val nestedObjectLines = mutableListOf<String>()
                i++
                while (i < lines.size && lines[i].startsWith("   ")) { // 3 spaces for indentation
                    nestedObjectLines.add(lines[i].substring(3)) // Remove leading spaces
                    i++
                }
                val nestedObject = parseObject(nestedObjectName, nestedObjectLines, varMap)
                properties[nestedObjectName] = nestedObject
                continue
            }
            i++
        }
        return MyObject(name, properties)
    }

    val lines = input.trim().lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]
        if (line.startsWith("Initialize:")) {
            i++
            while (i < lines.size && lines[i].startsWith("   ")) { // 3 spaces for indentation
                val parts = lines[i].trim().split(":")
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim()
                    variables[key] = value
                    println("Initialized variable: $key = $value")
                }
                i++
            }
        } else if (line.endsWith(":")) {
            val objectName = line.substringBefore(":").trim()
            val objectLines = mutableListOf<String>()
            i++
            while (i < lines.size && lines[i].startsWith("   ")) { // 3 spaces for indentation
                objectLines.add(lines[i].substring(3)) // Remove leading spaces
                i++
            }
            val obj = parseObject(objectName, objectLines, variables)
            objects[obj.name] = obj
        } else {
            i++
        }
    }

    // Print the variables map to ensure it's populated correctly
    println("Variables map: $variables")

    // Resolve references recursively
    fun resolveReferences(obj: MyObject) {
        obj.properties.forEach { (key, value) ->
            if (value is String && value.startsWith("#")) {
                println("Resolving reference for key: $key, value: $value")
                obj.properties[key] = variables[value.substring(1)] ?: error("Undefined reference: $value")
            } else if (value is MyObject) {
                resolveReferences(value)
            }
        }
    }

    objects.values.forEach { resolveReferences(it) }

    return objects
}
