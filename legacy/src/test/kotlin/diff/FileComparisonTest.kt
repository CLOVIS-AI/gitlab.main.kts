package opensavvy.gitlab.ci.diff

import org.junit.Test

class FileComparisonTest {

	@Test
	fun checkFile() {
		assertEqualsFile("diff/true.txt", "true")
	}
}
