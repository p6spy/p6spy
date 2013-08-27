#!/usr/bin/env groovy

import groovy.xml.MarkupBuilder

// get environment variables
def usernameValue = System.getenv("SONATYPE_USERNAME")
def passwordValue = System.getenv("SONATYPE_PASSWORD")
if( usernameValue == null ) {
  System.out.println("Environment variable SONATYPE_USERNAME not set - skipping deployment")
  System.exit(-1)
}
if( passwordValue == null ) {
  System.out.println("Environment variable SONATYPE_PASSWORD not set - skipping deployment")
  System.exit(-1)
}

// load existing settings.xml file
def originalSettingsFile = new File(System.getProperty("user.home"), ".m2/settings.xml")
def settings = new XmlParser().parse(originalSettingsFile);

def servers = settings.servers
if( servers == null ) {
  // create the node if it did not exist
  servers = settings.append(new NodeBuilder().createNode("servers"))
} else {
  // if it existed, it will be a NodeList...
  servers = servers[0]
}

// append server node for snapshots
servers.append(NodeBuilder.newInstance().server {
  username(usernameValue)
  password(passwordValue)
  id('sonatype-nexus-snapshots')
})

// append server node for staging
servers.append(NodeBuilder.newInstance().server {
  username(usernameValue)
  password(passwordValue)
  id('sonatype-nexus-staging')
})

// write out new settings.xml file
def writer = new FileWriter(new File(originalSettingsFile.parentFile, 'deploySettings.xml'))
new XmlNodePrinter(new PrintWriter(writer)).print(settings)
writer.close()