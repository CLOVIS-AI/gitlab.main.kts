/*
 * Copyright (c) 2022-2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
