# Multiplatform library template, for Nexus Portal Publisher API

This is a fork of JetBrains Kotlin Multiplatform Library template, suited for the new 
Nexus Portal Publisher API.
All new Sonatype Nexus registrants since Feb. 1st. 2024 have to use the new API
instead of traditional Maven API over https://central.sonatype.com - while this JetBrains
project template is still based on the traditional version.

For any newcomers to Sonatype Nexus, the JetBrains template is simply not usable.

This fork is to fix that situation, with using [vanniktech/gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin).

Many parts of the original template is still alive. You can try publishing on GitHub Actions
by manually starting the deployment workflow ("Deploy to central").

You can see what are different from the original template by `diff -ur their-repo this-repo`.

This modified version bumped Gradle version too though, so that the latest stable IDEA
does not refuse to open because 8.1 is too old for it.

----

## What is it?

It is the barebones library project intended to quickly bootstrap a Kotlin Multiplatform library, that is deployable to Maven Central.

It has only one function: generate the [Fibonacci sequence](https://en.wikipedia.org/wiki/Fibonacci_sequence) starting from platform-provided numbers. Also, it has a test for each platform just to be sure that tests run.

Note that no other actions or tools usually required for the library development are set up, such as [tracking of backwards compatibility]
(https://kotlinlang.org/docs/jvm-api-guidelines-backward-compatibility.html#tools-designed-to-enforce-backward-compatibility), explicit API mode,
licensing, contribution guideline, code of conduct and others.

## How do I build it?

1. - [x] Clone this repository ot just [use it as template](https://github.com/Kotlin/multiplatform-library-template/generate)
1. - [ ] Edit library module name and include it in [`settings.gradle.kts`](settings.gradle.kts#L18)
1. - [ ] Edit [`groupId` and `version`](convention-plugins/src/main/kotlin/module.publication.gradle.kts#L10-L11)
    1. If you need the Android support update namespace [there](library/build.gradle.kts#L38) too
    1. If you don't need an Android support delete the [`android` section](library/build.gradle.kts#L37-L43)
1. - [ ] Edit [build targets you need](library/build.gradle.kts#L9-L21)

At this stage, you have everything set to work with Kotlin Multiplatform. The project should be buildable (but you might need to provide actual starting values for the platforms you need).

## How do I make it build on GitHub Actions?

To make it work on GitHub actions, you need to update the [`matrix` section in `gradle.yml`](.github/workflows/gradle.yml#L25-L34). If you didn't change platforms in `build.gradle.kts` you don't need to touch anything. But still read it to understand how it works.

Also, currently, it only runs tests, but you can change this behaviour as you wish by modifying `matrix` and the Gradle [build command](.github/workflows/gradle.yml#L52)

## How do I deploy it to Maven Central?

The most part of the job is already automated for you. However, deployment to Maven Central requires some manual work from your side. 

1. - [ ] Create an account at [Sonatype issue tracker](https://issues.sonatype.org/secure/Signup!default.jspa)
1. - [ ] [Create an issue](https://issues.sonatype.org/secure/CreateIssue.jspa?issuetype=21&pid=10134) to create new project for you
1. - [ ] You will have to prove that you own your desired namespace
1. - [ ] Create a GPG key with `gpg --gen-key`, use the same email address you used to sign up to the Sonatype Jira
1. - [ ] Find your key id in the output of the previous command looking like `D89FAAEB4CECAFD199A2F5E612C6F735F7A9A519`
1. - [ ] Upload your key to a keyserver, for example 
    ```bash
    gpg --send-keys --keyserver keyserver.ubuntu.com "<your key id>"
    ```
1. - [ ] If your repository is Sonatype Nexus, it will require its ["access token" version of username and password](https://central.sonatype.org/publish/generate-token/) instead of plain Nexus username and password, so create them.
1. - [ ] Now you should create secrets available to your GitHub Actions
    1. via `gh` command
    ```bash
    gh secret set OSSRH_GPG_SECRET_KEY -a actions --body "$(gpg --export-secret-key --armor "<your key id>")"
    gh secret set OSSRH_GPG_SECRET_KEY_ID -a actions --body "<your key id>"
    gh secret set OSSRH_GPG_SECRET_KEY_PASSWORD -a actions --body "<your key password>"
    gh secret set OSSRH_PASSWORD -a actions --body "<your access token password>"
    gh secret set OSSRH_USERNAME -a actions --body "<your access token username>"
    ```
    1. Or via the interface in `Settings` → `Secrets and Variables` → `Actions`, same variables as in 1.
1. - [ ] Edit deployment pom parameters in [`module.publication.gradle.kts`](convention-plugins/src/main/kotlin/module.publication.gradle.kts#L25-L44)
1. - [ ] Edit deploy targets in [`deploy.yml`](.github/workflows/deploy.yml#L23-L36)
1. - [ ] Call deployment manually when ready [in Actions](../../actions/workflows/deploy.yml) → `Run Workflow`
1. - [ ] When you see in your account on https://oss.sonatype.org that everything is fine, you can release your staging repositories and add target `releaseSonatypeStagingRepository` to `deploy.yml` [after this line](.github/workflows/deploy.yml#L60). This way artifacts will be published to central automatically when tests pass.
