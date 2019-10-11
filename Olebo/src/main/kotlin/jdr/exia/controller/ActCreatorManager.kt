package jdr.exia.controller

import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer

object ActCreatorManager : Observable {
    override var observer: Observer? = null
}