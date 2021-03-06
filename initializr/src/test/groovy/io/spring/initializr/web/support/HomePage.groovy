/*
 * Copyright 2012-2014 the original author or authors.
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

package io.spring.initializr.web.support

import com.gargoylesoftware.htmlunit.WebResponse
import com.gargoylesoftware.htmlunit.html.*

/**
 * Represent the home page of the service.
 *
 * @author Stephane Nicoll
 */
abstract class HomePage {

	String groupId
	String artifactId
	String name
	String description
	String packageName
	String type
	String packaging
	String language
	List<String> dependencies = []

	protected final HtmlPage page

	protected HomePage(HtmlPage page) {
		this.page = page
	}

	/**
	 * Generate a project using the specified temporary directory. Return
	 * the {@link WebResponse}.
	 * @see org.junit.rules.TemporaryFolder
	 */
	WebResponse generateProject() {
		setup()
		def submit = page.getElementByName('generate-project')
		def newMessagePage = submit.click();
		newMessagePage.webResponse
	}

	/**
	 * Setup the {@link HtmlPage} with the customization of this
	 * instance. Only applied when a non-null value is set
	 */
	protected void setup() {
		setTextValue('groupId', groupId)
		setTextValue('artifactId', artifactId)
		setTextValue('name', name)
		setTextValue('description', description)
		setTextValue('packageName', packageName)
		select('language', language)
		selectDependencies(dependencies)
	}

	protected void setTextValue(String elementId, String value) {
		if (value) {
			def input = page.getHtmlElementById(elementId)
			input.setValueAttribute(value)
		}
	}

	protected abstract void select(String selectId, String value)

	protected void selectDependencies(List<String> dependencies) {
		def styles = page.getElementsByName("style")
		def allStyles = [:]
		for (HtmlCheckBoxInput checkBoxInput : styles) {
			allStyles[checkBoxInput.getValueAttribute()] = checkBoxInput
		}
		for (String dependency : dependencies) {
			def checkBox = allStyles.get(dependency)
			if (checkBox) {
				checkBox.checked = true
			} else {
				throw new IllegalArgumentException(
						"No dependency with name '$dependency' was found amongst '${allStyles.keySet()}")
			}
		}
	}

}
