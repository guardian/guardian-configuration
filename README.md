# Guardian Configuration Framework #

Framework support for a standard webapp configuration scheme.

## Building it ##

* Ensure you have SBT installed, we recommend 0.13.5

## Compatibility  ##

* Latest version 4.0 for Scala 2.10 & 2.11

## Guiding Principles ##

*   _Support the minimum viable requirement set_

    An unnecessarily complex application configuration procedure additionally
    and unnecessarily complicates the building, testing, continuous integration,
    and deployment of that application.

*   _Use puppet_

    Configuration should use puppet where possible to manage operations type
    property specification. By managing properties in puppet which refer
    primarily to the systems organisation or the environment/stage in which the
    webapp is running, routine systems administration and reorganisation is
    decoupled from webapp deployment.


## The Configuration Standard ##

Webapp properties fall into a number of categories and are acquired from files
and classpath resources which are distributed in various locations, under puppet
or version control, according to their variety:

1.  _Installation properties_: Properties relating to how a specific machine is
    installed, e.g. where the Solr home directory is located, how much memory
    to assign the container.

    These properties are provided as commandline Java parameters passed to the
    container in an `init.d` script and are managed by puppet.

2.  _Setup properties_: Stage environment and domain properties describing
    the machine on which the webapp container is running. These properties are
    not suitable for use in the webapp itself but are necessary for the
    configuration framework to correctly configure the webapp.

    These properties are provided by a puppet managed Java properties file
    located at `/etc/gu/install_vars` and must contain the following properties:

    * `STAGE` denoting the environment name, e.g. `DEV`, `PROD`, `QA`.
    * `INT_SERVICE_DOMAIN` denoting the DNS domain.

3.  _Developer account override properties_: Property settings used locally on
    developer machines to facilitate test runs or local integration testing.
    These settings should not used for any purpose other than as a temporary
    measure on a developer machine.

    These properties are provided by a local account managed Java properties
    file located at `~/.gu/<appname>.properties`.

4.  _Operations properties_: Properties which are application specific and
    primarily of interest to operations, e.g. database connection strings,
    cooperating hosts, etc.

    (The term operations properties is used in preference to systems properties
    to avoid confusion with Java system properties.)

    These properties are provided by a puppet managed Java properties file
    located at `/etc/gu/<appname>.properties`. These properties are not
    subdivided like developer properties because puppet templating functionality
    is more suitable than multiple files.

5.  _Developer properties(stage based)_: Properties which are application
    specific, primarily of interest to developers, and which vary by stage,
    e.g. stdout logging configuration, a send email enabling flag, etc.

    These properties are provided by a war resource usually located at
    `/conf/<stage>.properties`.

6.  _Developer properties(service domain based)_: Properties which are application
    specific, primarily of interest to developers, and which vary by service
    domain.

    Stage based properties are preferable in nearly every case. However, if the
    stage is colocated then it may be necessary to specify properties that vary
    by service domain as well, e.g. colo specific load balancer URLs, etc.

    These properties are provided by a war resource usually located at
    `/conf/<service.domain>.properties`.

7.  _Developer properties(common)_: Properties which are application
    specific, primarily of interest to developers, and which do not vary by
    stage or service domain, e.g. external web service urls.

    These properties are provided by a war resource usually located at
    `/conf/global.properties`.

8. _Java System properties_: Properties which are passed to the JVM when started.
	These should generally not be used unless required by deployment in a restrictive hosting
	environment, i.e. Heroku.


Only properties of types 3 through 7 should be provided to applications. They
should be sourced as specified, first property definition wins, in the order
listed above.



## Extended Functionality ##

Most, if not all, applications can be configured entirely within the framework
above.

Certain extended functionalities, described below, are presently supported but
under consideration for deprecation. The concern is to find the correct balance
between number of requirements and convenience.

Feedback and use cases which demonstrate a requirement for any of the following
would be greatly appreciated.

Given the threat of deprecation these extensions should be avoided where
possible.

*   _Java System property placeholder expansion_

    Property values may contain Java system property placeholder substrings
    of the form `${propertyname}` which are substituted for actual values from
    `java.lang.System.getProperties` at runtime.

*   _Environment property placeholder expansion_

    Similarly, property values may contain environment property placeholder
    substrings of the form `${env.propertyname}` which are substituted for
    actual values from `java.lang.System.getEnv` at runtime.

*   _default.properties_

    Using developer account override properties in `~/.gu/<appname>.properties`
    means applications can be developed and run in environments without any
    operations support at the cost of some custom maintanence.

    It can be desirable, however, for applications to run on unmanaged machines
    with zero configuration, e.g. open developed applications.

    Using global developer settings may not be appropriate if default values
    can be assumed. To this end, a default stage and service domain setting is
    assumed if none is available from `/etc/gu/install_vars`. In both cases,
    this default is taken to be the literal text `default`.

    The upshot is that a stage based developer properties and a service domain
    based developer properties are provided to applications when running in
    environments without setup properties. These both happen to be sourced
    from the same location, `default.properties`, so there is de facto a
    single source location for properties for unconfigured machines.

    The present implementation contains the possibility for very unexpected
    configuration if `/etc/gu/install_vars` is partially specified and a
    `default.properties` is provided.

    Use at your own risk.

