package io.jenkins.tools.pluginmodernizer.core.model;

/**
 * Extracted platform from Jenkinsfile
 */
public record PlatformConfig(Platform name, JDK jdk, String jenkins, boolean implicit) {}
;
