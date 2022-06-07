package opensavvy.gitlab.ci.yaml

fun yaml(value: String) = Yaml.Scalar.StringLiteral(value)
fun yaml(value: Long) = Yaml.Scalar.IntegerLiteral(value)
fun yaml(value: Double) = Yaml.Scalar.FloatingLiteral(value)
fun yaml(value: Boolean) = Yaml.Scalar.BooleanLiteral(value)

fun yaml(value: String?) = if (value == null) Yaml.Scalar.NullLiteral else yaml(value)
fun yaml(value: Long?) = if (value == null) Yaml.Scalar.NullLiteral else yaml(value)
fun yaml(value: Double?) = if (value == null) Yaml.Scalar.NullLiteral else yaml(value)
fun yaml(value: Boolean?) = if (value == null) Yaml.Scalar.NullLiteral else yaml(value)
fun yaml(value: Nothing?) = Yaml.Scalar.NullLiteral

fun yaml(vararg values: Yaml) = Yaml.Collection.ListLiteral(*values)
fun yaml(vararg values: Pair<Yaml, Yaml>) = Yaml.Collection.MapLiteral(*values)

fun yaml(values: List<Yaml>) = Yaml.Collection.ListLiteral(values)
fun yaml(values: Map<Yaml, Yaml>) = Yaml.Collection.MapLiteral(values)
