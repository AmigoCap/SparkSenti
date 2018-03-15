#!/bin/bash
/usr/bin/expect << EOD
    set timeout -1
    spawn scp $2:$1 .
    expect "*password*"
    send "$3\r"
    expect eof
EOD
echo
