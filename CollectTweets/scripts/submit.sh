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
    send "spark-submit --master yarn --conf spark.executorEnv.JAVA_HOME=/usr/lib/jvm/jre-1.8.0-openjdk/ --class twitter_classifier.Collect --jars $files '"$1"'/lib/sparksenti_2.11-0.1.jar '"$4"' > '"$5"'\r"
    expect "$ "
    close
'
