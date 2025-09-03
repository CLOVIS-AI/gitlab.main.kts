/*
 * Copyright (c) 2025, OpenSavvy and contributors.
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

package opensavvy.gitlab.ci.yaml

import opensavvy.gitlab.ci.YamlExport

@DslMarker
annotation class YamlDsl

@YamlDsl
@Suppress("INAPPLICABLE_JVM_NAME")
interface YamlMapScope {

	@YamlDsl
	fun add(name: String, value: Yaml)

	@YamlDsl fun add(name: String, value: String) = add(name, yaml(value))
	@YamlDsl fun add(name: String, value: Long) = add(name, yaml(value))
	@YamlDsl fun add(name: String, value: Double) = add(name, yaml(value))
	@YamlDsl fun add(name: String, value: Boolean) = add(name, yaml(value))
	@YamlDsl fun add(name: String, value: YamlExport) = add(name, value.toYaml())

	@JvmName("addList")
	@YamlDsl fun add(name: String, value: List<YamlExport>) = add(name, yamlList(value))
	@YamlDsl fun add(name: String, value: Iterable<YamlExport>) = add(name, value.toList())
	@JvmName("addStrings")
	@YamlDsl fun add(name: String, value: Iterable<String>) = add(name, value.map(::yaml))
	@JvmName("addLongs")
	@YamlDsl fun add(name: String, value: Iterable<Long>) = add(name, value.map(::yaml))
	@JvmName("addDoubles")
	@YamlDsl fun add(name: String, value: Iterable<Double>) = add(name, value.map(::yaml))
	@JvmName("addBooleans")
	@YamlDsl fun add(name: String, value: Iterable<Boolean>) = add(name, value.map(::yaml))
	@YamlDsl fun add(name: String, value: Map<Yaml, Yaml>) = add(name, yamlMap(value))
	@JvmName("addStringsMap")
	@YamlDsl fun add(name: String, value: Map<String, String>) = add(name, yamlMap(value))

	@YamlDsl fun add(name: String, value: String?) = if (value == null) Yaml.Scalar.NullLiteral else add(name, yaml(value))
	@YamlDsl fun add(name: String, value: Long?) = if (value == null) Yaml.Scalar.NullLiteral else add(name, yaml(value))
	@YamlDsl fun add(name: String, value: Double?) = if (value == null) Yaml.Scalar.NullLiteral else add(name, yaml(value))
	@YamlDsl fun add(name: String, value: Boolean?) = if (value == null) Yaml.Scalar.NullLiteral else add(name, yaml(value))
	@YamlDsl fun add(name: String, value: YamlExport?) = if (value == null) Yaml.Scalar.NullLiteral else add(name, yaml(value))
	@YamlDsl fun add(name: String, value: Nothing?) = Yaml.Scalar.NullLiteral

	@YamlDsl fun addNotNull(name: String, value: String?) = if (value == null) {} else add(name, yaml(value))
	@YamlDsl fun addNotNull(name: String, value: Long?) = if (value == null) {} else add(name, yaml(value))
	@YamlDsl fun addNotNull(name: String, value: Double?) = if (value == null) {} else add(name, yaml(value))
	@YamlDsl fun addNotNull(name: String, value: Boolean?) = if (value == null) {} else add(name, yaml(value))
	@YamlDsl fun addNotNull(name: String, value: YamlExport?) = if (value == null) {} else add(name, yaml(value))

	@JvmName("addListNotEmpty")
	@YamlDsl fun addNotEmpty(name: String, value: List<YamlExport>) = if (value.isEmpty()) {} else add(name, value)
	@YamlDsl fun addNotEmpty(name: String, value: Iterable<YamlExport>) = addNotEmpty(name, value.toList())
	@JvmName("addStringsNotEmpty")
	@YamlDsl fun addNotEmpty(name: String, value: Iterable<String>) = addNotEmpty(name, value.map(::yaml))
	@JvmName("addLongsNotEmpty")
	@YamlDsl fun addNotEmpty(name: String, value: Iterable<Long>) = addNotEmpty(name, value.map(::yaml))
	@JvmName("addDoublesNotEmpty")
	@YamlDsl fun addNotEmpty(name: String, value: Iterable<Double>) = addNotEmpty(name, value.map(::yaml))
	@JvmName("addBooleansNotEmpty")
	@YamlDsl fun addNotEmpty(name: String, value: Iterable<Boolean>) = addNotEmpty(name, value.map(::yaml))
	@YamlDsl fun addNotEmpty(name: String, value: Map<Yaml, Yaml>) = if (value.isEmpty()) {} else add(name, value)
	@JvmName("addStringsMapNotEmpty")
	@YamlDsl fun addNotEmpty(name: String, value: Map<String, String>) = if (value.isEmpty()) {} else add(name, value)
}

fun yamlMap(
	block: YamlMapScope.() -> Unit
): Yaml.Collection.MapLiteral {
	val data = HashMap<Yaml, Yaml>()

	val dsl = object : YamlMapScope {
		override fun add(name: String, value: Yaml) {
			data[yaml(name)] = value
		}
	}

	block(dsl)

	return yamlMap(data)
}
