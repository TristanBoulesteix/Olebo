package jdr.exia.model.dao

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.utils.appDatas
import java.io.File
import java.io.File.separator
import java.sql.DriverManager
import java.sql.ResultSet
import javax.swing.ImageIcon

object DAO {
    private val db_name = "Olebo${separator}db${separator}template.db"
    private val file_path = "$appDatas$db_name"
    private val url = "jdbc:sqlite:$file_path"
    private val connection by lazy {
        if (!File(file_path).exists()) {
            File(this.javaClass.classLoader.getResource("db/template.db")!!.toURI()).copyTo(
                File(file_path), true
            )
        }

        DriverManager.getConnection(url)
    }

    private fun select(rSQL: String): ResultSet {
        val stmt = connection.createStatement()
        return stmt.executeQuery(rSQL)
    }

    fun getActsList(): Array<String> {
        val actsName = mutableListOf<String>()
        val req = select("SELECT Name FROM Act")

        while (req.next()) {
            actsName += req.getString(1)
        }

        req.close()

        return actsName.toTypedArray()
    }

    // Get all acts in a List
    fun getAllActs(): List<Act> {
        val acts = mutableListOf<Act>()
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
            acts += Act(req.getString("Name"), sceneFromAct(req.getInt("Id")))
        }

        req.close()

        return acts
    }
}