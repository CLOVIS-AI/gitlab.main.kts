package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.yaml

enum class When : YamlExport {
	Always {
		override fun toYaml() = yaml("always")
	},
	OnSuccess {
		override fun toYaml() = yaml("on_success")
	},
	OnFailure {
		override fun toYaml() = yaml("on_failure")
	},
	;
}
