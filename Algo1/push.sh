#!/bin/bash

/usr/bin/expect << EOD
    set timeout 60
    spawn scp $1 $2:.
    expect "*password*"
    send "$3\r"
    expect eof
EOD
