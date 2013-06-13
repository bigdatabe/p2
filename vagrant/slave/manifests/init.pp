Exec { path => [ "/usr/local/bin/", "/usr/local/sbin", "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

# -- JDK
class { 'jdk': }

# -- Storm Worker
class { 'storm::config':
    nimbus_host         => 'master.p2.bigdata.be',
    zookeeper_servers   => ['master.p2.bigdata.be' ],
    supervisor_slots    => [ 6700, 6701, 6702 ],
    ui_port             => 9088,
}

class { 'storm::worker': }

