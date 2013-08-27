#!/usr/bin/env groovy

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
if( !originalSettingsFile.exists() ) {
  System.out.println("Could not load settings from ${originalSettingsFile.absolutePath}")
  System.exit(-1)
}
def settings = new XmlParser().parse(originalSettingsFile);
System.out.println("Loaded settings.xml")

def servers = settings.servers
if( servers == null ) {
  // create the node if it did not exist
  System.out.println("No servers configured in settings.xml - adding node")
  servers = settings.append(new NodeBuilder().createNode("servers"))
} else {
  // if it existed, it will be a NodeList...
  System.out.println("Servers configured in settings.xml - appending additional server nodes")
  servers = servers[0]
}

System.out.println("Appending server node for snapshots")
// append server node for snapshots
servers.append(NodeBuilder.newInstance().server {
  username(usernameValue)
  password(passwordValue)
  id('sonatype-nexus-snapshots')
})

System.out.println("Appending server node for staging")
// append server node for staging
servers.append(NodeBuilder.newInstance().server {
  username(usernameValue)
  password(passwordValue)
  id('sonatype-nexus-staging')
})

def targetFile = new File(originalSettingsFile.parentFile, 'deploySettings.xml')
System.out.println("Writing ${targetFile.absolutePath}")
// write out new settings.xml file
def writer = new FileWriter(targetFile)
new XmlNodePrinter(new PrintWriter(writer)).print(settings)
writer.close()