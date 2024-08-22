package fr.olebo.memory.tests.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStream
import java.util.prefs.NodeChangeListener
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

class PreferencesMock : Preferences() {
    private val items = buildMap<Any, Any?> {
        put("stringKey", Json.encodeToString("expected"))
        put("intKey", Json.encodeToString(42))
    }.toMutableMap()
    
    override fun put(key: String, value: String?) {
        items[key] = value
    }

    override fun get(key: String, def: String?): String? {
        return items[key] as String?
    }

    override fun remove(key: String) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun putInt(key: String, value: Int) {
        TODO("Not yet implemented")
    }

    override fun getInt(key: String, def: Int): Int {
        TODO("Not yet implemented")
    }

    override fun putLong(key: String, value: Long) {
        TODO("Not yet implemented")
    }

    override fun getLong(key: String, def: Long): Long {
        TODO("Not yet implemented")
    }

    override fun putBoolean(key: String, value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getBoolean(key: String, def: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun putFloat(key: String, value: Float) {
        TODO("Not yet implemented")
    }

    override fun getFloat(key: String, def: Float): Float {
        TODO("Not yet implemented")
    }

    override fun putDouble(key: String, value: Double) {
        TODO("Not yet implemented")
    }

    override fun getDouble(key: String, def: Double): Double {
        TODO("Not yet implemented")
    }

    override fun putByteArray(key: String, value: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun getByteArray(key: String, def: ByteArray?): ByteArray? {
        TODO("Not yet implemented")
    }

    override fun keys(): Array<out String?>? {
        TODO("Not yet implemented")
    }

    override fun childrenNames(): Array<out String?>? {
        TODO("Not yet implemented")
    }

    override fun parent(): Preferences? {
        TODO("Not yet implemented")
    }

    override fun node(pathName: String?): Preferences? {
        TODO("Not yet implemented")
    }

    override fun nodeExists(pathName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeNode() {
        TODO("Not yet implemented")
    }

    override fun name(): String? {
        TODO("Not yet implemented")
    }

    override fun absolutePath(): String? {
        TODO("Not yet implemented")
    }

    override fun isUserNode(): Boolean {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        TODO("Not yet implemented")
    }

    override fun flush() {
        TODO("Not yet implemented")
    }

    override fun sync() {
        TODO("Not yet implemented")
    }

    override fun addPreferenceChangeListener(pcl: PreferenceChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun removePreferenceChangeListener(pcl: PreferenceChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun addNodeChangeListener(ncl: NodeChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun removeNodeChangeListener(ncl: NodeChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun exportNode(os: OutputStream?) {
        TODO("Not yet implemented")
    }

    override fun exportSubtree(os: OutputStream?) {
        TODO("Not yet implemented")
    }
}