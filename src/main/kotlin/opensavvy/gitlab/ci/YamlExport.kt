package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml

/**
 * Objects that can be converted to Yaml.
 */
interface YamlExport {

	/**
	 * Converts this object into a Yaml object.
	 */
	fun toYaml(): Yaml

}
