#!/bin/bash

/usr/bin/expect << EOD
    set timeout 60
    spawn ssh $2
    expect "*password*"
    send "$3\r"
    expect "$ "
    send "spark-submit --verbose --master yarn --class algo1_worksheet.test --jars $1/lib/*.jar $1/lib/sparksenti_2.11-0.1.jar\r"
    expect "$ "
    close
EOD

echo
