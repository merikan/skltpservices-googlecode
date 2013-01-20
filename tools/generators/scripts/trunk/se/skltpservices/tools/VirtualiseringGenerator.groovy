#!/usr/bin/env groovy

package se.skltpservices.tools

import groovy.io.FileType

org.apache.commons.io.FileUtils

@Grab(group='commons-io', module='commons-io', version='1.3.2')
import org.apache.commons.io.FileUtils

@Grab(group='dom4j', module='dom4j', version='1.6.1')
import org.dom4j.io.SAXReader

/**
 * This script should help us to generate many services at one time. 
 *
 * PREREQUISITES:
 * This script is depending on the archetype named service-archetype.
 * This archetype must be installed before running the script.
 * Check the TODO below to see any flaws still not fixed.
 *
 * TO RUN:
 * Just execute ./VirtualiseringGenerator.groovy and follow the instruction coming up.
 * 
 * Version info:
 * A first version is created to solve that we would like to generate several service interactions without to much manual work.
 *
 * TODO:
 * 
 */

def getAllFilesMatching(direcory, pattern){
	def filesFound = []
	direcory?.traverse(type:FileType.FILES, nameFilter: ~pattern){ fileFound -> filesFound << fileFound }
	filesFound.each { fileFound -> println "File to process: ${fileFound.name}" }
	return filesFound
}

def getAllDirectoriesMatching(direcory, pattern){
	def dirsFound = []
	direcory?.traverse(type:FileType.DIRECTORIES, nameFilter: ~pattern){ dirFound -> dirsFound << dirFound }
	dirsFound.each { dirFound -> println "Directory to process: ${dirFound}" }
	return dirsFound
}

def getAllUniqueRivNameSpaces(wsdlFile){
	def rivNameSpace = 'No namespace found'

	new SAXReader().read(wsdlFile).getRootElement().declaredNamespaces().grep(~/.*urn:riv.*/).each{ namespace ->
		if(namespace.text.contains('rivtabp')){
			rivNameSpace = namespace.text
		}
	}
	println rivNameSpace
	return rivNameSpace
}

def buildVirtualServices(serviceInteractionDirectories, targetDir){

	serviceInteractionDirectories.each { serviceInteractionDirectory ->

		def (name, schemaDir) = serviceInteractionDirectory.parent.split("schemas")	
		def artifactId = serviceInteractionDirectory.name - 'Interaction'
		def schemasFiles = getAllFilesMatching(serviceInteractionDirectory, /.*\.wsdl/)
		
		def serviceNameSpace = getAllUniqueRivNameSpaces(schemasFiles[0])
		def serviceNameSpaceArray = serviceNameSpace.split("\\:")
		
		def namespacePrefix = serviceNameSpaceArray[0] + ":" + serviceNameSpaceArray[1]
		def maindomain = serviceNameSpaceArray[2]
		def subdomain =  serviceNameSpaceArray[3] 
		def serviceName = serviceNameSpaceArray[4]
		def serviceVersion = serviceNameSpaceArray[5]
		def rivtaVersion = serviceNameSpaceArray[6]
		
		def wsdlFileName = schemasFiles[0].name
	
		def version = '2.0'
		
		def mvnCommand = """mvn archetype:generate 
		-DinteractiveMode=false 
		-DarchetypeArtifactId=service-archetype 
		-DarchetypeGroupId=se.skl.tp.archetype 
		-DarchetypeVersion=1.0 
		-Duser.dir=${targetDir} 
		-DgroupId=se.skl.skltpservices.${maindomain}.${subdomain}
		-DartifactId=${artifactId} 
		-Dversion=${version}
		-DdomainName=${maindomain} 
		-DdomainSubName=${subdomain} 
		-DserviceMethod=${artifactId} 
		-DserviceWsdlFileDir=classpath:/schemas$schemaDir/${artifactId}Interaction/${wsdlFileName}
		-DserviceInteraction=${artifactId}Interaction  
		-DserviceRelativePath=${artifactId}/${serviceVersion}/${rivtaVersion} 
		-DserviceNamespace=${serviceNameSpace}  
		"""
		println "$mvnCommand"
		
		def process = mvnCommand.execute()
		process.waitFor()
		
		// Obtain status and output
		println "RETURN CODE: ${ process.exitValue()}"
		println "STDOUT: ${process.in.text}"
	}
}

def copyServiceSchemas(serviceInteractionDirectories, targetDir){
	serviceInteractionDirectories.each { serviceInteractionDirectory ->
		def schemasFiles = getAllFilesMatching(serviceInteractionDirectory, /.*\.xsd|.*\.xml|.*\.wsdl/)
		
		def (name, parentDir) = serviceInteractionDirectory.parent.split("schemas")		
		def serviceInteraction = serviceInteractionDirectory.name
		def serviceDirectory = serviceInteraction - 'Interaction'
		def schemaTargetDir = "${targetDir}/${serviceDirectory}/Virtualisering/src/main/resources/schemas/$parentDir/${serviceInteraction}"
		new File("${schemaTargetDir}").mkdirs()
		
		schemasFiles.each {sourceSchemaFile -> 
			def targetSchemaFile = new File("${schemaTargetDir}/$sourceSchemaFile.name")
			FileUtils.copyFile(sourceSchemaFile, targetSchemaFile)}
	}
}

def copyCoreSchemas(serviceInteractionDirectories, coreSchemaDirectory, targetDir){
	serviceInteractionDirectories.each { serviceInteractionDirectory ->
		def schemasFiles = getAllFilesMatching(coreSchemaDirectory, /.*\.xsd/)
		
		def serviceInteraction = serviceInteractionDirectory.name
		def serviceDirectory = serviceInteraction - 'Interaction'
		def coreSchemaTargetDir = "${targetDir}/${serviceDirectory}/Virtualisering/src/main/resources/schemas/core_components"
		new File("${coreSchemaTargetDir}").mkdirs()
		
		schemasFiles.each {sourceSchemaFile -> 
			def targetSchemaFile = new File("${coreSchemaTargetDir}/$sourceSchemaFile.name")
			FileUtils.copyFile(sourceSchemaFile, targetSchemaFile)}
		
	}
}

if( args.size() < 1){
	println "This tool generates service virtualising components based on service interactions found in sourceDir. They are generated in the dir where script is executed."
	println "Point sourceDir to the schemas dir containing:"
	println "core_components"
	println "interactions"
	println ""
	println "To be able to run this tool you need to have the service-archetype installed, found under tools/generators/archetypes."
	println ""
	println "Required parameters: source directory [sourceDir] \n"
	println "PARAMETERS DESCRIPTION:"
	println "[sourceDir] is the base direcory where this script will start working to look for servivce interactions, e.g /repository/rivta/ServiceInteractions/riv/crm/scheduling/trunk "
	println ""
	println "OUTPUT:"
	println "New maven folders containing service interactions"
	return
}

def sourceDir = new File(args[0])
def targetDir = "."

new File("${targetDir}/pom.xml") << new File("pomtemplate.xml").asWritable()

def serviceInteractionDirectories = getAllDirectoriesMatching(sourceDir,/.*Interaction$/)
def coreSchemaDirectory = getAllDirectoriesMatching(sourceDir,/core_components/)[0]

buildVirtualServices(serviceInteractionDirectories, targetDir)
copyServiceSchemas(serviceInteractionDirectories, targetDir)
copyCoreSchemas(serviceInteractionDirectories, coreSchemaDirectory, targetDir)

println ""
println ""
println "NOTE! Run mvn clean install to build deployable jar-files for service platform"