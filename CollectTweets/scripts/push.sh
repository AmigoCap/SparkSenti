#!/bin/bash

/usr/bin/expect << EOD
    set timeout -1
    spawn scp $1 $2:./$4
    expect "*password*"
    send "$3\r"
    expect eof
EOD
