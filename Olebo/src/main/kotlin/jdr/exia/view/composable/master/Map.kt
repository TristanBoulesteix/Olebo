@file:Suppress("FunctionName")

package jdr.exia.view.composable.master

import androidx.compose.desktop.SwingPanel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jdr.exia.model.act.Act
import jdr.exia.model.element.Elements
import jdr.exia.view.legacy.frames.rpg.GameFrame
import jdr.exia.view.legacy.frames.rpg.MapPanel
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO


@Composable
fun Map(act: Act) = SwingPanel(
    modifier = Modifier.fillMaxSize(),
    factory = {
        MapPanel(object : GameFrame {
            override fun updateMap(tokens: Elements) {

            }

            override var mapBackground: Image? = null

            override fun reload() {

            }
        }).apply {
            transaction {
                backGroundImage = with(act) {
                    scenes.findWithId(sceneId)?.background?.let {
                        ImageIO.read(File(it))
                    }
                }
            }
        }
    }
)