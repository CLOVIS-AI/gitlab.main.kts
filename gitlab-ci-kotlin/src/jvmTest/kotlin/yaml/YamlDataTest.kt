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

package opensavvy.gitlab.ci.yaml

import opensavvy.prepared.runner.testballoon.preparedSuite
import org.intellij.lang.annotations.Language

val YamlDataTest by preparedSuite {

	test("Single line string") {
		val text = Yaml.Scalar.StringLiteral("Hello world")

		assertEqualsYaml("""
			Hello world
			""", text
		)
	}

	test("Multi-line string") {
		val text = Yaml.Scalar.StringLiteral("""
			Hello
			World
			  With bonus indentation
		""".trimIndent())

		assertEqualsYaml("""
			|
			  Hello
			  World
			    With bonus indentation
			""", text
		)
	}

	test("List") {
		val list = Yaml.Collection.ListLiteral(
			listOf(
				Yaml.Scalar.StringLiteral("Item 1"),
				Yaml.Scalar.StringLiteral("Item 2"),
				Yaml.Scalar.IntegerLiteral(1),
				Yaml.Scalar.FloatingLiteral(0.5),
			)
		)

		assertEqualsYaml("""
			
			  - Item 1
			  - Item 2
			  - 1
			  - 0.5
			""", list)
	}

	test("Map") {
		val map = Yaml.Collection.MapLiteral(
			Yaml.Scalar.StringLiteral("key") to Yaml.Scalar.StringLiteral("value"),
			Yaml.Scalar.StringLiteral("a number value") to Yaml.Scalar.IntegerLiteral(100),
			Yaml.Scalar.StringLiteral("sub fields") to Yaml.Collection.MapLiteral(
				Yaml.Scalar.StringLiteral("some key") to Yaml.Scalar.StringLiteral("some value"),
				Yaml.Scalar.StringLiteral("some list") to Yaml.Collection.ListLiteral(
					Yaml.Scalar.FloatingLiteral(512.7),
					Yaml.Scalar.StringLiteral("it works :)")
				)
			)
		)

		assertEqualsYaml("""
			
			key: value
			a number value: 100
			sub fields: 
			  some key: some value
			  some list: 
			    - 512.7
			    - 'it works :)'
""", map)
	}

}

private fun assertEqualsYaml(@Language("yaml") expected: String, actual: Yaml) {
	val generated = actual.toYamlString()
	check(expected.trimIndent() == generated.removeSuffix("\n"))

	println(generated)
}
