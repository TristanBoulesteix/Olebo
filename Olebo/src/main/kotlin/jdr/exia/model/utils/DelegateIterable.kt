package jdr.exia.model.utils

import jdr.exia.model.dao.DAO
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * This class allow us to get a lazy Mutablelist from a SizedIterable by creating automatically a transaction
 */
class DelegateIterable<T>(initializer: () -> SizedIterable<T>) : Lazy<MutableList<T>> {
    companion object UnitializedValue

    private var initializer: (() -> SizedIterable<T>)? = initializer
    @Volatile
    private var _value: Any? = UnitializedValue

    override fun isInitialized(): Boolean = _value !== UnitializedValue

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."

    override val value: MutableList<T>
        get() {
            //val v1 = _value
/*            if (v1 !== UnitializedValue) {
                @Suppress("UNCHECKED_CAST")
                return v1 as MutableList<T>
            }*/

            return transaction(DAO.database) {
                synchronized(this) {
/*                    val v2 = _value
                    if (v2 !== UnitializedValue) {
                        @Suppress("UNCHECKED_CAST") (v2 as MutableList<T>)
                    } else {
                        val typedValue = initializer!!()
                        _value = typedValue
                        initializer = null

                        typedValue.getContent()
                    }*/
                    val typedValue = initializer!!()
                    _value = typedValue
                    //initializer = null

                    typedValue.getContent()

                }
            }
        }

    /**
     * Extension dunction which allow us to get a MutableList from a SizedIterable
     */
    private fun <T> SizedIterable<T>.getContent(): MutableList<T> {
        val content = mutableListOf<T>()

        this.forEach {
            content += it
        }

        return content
    }
}