package jdr.exia.viewModel.home

import jdr.exia.model.act.Act

sealed class HomeContent

data object ActsView : HomeContent()

data object ElementsView : HomeContent()

class ActEditor(val act: Act) : HomeContent()

data object ActCreator : HomeContent()