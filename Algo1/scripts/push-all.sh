#!/bin/bash
/usr/bin/expect << EOD
    set timeout -1
    spawn scp $1 "SentiWordNet.txt" $2:.
    expect "*password*"
    send "$3\r"
    expect eof
    spawn ssh $2
    expect "*password*"
    send "$3\r"
    expect "$ "
    send "tar xzvf SparkSenti-0.1.tar.gz\r"
    expect "$ "
    close
EOD

echo
