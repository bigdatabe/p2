Exec { path => [ "/usr/local/bin/", "/usr/local/sbin", "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

stage { 'first':
  before => Stage['main'],
}

# -- JDK
class { 'jdk':
    stage   => 'first'
}

# -- Storm Worker
class { 'storm::config':
    nimbus_host         => '192.168.123.1',
    zookeeper_servers   => ['192.168.123.1' ],
    supervisor_slots    => [ 6700, 6701, 6702 ],
    ui_port             => 9088,
}

class { 'storm::worker': }

