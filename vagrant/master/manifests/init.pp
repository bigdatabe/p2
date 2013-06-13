Exec { path => [ "/usr/local/bin/", "/usr/local/sbin", "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

stage { 'first':
  before => Stage['main'],
}

# -- JDK
class { 'jdk':
    stage   => 'first'
}

# -- Storm Nimbus
class { 'storm::config':
    nimbus_host         => 'master.p2.bigdata.be',
    zookeeper_servers   => ['master.p2.bigdata.be' ],
    supervisor_slots    => [ 6700, 6701, 6702 ],
    ui_port             => 9088,
}

class { 'storm::nimbus': }
class { 'storm::ui': }


# -- Zookeeper
$zookeeper_hosts = {
    "master.p2.bigdata.be" => 1,
}

class { 'cdh4::rpm_source' :
    stage   => first
}
class { 'cdh4::zookeeper' : }
class { "cdh4::zookeeper::config":
    zookeeper_hosts => $zookeeper_hosts,
}
class { 'cdh4::zookeeper::server' : }
class { 'cdh4::zookeeper::log_cleanup' : }