*   _Providing stage based properties for variants of stage name_

    Provides support for allowing a replica environment to abide by a specific 
    stage's properties when the stage already exists.

    For example: 

    A stage named 'CODE' already exists and you wish to test a feature on a 
    replica stack with the same properties as the CODE environment.

    To do this, simply give your stage name in the following format: 

    stageNameOfPropertiesToReplicate___yourOwnUniqueStageName

    E.g. CODE___TEST-NEW-FUNCTIONALITY

    The delimiter to activate this is three underscores ('___') after the 
    stage whose properties you wish to replicate its properties.


## Common Pitfalls ##

1.  _Adding new requirements_

    If your application configuration does not conform to this scheme, the
    default assumption should be that your configuration requirements should be
    revisited, rather than the logic in this framework is insufficient.

    Extended functionality may be necessary but the prejudice should be against
    augmenting this framework.

2.  _Overriding installation properties with Java Systems properties_

    This is a development convenience which is very inconsistent in production.
    Both the installation and Java Systems properties are configured by
    operations, there should be no call for overriding.


## How to add Guardian Configuration to your webapp ##

Include the most recent version of the framework using your favourite
dependency management tool.

    build.sbt:
      resolvers += "Guardian GitHub Repository" at "http://guardian.github.com/maven/repo-releases"
      libraryDependencies += "com.gu" % "configuration" % latestConfigurationVersion

To process configuration property files:

    object Configuration {
       import com.gu.conf.ConfigurationFactory

       // Using typed public accessors facilitates better IDE inspection.
       lazy val dbpassword = configuration("db.password")

       private lazy val configuration = ConfigurationFactory("application-name")
    }

This defaults to reading resource configuration property files from `conf` on the classpath. In
the case where there is contention for the default location, the configuration source location may be
specified in the `getConfiguration` call:

       private lazy val configuration = ConfigurationFactory("application-name", "conf/application-name")


## Example ##

`MySolrIndexer` is a Solr index population webapp running in a Jetty container
which periodically downloads new source data from an FTP site, transforms it
into Solr update documents and posts them to a Solr somewhere.

For purposes of illustration this example follows the actual configuration in a
QA environment.

1.  _Installation properties_: The puppetted `/etc/init.d/mysolrindexer` daemon
    service script sets up `jetty.home`, `java.io.tmpdir`, and various other
    properties of interest almost entirely to operations.

2.  _Setup properties_: The puppetted `/etc/install_vars` file for the QA
    environment looks like the following:

        STAGE=QA
        INT_SERVICE_DOMAIN=guqa.gnl

    It is of interest to application writers as it indicates the names of the
    property files which will be read later for stage and service domain
    specific configuration. In this example environment, stage specific
    configuration will be read from `QA.properties` and service domain
    specific configuration will be read from `guqa.gnl.properties`.

3.  _Developer account override properties_: The QA environment does not require
    any developer account overrides. The source is still checked. In this
    case is empty but it would have been located at `~/.gu/mysolrindexer` in the
    home directory of the account running the webapp container.

4.  _Operations properties_: In this example, the operations properties include the
    base Solr URL for the Solr to index and a location to archive downloaded FTP
    source data. The following is puppeted to
    `/etc/gu/mysolrindexer.properties`:

        solr.master=http://qa.mysolrindexer.com:8983/solr
        download.archive=file:/jetty-apps/mysolrindexer/archive

5.  _Developer properties(stage based)_: The classpath resource
    `/conf/QA.properties` contains properties for accessing the slower QA
    version of the data source and the email address for incident reporting for
    the QA instance.

        datasource.url=ftp://qa.datasource.com
        datasource.connection.timeout.ms=60000
        report.email.address=qa@mysolrindexer.com

6.  _Developer properties(service domain based)_: This property source is unused
    since the QA environment is not colocated or external but would have been
    located in the classpath resource at `/conf/guqa.gnl.properties` in the
    webapp.

7.  _Developer properties(common)_: The remaining properties are common across
    all stages and service domains and provided from the classpath resource at
    `/conf/global.properties`. Here we use the first definition wins feature to
    indicate a default `datasource.connection.timeout.ms`:

        datasource.connection.timeout.ms=1000

A `Configuration` for `mysolrindexer` is factoried in Scala code like follows:

    val conf = (new ConfigurationFactory) getConfiguration "mysolrindexer"

And result in a `conf.toString` with contents:

    # Properties from file:///home/jetty/.gu/mysolrindexer.properties

    # Properties from file:///etc/gu/mysolrindexer.properties
    solr.master=http://qa.mysolrindexer.com:8983/solr
    download.archive=file:/jetty-apps/mysolrindexer/archive

    # Properties from classpath:conf/QA.properties
    datasource.url=ftp://qa.datasource.com
    datasource.connection.timeout.ms=60000
    report.email.address=qa@mysolrindexer.com

    # Properties from classpath:conf/guqa.gnl.properties

    # Properties from classpath:conf/global.properties

	# Properties from Java System

Note that nonactive `datasource.connection.timeout.ms` from `global.properties`
is not reported.

### Heroku Example

When deploying to Heroku, the file system is not available to read properties from.
Heroku recommends Environment variables should be used when requiring properties from outside of the application.

[An up to date article is available on Heroku](http://devcenter.heroku.com/articles/config-vars) about configuration but briefly, here is how you can use the Heroku CLI application:

	heroku config:add my.application.property=SomeValue -a my_app_name

You you view configuration:

	heroku config -a my_app_name

Remove by:

	heroku config:remove my.application.property -a my_app_name

