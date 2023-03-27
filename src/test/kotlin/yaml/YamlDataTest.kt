package opensavvy.gitlab.ci.yaml

import org.intellij.lang.annotations.Language
import kotlin.test.Test
import kotlin.test.assertEquals

class YamlDataTest {

	@Test
	fun singleLineString() {
		val text = Yaml.Scalar.StringLiteral("Hello world")

		assertEqualsYaml("""
			Hello world
			""", text
		)
	}

	@Test
	fun multiLineString() {
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

	@Test
	fun list() {
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

	@Test
	fun map() {
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

	fun assertEqualsYaml(@Language("yaml") expected: String, actual: Yaml) {
		val generated = actual.toYamlString()
		assertEquals(expected.trimIndent(), generated.removeSuffix("\n"))

		println(generated)
	}
}
