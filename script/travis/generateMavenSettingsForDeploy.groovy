#!/usr/bin/env groovy

// load existing settings.xml file
def originalSettingsFile = new File(System.getProperty("user.home"), ".m2/settings.xml")
if( !originalSettingsFile.exists() ) {
  println "Could not load settings from ${originalSettingsFile.absolutePath}"
  System.exit(-1)
}
def settings = new XmlParser().parse(originalSettingsFile);
println "Maven settings loaded from ${originalSettingsFile.absolutePath}"

// get environment variables
def usernameValue = System.getenv("SONATYPE_USERNAME")
def passwordValue = System.getenv("SONATYPE_PASSWORD")

if( usernameValue != null && passwordValue != null ) {

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
} else {
  println "Environments variable SONATYPE_USERNAME or SONATYPE_PASSWORD not set"
}

// add cloudbees repositories
def profiles = settings.profiles
if( profiles.size() == 0 ) {
  // create the node if it did not exist
  settings.append(new NodeBuilder().createNode("profiles"))
  profiles = settings.profiles
}

println "Appending profile for cloudbees repositories"
profiles[0].append(NodeBuilder.newInstance().profile {
  id('cloudbees')
  repositories {
    respository {
      id('cloudbees-release')
      snapshots {
        enabled('false')
      }
      name('cloudbees-release')
      url('http://repository-p6spy.forge.cloudbees.com/release')
    }
  }
})

def activeProfiles = settings.activeProfiles
if( activeProfiles.size() == 0 ) {
  // create the node if it did not exist
  settings.append(new NodeBuilder().createNode("activeProfiles"))
  activeProfiles = settings.activeProfiles
}

activeProfiles[0].append(NodeBuilder.newInstance().activeProfile('cloudbees'))


// write out new settings.xml file
def targetFile = new File(originalSettingsFile.parentFile, 'deploySettings.xml')
println "Writing ${targetFile.absolutePath}"
def writer = new FileWriter(targetFile)
new XmlNodePrinter(new PrintWriter(writer)).print(settings)
writer.close()