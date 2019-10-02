package jdr.exia.model.dao

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import java.sql.DriverManager
import java.sql.ResultSet
import javax.swing.ImageIcon

object DAO {
    private const val url = "jdbc:sqlite:C://sqlite/db/test.db" // TODO : Mettre la bonne url de la base

    private val connection = DriverManager.getConnection(url)

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