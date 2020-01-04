import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

version = "2019.1"

project {
    buildType(Build)
    buildType(BuildPublish)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    params {
        param("image.createdat", "")
    }

    steps {
        script {
            name = "Get Date"
            scriptContent = """echo "##teamcity[setParameter name='image.createdat' value='${'$'}(date -u -Iseconds)']""""
        }
        dockerCommand {
            commandType = build {
                source = path {
                    path = "Dockerfile"
                }
                namesAndTags = "kierranm/tails"
                commandArgs = """--pull --label "org.opencontainers.image.revision"="%build.vcs.number%" --label "org.opencontainers.image.version"="%teamcity.build.branch%" --label "org.opencontainers.image.created"="%image.createdat%""""
            }
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "%github.commit_status_publisher_token%"
                }
            }
        }
        pullRequests {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            provider = github {
                authType = vcsRoot()
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }
})

object BuildPublish : BuildType({
    name = "Build & Publish"

    vcs {
        root(DslContext.settingsRoot)
    }

    params {
        param("image.createdat", "")
        param("image.version", "")
    }

    steps {
        script {
            name = "Get Date"
            scriptContent = """echo "##teamcity[setParameter name='image.createdat' value='${'$'}(date -u -Iseconds)']""""
        }
        script {
            name = "Get Version"
            scriptContent = """echo "##teamcity[setParameter name='image.version' value='${'$'}(echo '%teamcity.build.branch%' | sed 's/^v\(.*\)/\1/')']""""
        }
        dockerCommand {
            commandType = build {
                source = path {
                    path = "Dockerfile"
                }
                namesAndTags = """
                    kierranm/tails:latest
                    kierranm/tails:%build.vcs.number%
                    kierranm/tails:%image.version%
                """.trimIndent()
                commandArgs = """--pull --label "org.opencontainers.image.revision"="%build.vcs.number%" --label "org.opencontainers.image.version"="%image.version%" --label "org.opencontainers.image.created"="%image.createdat%""""
            }
        }
    }

    triggers {
        vcs {
            branchFilter = "+:v*"
        }
    }
})
