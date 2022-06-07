package opensavvy.gitlab.ci.utils

operator fun StringBuilder.plusAssign(text: CharSequence) {
	this.append(text)
}
