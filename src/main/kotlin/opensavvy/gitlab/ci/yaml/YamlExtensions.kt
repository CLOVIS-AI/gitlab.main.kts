package opensavvy.gitlab.ci.yaml

import opensavvy.gitlab.ci.YamlExport

fun yaml(value: String) = Yaml.Scalar.StringLiteral(value)
fun yaml(value: Long) = Yaml.Scalar.IntegerLiteral(value)
fun yaml(value: Double) = Yaml.Scalar.FloatingLiteral(value)
fun yaml(value: Boolean) = Yaml.Scalar.BooleanLiteral(value)

fun yaml(value: String?) = if (value == null) Yaml.Scalar.NullLiteral else yaml(value)
fun yaml(value: Long?) = if (value == null) Yaml.Scalar.NullLiteral else yaml(value)
fun yaml(value: Double?) = if (value == null) Yaml.Scalar.NullLiteral else yaml(value)
fun yaml(value: Boolean?) = if (value == null) Yaml.Scalar.NullLiteral else yaml(value)
fun yaml(value: Nothing?) = Yaml.Scalar.NullLiteral
fun yaml(value: YamlExport) = value.toYaml()

fun yaml(vararg values: YamlExport) = Yaml.Collection.ListLiteral(values.map { it.toYaml() })
fun yaml(vararg values: Pair<YamlExport, YamlExport>) =
	Yaml.Collection.MapLiteral(values.map { (k, v) -> k.toYaml() to v.toYaml() })

fun yaml(values: List<Yaml>) = Yaml.Collection.ListLiteral(values)
fun yaml(values: Set<Yaml>) = yaml(values.toList())
fun yaml(values: Map<Yaml, Yaml>) = Yaml.Collection.MapLiteral(values)

@JvmName("yamlAuto")
fun yaml(values: List<YamlExport>) = Yaml.Collection.ListLiteral(values.map { it.toYaml() })

@JvmName("yamlAuto")
fun yaml(values: Set<YamlExport>) = yaml(values.toList())

@JvmName("yamlAuto")
fun yaml(values: Map<YamlExport, YamlExport>) =
	Yaml.Collection.MapLiteral(values.map { (k, v) -> k.toYaml() to v.toYaml() })
