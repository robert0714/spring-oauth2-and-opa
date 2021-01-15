## About Docker Image Creating
reference: https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#extended-usage


#### Build to Docker daemon

Jib can also build your image directly to a Docker daemon. This uses the `docker` command line tool and requires that you have `docker` available on your `PATH`.

```shell
mvn compile jib:dockerBuild
```

If you are using [`minikube`](https://github.com/kubernetes/minikube)'s remote Docker daemon, make sure you [set up the correct environment variables](https://github.com/kubernetes/minikube/blob/master/docs/reusing_the_docker_daemon.md) to point to the remote daemon:

```shell
eval $(minikube docker-env)
mvn compile jib:dockerBuild
```

Alternatively, you can set environment variables in the Jib configuration. See [`dockerClient`](#dockerclient-object) for more configuration options.

#### Build an image tarball

You can build and save your image to disk as a tarball with:

```shell
mvn compile jib:buildTar
```

This builds and saves your image to `target/jib-image.tar`, which you can load into docker with:

```shell
docker load --input target/jib-image.tar
```

### Bind to a lifecycle

You can also bind `jib:build` to a Maven lifecycle, such as `package`, by adding the following execution to your `jib-maven-plugin` definition:

```xml
<plugin>
  <groupId>com.google.cloud.tools</groupId>
  <artifactId>jib-maven-plugin</artifactId>
  ...
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>build</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Then, you can build your container image by running:

```shell
mvn package
```

### Additional Build Artifacts

As part of an image build, Jib also writes out the _image digest_ and the _image ID_. By default, these are written out to `target/jib-image.digest` and `target/jib-image.id` respectively, but the locations can be configured using the `<outputFiles><digest>` and `<outputFiles><imageId>` configuration properties. See [Extended Usage](#outputpaths-object) for more details.