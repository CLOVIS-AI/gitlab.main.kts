/*
 * Copyright (c) 2022-2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.Job
import opensavvy.gitlab.ci.When
import opensavvy.gitlab.ci.script.shell

fun Job.publishChangelogToTelegram() {
	image("registry.gitlab.com/clovis-ai/dotfiles:latest")

	script {
		shell("git changelog --format telegram-html --incoming >changelog-telegram")
		shell("announce-telegram changelog-telegram \"\$CHAT_IDS\" || echo \"Couldn't send the message via Telegram\"")
	}

	artifacts {
		include("changelog-telegram")
		rule(When.Always)
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
		rule(When.Always)
	}
}
