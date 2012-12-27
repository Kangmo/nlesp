ps -ef | grep 'com.thxsoft.vds.Main' | grep -v grep | awk '{ print $2 }'
