package opensavvy.gitlab.ci.utils

import kotlin.reflect.KProperty

class DelegateProvider<Parent, Return>(
	private val parent: Parent,
	private val onCreation: (parent: Parent, property: KProperty<*>) -> Return,
) {

	operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Return =
		onCreation(parent, property)

}

internal fun <Parent, Return> Parent.generateDelegateProvider(onCreation: (parent: Parent, property: KProperty<*>) -> Return) =
	DelegateProvider(this, onCreation)

internal fun <Parent, Type> Parent.generateReadOnlyDelegateProvider(init: (parent: Parent, property: KProperty<*>) -> Type) =
	generateDelegateProvider { parent, property ->
		ReadOnlyDelegate(init(parent, property))
	}
