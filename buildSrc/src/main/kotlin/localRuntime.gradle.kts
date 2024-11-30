configurations {
    val localRuntime by creating
    configurations.getByName("runtimeClasspath").extendsFrom(localRuntime)
}
