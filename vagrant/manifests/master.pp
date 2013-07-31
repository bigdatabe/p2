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
