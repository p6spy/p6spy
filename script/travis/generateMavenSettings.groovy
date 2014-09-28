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

def profiles = settings.profiles
if( profiles.size() == 0 ) {
  // create the node if it did not exist
  settings.append(new NodeBuilder().createNode("profiles"))
  profiles = settings.profiles
}

println "Appending profile for p6spy-it-mvnrepo"
profiles[0].append(NodeBuilder.newInstance().profile {
  id('p6spy-it-mvnrepo')
  activation {
    activeByDefault(true)
  }
  repositories {
    repository {
      id('p6spy-it-mvnrepo')
      snapshots {
        enabled('true')
      }
      releases {
        enabled('true')
      }
      name('p6spy-it-mvnrepo')
      url('https://github.com/p6spy/p6spy-it-mvnrepo/raw/master')
    }
  }
})

println "Appending profile for Eclipse egit"
profiles[0].append(NodeBuilder.newInstance().profile {
  id('eclipse-egit')
  activation {
    activeByDefault(true)
  }
  repositories {
    repository {
      id('eclipse-egit')
      snapshots {
        enabled('true')
      }
      releases {
        enabled('true')
      }
      name('eclipse-egit')
      url('https://repo.eclipse.org/content/repositories/egit-releases/')
    }
  }
})

// write out new settings.xml file
def targetFile = new File(originalSettingsFile.parentFile, 'p6spySettings.xml')
println "Writing ${targetFile.absolutePath}"
def writer = new FileWriter(targetFile)
new XmlNodePrinter(new PrintWriter(writer)).print(settings)
writer.close()
