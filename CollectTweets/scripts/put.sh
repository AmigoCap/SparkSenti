#!/bin/bash
/usr/bin/expect << EOD
    set timeout -1
    spawn scp $1 $2:.
    expect "*password*"
    send "$3\r"
    expect eof
    spawn ssh $2
    expect "*password*"
    send "$3\r"
    expect "$ "
    send "hdfs dfs -put $1\r"
    expect "$ "
    close
EOD
echo