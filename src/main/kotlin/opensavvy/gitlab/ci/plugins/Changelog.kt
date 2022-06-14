package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.*
import opensavvy.gitlab.ci.script.shell

fun Job.publishChangelogToTelegram() {
	image("registry.gitlab.com/clovis-ai/dotfiles:latest")

	script {
		shell("git changelog --format telegram-html --incoming >changelog-telegram")
		shell("announce-telegram changelog-telegram \"\$CHAT_IDS\" || echo \"Couldn't send the message via Telegram\"")
	}

	artifacts {
		include("changelog-telegram")
		always()
	}
}

fun Job.publishChangelogToDiscord() {
	image("registry.gitlab.com/clovis-ai/dotfiles:latest")

	script {
		shell("git changelog --format discord --incoming >changelog-discord.json")
		shell("announce-discord changelog-discord.json \"\$DISCORD_CHAT\" \"\$DISCORD_TOKEN\" || echo \"Couldn't send the message via Discord\"")
	}

	artifacts {
		include("changelog-discord.json")
		always()
	}
}
