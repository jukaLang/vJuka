import com.jme3.app.SimpleApplication
import com.jme3.font.BitmapText
import com.jme3.input.KeyInput
import com.jme3.input.controls.ActionListener
import com.jme3.input.controls.KeyTrigger
import com.jme3.light.AmbientLight
import com.jme3.light.DirectionalLight
import com.jme3.material.Material
import com.jme3.material.RenderState
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector3f
import com.jme3.scene.Geometry
import com.jme3.scene.Mesh
import com.jme3.scene.Node
import com.jme3.scene.VertexBuffer
import com.jme3.system.AppSettings
import com.jme3.texture.Texture2D
import com.jme3.texture.plugins.AWTLoader
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.lang.System.err
import javax.imageio.ImageIO

class Main(private val appInfos: List<AppInfo>) : SimpleApplication(), ActionListener {
    private lateinit var hexagons: Array<Array<Node>>
    private var selectedRow = 0
    private var selectedCol = 0

    override fun simpleInitApp() {
        // Set background color
        viewPort.backgroundColor = ColorRGBA(51f / 255f, 52f / 255f, 71f / 255f, 1f)

        // Enable flyCam for mouse movement
        flyCam.isEnabled = true

        // Add a directional light to the scene
        val sun = DirectionalLight()
        sun.color = ColorRGBA.White
        sun.direction = Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal()
        rootNode.addLight(sun)

            /*val ambientLight = AmbientLight() //MIGHT NOT NEED IT? FOR NOW I JUST ADDED IT
        ambientLight.color = ColorRGBA.White.mult(0.3f) // Adjust color and intensity
        rootNode.addLight(ambientLight)*/

        // Create hexagons
        hexagons = createHexagonsGrid(appInfos)

        // Attach hexagons to the root node, filtering out null values
        hexagons.flatten().filterNotNull().forEach { rootNode.attachChild(it) }

        // Mapping for TSP will change...but follow same architecture
        inputManager.addMapping("Left", KeyTrigger(KeyInput.KEY_LEFT))
        inputManager.addMapping("Right", KeyTrigger(KeyInput.KEY_RIGHT))
        inputManager.addMapping("Up", KeyTrigger(KeyInput.KEY_UP))
        inputManager.addMapping("Down", KeyTrigger(KeyInput.KEY_DOWN))
        inputManager.addMapping("Enter", KeyTrigger(KeyInput.KEY_RETURN))
        inputManager.addListener(this, "Left", "Right", "Up", "Down","Enter")

        // Debugging: Print hexagons array size and contents
        println("Hexagons array size: ${hexagons.size} x ${hexagons[0].size}")
        hexagons.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, node ->
                println("Hexagon at [$rowIndex][$colIndex]: ${node?.name}")
            }
        }

        // Highlight the first hexagon if the array is not empty
        if (hexagons.isNotEmpty() && hexagons[0].isNotEmpty()) {
            highlightHexagon(selectedRow, selectedCol)
        }

        // Adjust the camera position to view the hexagons centered with the first hexagon in the top left
        cam.location = Vector3f(200f, 100f, 300f)
        cam.lookAt(Vector3f(200f, 100f, 0f), Vector3f.UNIT_Y)
    }

    private fun createHexagonMesh(sideLength: Float): Mesh {
        val height = sideLength * Math.sqrt(3.0).toFloat() / 2
        val vertices = floatArrayOf(
            0f, height, 0f,
            -sideLength, height / 2, 0f,
            -sideLength, -height / 2, 0f,
            0f, -height, 0f,
            sideLength, -height / 2, 0f,
            sideLength, height / 2, 0f
        )
        val texCoords = floatArrayOf(
            0.5f, 1f,
            0f, 0.75f,
            0f, 0.25f,
            0.5f, 0f,
            1f, 0.25f,
            1f, 0.75f
        )
        val indices = intArrayOf(
            0, 1, 5,
            1, 2, 4,
            2, 3, 4,
            4, 5, 1
        )

        val mesh = Mesh()
        mesh.setBuffer(VertexBuffer.Type.Position, 3, vertices)
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, texCoords)
        mesh.setBuffer(VertexBuffer.Type.Index, 3, indices)
        mesh.updateBound()
        return mesh
    }

    private fun createHexagonsGrid(appInfos: List<AppInfo>): Array<Array<Node>> {
        val totalHexagons = appInfos.size
        val cols = 10
        val rows = (totalHexagons + cols - 1) / cols
        val hexagons = Array(rows) { Array<Node?>(cols) { null } }
        val hexagonSideLength = 80f
        val hexagonHeight = hexagonSideLength * Math.sqrt(3.0).toFloat() / 2
        val horizontalSpacing = hexagonSideLength * 2.1f // Adjusted for spacing
        val verticalSpacing = hexagonHeight * 2.1f // Adjusted for spacing
        val startX = 10 + hexagonSideLength
        val startY = 100

        for ((index, appInfo) in appInfos.withIndex()) {
            val row = index / cols
            val col = index % cols

            val hexagon = createHexagonMesh(hexagonSideLength)
            val geom = Geometry("Hexagon$row$col", hexagon)

            val file = File(appInfo.icon)
            val image = ImageIO.read(file)


            val targetWidth = (hexagonSideLength * 2).toInt()
            val targetHeight = (hexagonHeight * 2).toInt()

            val processedImage =  processImage(appInfo.icon, targetWidth, targetHeight)

            val awtLoader = AWTLoader()
            val texture = Texture2D(awtLoader.load(processedImage, true))

            val material = Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
            material.setTexture("ColorMap", texture)
            material.setColor("Color", ColorRGBA(0.5f, 0.5f, 0.5f, 1f)) // Set base color to grey

            geom.material = material



            val node = Node("HexagonNode$row$col")
            node.attachChild(geom)

            val centerX = startX + col * horizontalSpacing + (row % 2) * (horizontalSpacing / 2)
            val centerY = startY + row * verticalSpacing
            node.localTranslation = Vector3f(centerX, centerY, 0f)

            // Add label and description inside the hexagon
            val label = BitmapText(guiFont, false)
            label.text = appInfo.label
            label.setSize(guiFont.charSet.renderedSize * 0.5f) // Adjusted size
            label.color = ColorRGBA.White // Set text color to white
            label.setLocalTranslation(-label.lineWidth / 2, label.lineHeight / 2, 0.1f) // Slightly forward on Z-axis
            node.attachChild(label)

            val description = BitmapText(guiFont, false)
            description.text = appInfo.description
            description.setSize(guiFont.charSet.renderedSize * 0.4f) // Adjusted size
            description.color = ColorRGBA.White // Set text color to white
            description.setLocalTranslation(-description.lineWidth / 2, -description.lineHeight / 2, 0.1f) // Slightly forward on Z-axis
            node.attachChild(description)

            hexagons[row][col] = node
        }

        return hexagons.map { row -> row.filterNotNull().toTypedArray() }.toTypedArray()
    }

    fun processImage(imagePath: String, targetWidth: Int, targetHeight: Int): BufferedImage {
        val image = ImageIO.read(File(imagePath))

        // Find the bounding box of the non-transparent pixels
        var minX = image.width
        var minY = image.height
        var maxX = 0
        var maxY = 0

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val alpha = image.getRGB(x, y) shr 24 and 0xff
                if (alpha != 0) {
                    if (x < minX) minX = x
                    if (y < minY) minY = y
                    if (x > maxX) maxX = x
                    if (y > maxY) maxY = y
                }
            }
        }

        // Crop the image to the bounding box
        val croppedImage = image.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1)

        // Resize the cropped image to fit within the hexagon
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)
        val g2: Graphics2D = resizedImage.createGraphics()
        g2.drawImage(croppedImage, 0, 0, targetWidth, targetHeight, null)
        g2.dispose()

        return resizedImage
    }

    private fun highlightHexagon(row: Int, col: Int) {
        hexagons.flatten().forEach {
            val geometry = it?.getChild(0) as? Geometry
            geometry?.material?.setColor("Color", ColorRGBA.White) // Set default color to white

            // Remove any existing outline
            val outline = it?.getChild("Outline")
            if (outline != null) {
                it.detachChild(outline)
            }
        }

        val selectedGeometry = hexagons[row][col]?.getChild(0) as? Geometry
        selectedGeometry?.material?.setColor("Color", ColorRGBA.Blue) // Set selected hexagon color to blue

    }

    override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
        if (!isPressed) return

        when (name) {
            "Left" -> selectedCol = (selectedCol - 1 + hexagons[0].size) % hexagons[0].size
            "Right" -> selectedCol = (selectedCol + 1) % hexagons[0].size
            "Up" -> selectedRow = (selectedRow - 1 + hexagons.size) % hexagons.size
            "Down" -> selectedRow = (selectedRow + 1) % hexagons.size
            "Enter" ->  launchSelectedApp()
        }

        highlightHexagon(selectedRow, selectedCol)
    }

    private fun launchSelectedApp() {
        val selectedAppInfo = appInfos[selectedRow * hexagons[0].size + selectedCol]
        try {
            println(selectedAppInfo.launch)
            val isWindows = System.getProperty("os.name").toLowerCase().startsWith("win")

            val processBuilder = if (isWindows) {
                ProcessBuilder("cmd.exe", "/c", "start \"\" \"${selectedAppInfo.launch}\"") // Added quotes for path with spaces
            } else {
                ProcessBuilder("/bin/sh", "-c", "\"$selectedAppInfo.launch\"")
            }

            processBuilder.start()
        } catch (e: IOException) {
            err.println(selectedAppInfo.launch)
            e.printStackTrace()
            // Handle error, e.g., show a message to the user
        }
    }
}

fun runme(appInfos: List<AppInfo>) {
    val app = Main(appInfos)
    val settings = AppSettings(true)
    settings.title = "vJuka"
    settings.setResolution(1280, 720)
    settings.isFullscreen = false
    settings.setRenderer(AppSettings.LWJGL_OPENGL2)
    app.setSettings(settings)
    app.isShowSettings = false
    app.start()
}
