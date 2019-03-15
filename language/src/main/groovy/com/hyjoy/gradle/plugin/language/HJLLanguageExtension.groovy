package com.hyjoy.gradle.plugin.language

class HJLLanguageExtension {

    String configFile

    HJLLanguageExtension configFile(String path) {
        this.configFile = path
        return this
    }
}