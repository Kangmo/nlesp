server_pid=`./get-server-pid.sh`
echo "The server PID is $server_pid."
kill -9 $server_pid
