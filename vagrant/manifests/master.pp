Exec { path => [ "/usr/local/bin/", "/usr/local/sbin", "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

stage { 'first':
  before => Stage['main'],
}

# -- JDK
class { 'site_jdk':
    stage   => 'first'
}


# -- Zookeeper
$zookeeper_hosts = {
    "master.storm.nathan.gs" => 1,
}

class { 'cdh4::rpm_source' :
    stage   => first
}
class { 'cdh4::zookeeper' : }
class { "cdh4::zookeeper::config":
    zookeeper_hosts => $zookeeper_hosts,
}
class { 'cdh4::zookeeper::server' :
    require     => Class['site_jdk'],
}
class { 'cdh4::zookeeper::log_cleanup' : }

# -- Storm Nimbus
class { 'storm::config':
    nimbus_host         => 'master.storm.nathan.gs',
    zookeeper_servers   => ['master.storm.nathan.gs' ],
    supervisor_slots    => [ 6700, 6701, 6702 ],
    ui_port             => 9088,
}

class { 'storm::nimbus':
    require     => Class['cdh4::zookeeper::server']
}
class { 'storm::ui': }

class { 'site_redis':
    require => Package['epel-release'],
}

package { 'epel-release' :
    ensure      => installed,
    provider    => rpm,
    source      => 'http://fedora.cu.be/epel/6/i386/epel-release-6-8.noarch.rpm'
}

package { 'remi-release' :
    ensure      => installed,
    provider    => rpm,
    source      => 'http://rpms.famillecollet.com/enterprise/remi-release-6.rpm'
}

yumrepo {'epel':
    enabled => 1,
    require => Package['epel-release']
}

yumrepo {'remi':
    enabled     => 1,
    mirrorlist  => 'http://rpms.famillecollet.com/enterprise/$releasever/remi/mirror',
    descr       => 'Remi',
    gpgcheck    => 1,
    gpgkey      => 'http://rpms.famillecollet.com/RPM-GPG-KEY-remi',
    require     => Package['remi-release']
}
