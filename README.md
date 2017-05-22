# Simple CI

A simple and free CI/CD server.

## What?

* CI - [Continuous integration](https://en.wikipedia.org/wiki/Continuous_integration)
* CD - [Continuous delivery](https://en.wikipedia.org/wiki/Continuous_delivery)

## Why?

| **Problem**                                                                                                                                                                        | **Solution**                                                                                    |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| Hosted CI servers are too expensive for small, bootstrapped, pre-revenue teams like ours.                                                                                          | Simple CI is free. As in beer.                                                                  |
| Free CI servers are too complex, full of bugs, and bog us down in plugin hell.                                                                                                     | Simple CI is simple. There are no plugins. Just tasks and shell scripts.                        |
| If I significantly refactor the project in a branch, the build will break because I can't update the project's CI configuration without breaking all the other branches.           | Simple CI's build configuration lives in a version-controlled file in the root of your project. |
| Some CI servers are now blessed with version-controlled configuration. However, it takes a number of pushes to remote before I can find a build configuration that actually works. | Simple CI can run your build configuration locally from the command line.                       |

## How?

Here's how Simple CI works:

1. It monitors your Git repository for changes, using either webhooks or
   polling.
2. When it detects changes on any branch, it shallow-clones the branch.
3. It then looks for a `simple-ci.xml` file in the root of the branch's source
   and, if found, parses it.
4. It spins up a new Docker container using the image from DockerHub specified
   in `simple-ci.xml`, mounting the branch's source to the container's
   filesystem.
5. It executes all the tasks defined in `simple-ci.xml` in parallel, waiting
   for dependencies to complete executing successfully where such task
   dependencies have been defined.

Once every task has executed successfully (or otherwise), the build is
complete and the results are made available.

## When?

**First, the bad news:**

This is a work in progress and not yet ready.

**Now the good news:**

This is a very high priority project and, given its simplicity, it should be
released and ready to use very soon.
