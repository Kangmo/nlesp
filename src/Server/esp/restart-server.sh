tar xvfz esp.tar.gz
./kill-server.sh
echo "Killed the server."
mv nohup.out nohup.backup
nohup ./run-server.sh &
tail -f nohup.out

