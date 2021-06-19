import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class Asset(@SerialName("browser_download_url") val url : String, val name: String) {

}