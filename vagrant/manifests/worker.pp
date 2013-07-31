Exec { path => [ "/usr/local/bin/", "/usr/local/sbin", "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

stage { 'first':
  before => Stage['main'],
}

# -- JDK
class { 'site_jdk':
    stage   => 'first'
}


class { 'site_hostname': }


package { "pkgconfig":
    ensure  => installed,
}

# -- Storm Worker
class { 'storm::config':
    nimbus_host         => 'master.storm.nathan.gs',
    zookeeper_servers   => ['master.storm.nathan.gs' ],
    supervisor_slots    => [ 6700, 6701, 6702 ],
    ui_port             => 9088,
}

class { 'storm::worker':
    require             => [Class['site_jdk'],Class['site_hostname']]
}




