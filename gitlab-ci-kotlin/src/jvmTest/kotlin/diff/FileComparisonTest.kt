package opensavvy.gitlab.ci.diff

import opensavvy.prepared.runner.kotest.PreparedSpec

class FileComparisonTest : PreparedSpec({

	test("Check that we can compare a value to the contents of a file") {
		assertEqualsFile("diff/true.txt", "true")
	}

})
