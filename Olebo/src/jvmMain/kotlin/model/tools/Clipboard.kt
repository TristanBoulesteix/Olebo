package jdr.exia.model.tools

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

fun saveToClipboard(content: String) =
    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(content), null)