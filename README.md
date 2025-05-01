# Kriteria

Kriteria is a type-safe Kotlin query DSL for Hibernate that provides a concise, readable alternative to writing HQL or using the Criteria API directly.
It leverages Kotlin's language features to create a fluent, expressive API for database queries.

## Features

- **Type-safe queries**: Compile-time checking of entity and property references
- **Fluent API**: Natural, readable query syntax that resembles SQL
- **Comprehensive query support**: Select, update, delete, joins, aggregates, and more
- **Modern Kotlin**: Uses Kotlin's context receivers for clean, concise code
- **Metamodel generation**: Symbol processor generates a typed metamodel from the entity model

## Installation

Add the following dependencies to your Gradle build file:

```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
}

ksp {
    arg("kriteriaProcessorTargetPackage", "my.generated.code")
}

dependencies {
    implementation("fi.evident.kriteria:core:VERSION")

    ksp("fi.evident.kriteria:symbol-processor:VERSION")
}
```

## Examples

Here are some examples. 
For more examples, check the [integration tests](integration-tests/src/test/kotlin/fi/evident/kriteria/test).

### Queries

```kotlin
// Find all employees using a just predicate
em.findAllByEntity(Employee, order = { asc(name) }) {
    it.department.name isEqualTo "Engineering"
}

// Find all employees using a more general query
em.findAll<Employee> { 
    val e = from(Employee)

    select(e)
    where(e.department.name isEqualTo "Engineering")
    order(asc(e.name))
}

// Find names of employees that don't have a department or who are in HR
em.findAll<String> { 
    val e = from(Employee)
    val d = leftJoin(e.department)

    select(e.name)
    where(or(isNull(d), d.name isEqualTo "HR"))
}

// Find departments that have employees with salary greater than 50 000
em.findAll<Department> {
    val d = from(Department)
    val e = innerJoinSet(d.employees)

    selectDistinct(d)
    where(e.salary isGreaterThan 50000)
}

// Same as above, but using a sub-query
em.findAll<Department> {
    val d = from(Department)
    val e = innerJoinSet(d.employees)

    select(d)
    where(exists(Employee) { e ->
        and(
            e.department.id isEqualTo d.id,
            e.salary isGreaterThan 50000
        )
    })
}

// Map joins
em.findAll<Employee> {
    val e = from(Employee)
    val roles = innerJoinMap(e.projectRoles)

    select(e)
    where(roles.key isEqualTo "Developer")
}
```

### Projections

```kotlin
// @CriteriaConstructor generates a typed constructEmployeeOverview function
class EmployeeOverview @CriteriaConstructor constructor(
    val name: String,
    val salary: Int,
    val departmentName: String,
)

// Find information about all employees within the salary range
em.findAll<EmployeeOverview> {
    val e = from(Employee)

    select(constructEmployeeOverview(e.name, e.salary, e.department.name))
    where(e.salary isInRange 40000..50000)
}
```

### Bulk updates

```kotlin
em.update(Employee) {
    set(it.salary, it.salary + 1000)
    where(it.department.name isEqualTo "Engineering")
}

em.delete(Employee) {
    e.salary isLessThanOrEqualTo 30000
}
```
