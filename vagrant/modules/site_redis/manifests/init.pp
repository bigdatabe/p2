class site_redis {

    package { 'redis':
        ensure  => latest,
    }

    service { 'redis':
        ensure  => running,
    }

    file { '/etc/redis.conf':
        ensure  => present,
        owner   => root,
        group   => root,
        content => template('site_redis/redis.conf.erb'),
    }
}