class site_hostname {

    file{ '/usr/local/bin/sethostname.sh':
        source  => "puppet:///modules/site_hostname/sethostname.sh",
        owner   => root,
        mode    => 755,
    }

    exec { "change hostname":
        command             => "/usr/local/bin/sethostname.sh",
        user                => root,
        onlyif              => "echo '! grep `hostname` /etc/hosts' | bash",
        require             => File['/usr/local/bin/sethostname.sh'],
    }

}