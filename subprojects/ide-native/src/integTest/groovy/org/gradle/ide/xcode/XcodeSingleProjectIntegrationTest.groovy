/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.ide.xcode

import org.gradle.ide.xcode.fixtures.AbstractXcodeIntegrationSpec

class XcodeSingleProjectIntegrationTest extends AbstractXcodeIntegrationSpec {
    def "create empty xcode project when no language plugins are applied"() {
        when:
        succeeds("xcode")

        then:
        executedAndNotSkipped(":xcodeProject", ":xcodeProjectWorkspaceSettings", ":xcode")

        def project = xcodeProject("${rootProjectName}.xcodeproj").projectFile
        project.mainGroup.assertHasChildren(['build.gradle'])
        project.assertNoTargets()
    }

    def "cleanXcode remove all XCode generated project files"() {
        given:
        buildFile << """
apply plugin: 'swift-executable'
"""

        when:
        succeeds("xcode")

        then:
        executedAndNotSkipped(":xcodeProject", ":xcodeProjectWorkspaceSettings", ":xcodeScheme${rootProjectName}Executable", ":xcodeWorkspace", ":xcodeWorkspaceWorkspaceSettings", ":xcode")

        def project = xcodeProject("${rootProjectName}.xcodeproj")
        project.projectFile.getFile().assertExists()
        project.schemeFiles*.file*.assertExists()
        project.workspaceSettingsFile.assertExists()
        project.dir.assertExists()

        when:
        succeeds("cleanXcode")

        then:
        executedAndNotSkipped(":cleanXcode")

        project.projectFile.getFile().assertDoesNotExist()
        project.schemeFiles*.file*.assertDoesNotExist()
        project.workspaceSettingsFile.assertDoesNotExist()
        project.dir.assertDoesNotExist()
    }
}
