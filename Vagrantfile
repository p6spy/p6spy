# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  # All Vagrant configuration is done here. The most common configuration
  # options are documented and commented below. For a complete reference,
  # please see the online documentation at vagrantup.com.

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "precise64"
  config.vm.box_url = "http://files.vagrantup.com/precise64.box"

  # increasing memory
  config.vm.provider :virtualbox do |vb|
    vb.customize ["modifyvm", :id, "--memory", "1024"]
  end

  #
  # Plugins
  #
  # make sure to install them using:
  #vagrant plugin install vagrant-omnibus
  #vagrant plugin install vagrant-berkshelf
  #vagrant plugin install vagrant-cachier

  # for managing cookbook dependencies
  config.berkshelf.enabled = true
  config.berkshelf.berksfile_path = "./Berksfile"

  # to ensure usage of the latest revision of chef
  config.omnibus.chef_version = :latest

  if Vagrant.has_plugin?("vagrant-cachier")
    # Configure cached packages to be shared between instances of the same base box.
    # More info on http://fgrehm.viewdocs.io/vagrant-cachier/usage
    config.cache.scope = :box   
    
    # use the generic cache bucket for Maven
	config.cache.enable :generic, {
 		"maven" => { cache_dir: "/home/vagrant/.m2/repository" }
	}
  end
  
  # The url from where the 'config.vm.box' box will be fetched if it
  # doesn't already exist on the user's system.
  # config.vm.box_url = "http://domain.com/path/to/above.box"

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  # config.vm.network :forwarded_port, guest: 80, host: 8080
  config.vm.network :forwarded_port, guest: 5005, host: 5005

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network :private_network, ip: "192.168.33.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network :public_network

  # If true, then any SSH connections made will enable agent forwarding.
  # Default value: false
  # config.ssh.forward_agent = true

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
  # config.vm.provider :virtualbox do |vb|
  #   # Don't boot with headless mode
  #   vb.gui = true
  #
  #   # Use VBoxManage to customize the VM. For example to change memory:
  #   vb.customize ["modifyvm", :id, "--memory", "1024"]
  # end
  #
  # View the documentation for the provider you're using for more
  # information on available options.

  # Enable provisioning with Puppet stand alone.  Puppet manifests
  # are contained in a directory path relative to this Vagrantfile.
  # You will need to create the manifests directory and a manifest in
  # the file precise64.pp in the manifests_path directory.
  #
  # An example Puppet manifest to provision the message of the day:
  #
  # # group { "puppet":
  # #   ensure => "present",
  # # }
  # #
  # # File { owner => 0, group => 0, mode => 0644 }
  # #
  # # file { '/etc/motd':
  # #   content => "Welcome to your Vagrant-built virtual machine!
  # #               Managed by Puppet.\n"
  # # }
  #
  # config.vm.provision :puppet do |puppet|
  #   puppet.manifests_path = "manifests"
  #   puppet.manifest_file  = "init.pp"
  # end

  # Enable provisioning with chef solo, specifying a cookbooks path, roles
  # path, and data_bags path (all relative to this Vagrantfile), and adding
  # some recipes and/or roles.
  #
  # config.vm.provision :chef_solo do |chef|
  #   chef.cookbooks_path = "../my-recipes/cookbooks"
  #   chef.roles_path = "../my-recipes/roles"
  #   chef.data_bags_path = "../my-recipes/data_bags"
  #   chef.add_recipe "mysql"
  #   chef.add_role "web"
  #
  #   # You may also specify custom JSON attributes:
  #   chef.json = { :mysql_password => "foo" }
  # end


  config.vm.provision "chef_solo" do |chef|
	  chef.add_recipe "java"
	  chef.add_recipe "maven"
	  chef.add_recipe "postgresql::server"
	  chef.add_recipe "mysql::server"
	  chef.add_recipe "sqlite"
	  chef.add_recipe "groovy"

	  chef.json = {

		  # java
		  "java" => {
		  "install_flavor" => "oracle",
		  # "install_flavor" => "openjdk",
		  "jdk_version" => "7",
		  "oracle" => { "accept_oracle_download_terms" => "true" },
		  "accept_license_agreement" => "true",
	  },
	  # maven
	  "maven" => {
		  "version" => "3",
		  "3" => {
		  "url" => "http://apache.openmirror.de/maven/maven-3/3.1.1/binaries/apache-maven-3.1.1-bin.tar.gz",
		  "checksum" => "077ed466455991d5abb4748a1d022e2d2a54dc4d557c723ecbacdc857c61d51b" }
	  },
		  # PostgreSQL
		  "postgresql" => {
		  "version" => "9.2",
		  "enable_pgdg_apt" => "true",
		  "password" => { "postgres" => "123" },
		  # otherwise certificate error on restart
		  config: { 
		  "ssl" => "false"
	  },
		  pg_hba: [  
			  { type: 'local', db: 'all', user: 'all', addr: '', method: 'trust' },
			  { type: 'host', db: 'all', user: 'all', addr: '127.0.0.1/32', method: 'trust' },
			  { type: 'host', db: 'all', user: 'all', addr: '::1/128 ', method: 'trust' }
	  ]
	  },

		  # MySQL
		  "mysql" => { 
		  "server_root_password" => "123", 
		  "server_repl_password" => "123",
		  "server_debian_password" => "123" 
	  },
		  # groovy
		  "groovy" => {
        "version"  => "2.1.9",
        "url"      => "https://dl.bintray.com/groovy/maven/groovy-binary-2.1.9.zip",
        "checksum" => "d9cb8d54680d508ac1eb928f8d0cfb1fb1bec7843bb405ea9a7d18f512b10ba6"
      }
	  }
  end

  config.vm.provision "shell", path: "script/vagrant/script.sh"


  # Enable provisioning with chef server, specifying the chef server URL,
  # and the path to the validation key (relative to this Vagrantfile).
  #
  # The Opscode Platform uses HTTPS. Substitute your organization for
  # ORGNAME in the URL and validation key.
  #
  # If you have your own Chef Server, use the appropriate URL, which may be
  # HTTP instead of HTTPS depending on your configuration. Also change the
  # validation key to validation.pem.
  #
  # config.vm.provision :chef_client do |chef|
  #   chef.chef_server_url = "https://api.opscode.com/organizations/ORGNAME"
  #   chef.validation_key_path = "ORGNAME-validator.pem"
  # end
  #
  # If you're using the Opscode platform, your validator client is
  # ORGNAME-validator, replacing ORGNAME with your organization name.
  #
  # If you have your own Chef Server, the default validation client name is
  # chef-validator, unless you changed the configuration.
  #
  #   chef.validation_client_name = "ORGNAME-validator"
end
