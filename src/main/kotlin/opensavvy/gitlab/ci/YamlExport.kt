package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml

interface YamlExport {

	fun toYaml(): Yaml

}
