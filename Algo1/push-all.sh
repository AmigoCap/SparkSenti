#!/bin/bash

/usr/bin/expect << EOD
    set timeout -1
    spawn scp $1 "SentiWordNet.txt" "tweets.txt" $2:.
    expect "*password*"
    send "$3\r"
    expect eof
    spawn ssh $2
    expect "*password*"
    send "$3\r"
    expect "$ "
    send "hdfs dfs -put tweets.txt\r"
    expect "$ "
    close
EOD
