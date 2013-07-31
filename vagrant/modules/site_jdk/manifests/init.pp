class site_jdk {

    $url = "http://download.oracle.com/otn-pub/java/jdk/7u25-b15/jdk-7u25-linux-x64.rpm"
    $file = "/tmp/jdk-7u25-linux-x64.rpm"
    $cookie = '"Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com"'
    package { "jdk-1.7.0_25-fcs.x86_64":
         source     => $file,
         provider   => rpm,
         require    => Exec['download jdk']
    }

    # http://getpocket.com/a/read/153528263
    exec{'download jdk':
        command     => "wget -O ${file} --no-cookies --no-check-certificate --header ${cookie} ${url}",
        user        => 'root',
        path        => '/usr/bin/',
        creates     => "${file}",
        timeout     => "360",
    }
}