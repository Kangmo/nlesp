tar cvfz esp.tar.gz bin schema libs-hbase-0.92.1 *.sh
#scp esp.tar.gz ladmin@192.168.0.40:ThxDev/src/Server/esp
scp esp.tar.gz ladmin@nhnsoft.com:upload
rm esp.tar.gz
