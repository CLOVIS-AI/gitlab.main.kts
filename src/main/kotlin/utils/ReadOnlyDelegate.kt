package opensavvy.gitlab.ci.utils

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ReadOnlyDelegate<Type>(private val value: Type) : ReadOnlyProperty<Any?, Type> {
	override fun getValue(thisRef: Any?, property: KProperty<*>) = value
}
