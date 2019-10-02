package jdr.exia.model.dao

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.appDatas
import java.io.File
import java.io.File.separator
import java.sql.DriverManager
import java.sql.ResultSet
import javax.swing.ImageIcon

object DAO {
    private val DB_NAME = "db${separator}template.db"
    private val url = "jdbc:sqlite:$appDatas$DB_NAME"
    private val connection by lazy {
        if(!File(url).exists()) {
            println(File(this.javaClass.classLoader.getResource("db/template.db")!!.toURI()).absolutePath)
        }

        DriverManager.getConnection(url)
    }

    private fun select(rSQL: String): ResultSet {
        val stmt = connection.createStatement()
        return stmt.executeQuery(rSQL)
    }

    // Get all acts in a mutableList
    fun getAllActs(): MutableList<Act> {
        val actName = mutableListOf<Act>()
        val req = select("SELECT * FROM Act")

        // Select all scene with act id
        fun sceneFromAct(idAct: Int): MutableList<Scene> {
            val reqAct = select("SELECT * FROM Scene WHERE ID_Act = $idAct")

            val scene = mutableListOf<Scene>()

            while (reqAct.next()) {
                scene += Scene(
                    reqAct.getString("Nom"),
                    ImageIcon(reqAct.getString("Background")),
                    mutableListOf()
                )
            }

            reqAct.close()

            return scene
        }

        while (req.next()) {
            actName += Act(req.getString("Name"), sceneFromAct(req.getInt("Id")))
        }

        req.close()

        return actName
    }
}