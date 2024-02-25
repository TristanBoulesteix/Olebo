package adaptors

import models.OS

interface OsAdaptor {
    val current: OS
}