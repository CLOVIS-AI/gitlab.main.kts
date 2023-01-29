package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.yaml
import opensavvy.gitlab.ci.yaml.yamlMap

data class Depends(
	val job: Job,
	val artifacts: Boolean,
	val optional: Boolean,
) : YamlExport {

	override fun toYaml() = yamlMap(
		yaml("job") to yaml(job.name),
		yaml("artifacts") to yaml(artifacts),
		yaml("optional") to yaml(optional)
	)

}
