package jdr.exia.controller

import jdr.exia.pattern.Observable
import jdr.exia.pattern.Observer

object ActCreatorFrameManager : Observable {
    override var observer: Observer? = null
}