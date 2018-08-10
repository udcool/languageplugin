package com.hyjoy.gradle.plugin.language

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 */
class HJLLanguagePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {

        project.extensions.create("hjlanguage", HJLLanguageExtension)

        project.getTasks().create("a_hjlanguage", HJLLTask.class)

        project.gradle.addListener(new TimeTrace())

    }
}
