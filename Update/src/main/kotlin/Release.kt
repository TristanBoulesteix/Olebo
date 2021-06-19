import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class Release(@SerialName("tag_name") val tag: String, val body: String, val assets: List<Asset>)