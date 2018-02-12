#!/bin/bash
/usr/bin/expect -c '
    set timeout -1
    spawn ssh '"$2"'
    expect "*password*"
    send "'"$3"'\r"
    expect "$ "
    send "echo '"$1"'/lib/*.jar | tr \" \" \",\"\r"
    expect -re {.*\r\n([_a-zA-Z0-9\-\/\.,?]*)\r\n} {set files $expect_out(1,string)}
    expect "$ "
    expect "$ "
    close
'
echo