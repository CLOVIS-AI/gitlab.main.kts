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

fun yamlList(vararg values: YamlExport) = Yaml.Collection.ListLiteral(values.map { it.toYaml() })
fun yamlMap(vararg values: Pair<YamlExport, YamlExport>) =
	Yaml.Collection.MapLiteral(values.map { (k, v) -> k.toYaml() to v.toYaml() })

fun yamlList(values: List<Yaml>) = Yaml.Collection.ListLiteral(values)
fun yamlList(values: Set<Yaml>) = yamlList(values.toList())
fun yamlMap(values: Map<Yaml, Yaml>) = Yaml.Collection.MapLiteral(values)

@JvmName("yamlAuto")
fun yamlList(values: List<YamlExport>) = Yaml.Collection.ListLiteral(values.map { it.toYaml() })

@JvmName("yamlAuto")
fun yamlList(values: Set<YamlExport>) = yamlList(values.toList())

@JvmName("yamlAuto")
fun yamlMap(values: Map<YamlExport, YamlExport>) =
	Yaml.Collection.MapLiteral(values.map { (k, v) -> k.toYaml() to v.toYaml() })

@JvmName("yamlStringList")
fun yamlList(values: List<String>) = yamlList(values.map { yaml(it) })

@JvmName("yamlStringMap")
fun yamlMap(values: Map<String, String>) = yamlMap(*values.map { (k, v) -> yaml(k) to yaml(v) }.toTypedArray())
