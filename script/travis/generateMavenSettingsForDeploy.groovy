#!/usr/bin/env groovy

// get environment variables
def usernameValue = System.getenv("SONATYPE_USERNAME")
def passwordValue = System.getenv("SONATYPE_PASSWORD")
if( usernameValue == null ) {
  println "Environment variable SONATYPE_USERNAME not set - skipping deployment"
  System.exit(-1)
}
if( passwordValue == null ) {
  println "Environment variable SONATYPE_PASSWORD not set - skipping deployment"
  System.exit(-1)
}

// load existing settings.xml file
def originalSettingsFile = new File(System.getProperty("user.home"), ".m2/settings.xml")
if( !originalSettingsFile.exists() ) {
  println "Could not load settings from ${originalSettingsFile.absolutePath}"
  System.exit(-1)
}
def settings = new XmlParser().parse(originalSettingsFile);
println "Maven settings loaded from ${originalSettingsFile.absolutePath}"

def servers = settings.servers
if( servers.size() == 0 ) {
  // create the node if it did not exist
  settings.append(new NodeBuilder().createNode("servers"))
  servers = settings.servers
}

// append server node for snapshots
println "Appending server node for snapshots"
servers[0].append(NodeBuilder.newInstance().server {
  username(usernameValue)
  password(passwordValue)
  id('sonatype-nexus-snapshots')
})

// append server node for staging
println "Appending server node for staging"
servers[0].append(NodeBuilder.newInstance().server {
  username(usernameValue)
  password(passwordValue)
  id('sonatype-nexus-staging')
})

// write out new settings.xml file
def targetFile = new File(originalSettingsFile.parentFile, 'deploySettings.xml')
println "Writing ${targetFile.absolutePath}"
def writer = new FileWriter(targetFile)
new XmlNodePrinter(new PrintWriter(writer)).print(settings)
writer.close()