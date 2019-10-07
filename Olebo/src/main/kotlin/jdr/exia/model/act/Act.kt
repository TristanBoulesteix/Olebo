package jdr.exia.model.act

import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.getContent
import jdr.exia.model.dao.tables.ActTable
import jdr.exia.model.dao.tables.SceneTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class Act(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Act>(ActTable)

    private val scenesIterable by Scene referrersOn SceneTable.idAct

    val name by ActTable.name
    val scenes by lazy {
        transaction(DAO.database) {
            scenesIterable.getContent()
        }
    }

}
