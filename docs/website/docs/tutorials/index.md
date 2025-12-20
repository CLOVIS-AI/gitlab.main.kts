# Getting started

Welcome to GitLab CI in Kotlin! This tutorial teaches how to create a simple GitLab CI pipeline in Kotlin and configure the project to use it.

Pre-requisites:

- Basic knowledge of command-line usage.
- [Install Java](https://www.java.com/en/download/help/download_options.html).
- [Install the Kotlin compiler](https://kotlinlang.org/docs/command-line.html).

If you prefer using an IDE, IntelliJ Community will install all of these automatically.

## Authoring the pipeline

<div class="annotate" markdown>

Create a file named `gitlab-ci.main.kts` (1) with the following content:

</div>

1. The name of the file can be whatever you prefer, `gitlab-ci.main.kts`, `.gitlab-ci.main.kts`, `my-project.main.kts` or whatever else.<br/>
   However, it **must** end with `.main.kts`, otherwise [dependencies cannot be downloaded](https://github.com/Kotlin/kotlin-script-examples/blob/master/jvm/main-kts/MainKts.md).

```kotlin
#!/usr/bin/env kotlin

@file:DependsOn("dev.opensavvy.gitlab:gitlab-ci-kotlin-jvm:VERSION-HERE") //(1)!

import opensavvy.gitlab.ci.*
import opensavvy.gitlab.ci.script.*
import opensavvy.gitlab.ci.plugins.*

val pipeline = gitlabCi {
	val test by stage()

	val helloWorld by job(stage = test) {
		script {
			shell("echo 'Hello world'") //(2)!
		}
	}
}

pipeline.println()
```

1. See the [list of published versions](https://gitlab.com/opensavvy/automation/gitlab-ci.kt/-/releases).
2. If you're using IntelliJ, you should automatically see that it recognizes `echo 'Hello world'` is a shell command, and uses shell code highlighting as well as auto-completion.

In recent versions of IntelliJ, you should be able to execute the file directly. Otherwise, you can run it from the terminal:

=== "IntelliJ"

    Simply open the file. 

    At the top of the file, IntelliJ may prompt you to mark the file as executable.

    To run the script, use the green arrow that appears on the first line of the file.

=== "Linux & macOS"

    Ensure the file is marked as executable:

    ```sh
    $ chmod u+x gitlab-ci.main.kts
    ```

    Then, execute the file:

    ```sh
    $ ./gitlab-ci.main.kts
    ```

=== "Windows"

    Execute the following command, replacing `kotlin` by the path to where you installed Kotlin:

    ```sh
    kotlin gitlab-ci.main.kts
    ```

The script should print:
```yaml
stages:
  - test

helloWorld:
  stage: test
  script:
    - echo "Hello world"
```

Congratulation, you have created your first GitLab CI pipeline in Kotlin!

!!! note "Performance"
    On the first run, the Kotlin compiler needs to initialize itself and download dependencies, so it may a bit slow. Next runs should be near-instantaneous.

!!! tip
    To view the documentation in IntelliJ, press CTRL + click on any symbol coming from this library, which will open the decompiled code. At the top of the file, find the "Download sources" button. Once downloading has completed, go back to your script. Quick documentation (CTRL Q by default) should now show the documentation for the symbol you're looking at. 

    If you can, consider contributing documentation improvements.

## Deploying the pipeline

GitLab doesn't natively recognize Kotlin files as being pipeline configuration. We _could_ commit the generated Yaml file in the repository, but that we would need to do so again each time we change the Kotlin files, which is tedious and easy to forget.

Instead, we will use GitLab's [downstream pipelines](https://docs.gitlab.com/ee/ci/pipelines/downstream_pipelines.html): a CI pipeline can start a child pipeline with a different configuration, including [a generated configuration](https://docs.gitlab.com/ee/ci/pipelines/downstream_pipelines.html#dynamic-child-pipelines).

Create a file named `.gitlab-ci.yml` at the root of the repository, with the following content:
```yaml
compile kotlin pipeline:
  image: registry.gitlab.com/opensavvy/automation/gitlab-ci.kt/kotlin:latest #(1)!
  script:
    - ./gitlab-ci.main.kts > kotlin-ci.yml
  
run kotlin pipeline:
  trigger:
    include:
      - artifact: kotlin-ci.yml
        job: "compile kotlin pipeline"
    strategy: depend
```

1. Use any Docker image which contains the `kotlin` binary. We build a custom `kotlin` image that you can find [here](https://gitlab.com/opensavvy/automation/gitlab-ci.kt/container_registry/9576542).

Now, each time you push a new commit, GitLab will recompile the Kotlin script to generate the Yaml configuration file, and then start a child pipeline running that Yaml file.

You should now have a working configuration where you can configure a pipeline in Kotlin that is executed by GitLab.
If you have any difficulties, don't hesitate to contact us using any of the methods mentioned in [the home page](../index.md).
