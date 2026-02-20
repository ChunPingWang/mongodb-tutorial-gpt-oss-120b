pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "mongodb-spring-course"
include("m01-rdb-vs-nosql")
include("m02-nosql-landscape")
include("m03-environment-setup")
include("m04-document-thinking")
include("m05-spring-data-crud")
include("m06-query-dsl")
include("m07-aggregation-pipeline")
include("m08-schema-validation")
include("m09-transactions")
include("m10-ddd-aggregate-modeling")
include("m11-polymorphism-inheritance")
include("m12-event-sourcing")
include("m13-cqrs-read-model")
include("m14-saga-pattern")
include("m15-indexing-performance")
include("m16-change-streams")
include("m17-observability")
include("m18-migration-versioning")
include("m19-banking-capstone")
include("m20-insurance-capstone")
include("m21-ecommerce-capstone")
