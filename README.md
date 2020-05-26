## Async Test App ##

The purpose of this application is to show the differences that different Async patterns in Spring Boot work. 

There are four options to explore with this code:

1. Sequential Streams `.stream()`
1. Parallel Streams `.parallelStream()` 
1. The `@Async` annotation
1. The `@Async` annotation with a custom ThreadPool


### Gatling ### 

This project uses the lkishalmi gatling gradle plugin. Running Gatling is simple. 

`./gradlew gatlingRun` or `./gradlew gatlingRun-{SimulationName}`

If you see that Gatling does not execute because of a silly Gradle feature, add `--rerun-tasks` to the end of the command. 


### Todo ###
Also produce a Reactive version in the same project. 

Some research has already been done (and sadly lost to git gods) that `@ConditionalOnClass` and `@ConditionalOnMissingClass` can be used on a tomcat Class name to load a WebFlux configuration instead of a Servlet (and therefore `@Controller`) configuration.

Find a way to combine all the Gatling simulation.log files together. There is a way to vanilla gatling's CLI.