package jdr.exia.model.dao

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.utils.MessageException
import jdr.exia.utils.appDatas
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

    /**
     * A select request
     */
    private fun select(rSQL: String): ResultSet {
        val stmt = connection.createStatement()
        return stmt.executeQuery(rSQL)
    }

    /**
     * Get all acts stored into the database
     */
    fun getActsList(): Array<String> {
        val actsName = mutableListOf<String>()
        val req = select("SELECT Name FROM Act")

        while (req.next()) {
            actsName += req.getString(1)
        }

        req.close()

        return actsName.toTypedArray()
    }

    /**
     * Get an instance of a selected act with its ID
     */
    fun getActWithId(idAct: Int): Act {
        val scenes = mutableListOf<Scene>()
        val reqScenes = select("SELECT * FROM Scene WHERE ID_Act = $idAct")

        while (reqScenes.next()) {
            scenes += Scene(
                reqScenes.getString("name"),
                ImageIcon(reqScenes.getString("Background")),
                mutableListOf()
            )
        }

        reqScenes.close()

        val reqAct = select("SELECT * FROM Act Where id = $idAct")

        var act: Act? = null

        while (reqAct.next()) {
            act = Act(reqAct.getString("Name"), scenes)
        }

        return act ?: throw MessageException("Error ! This act doesn't exist.")
    }
}