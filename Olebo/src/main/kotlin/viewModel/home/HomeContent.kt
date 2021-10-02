package jdr.exia.viewModel.home

import jdr.exia.model.act.Act

sealed class HomeContent

object ActsView : HomeContent()

object ElementsView : HomeContent()

class ActEditor(val act: Act) : HomeContent()

object ActCreator : HomeContent()