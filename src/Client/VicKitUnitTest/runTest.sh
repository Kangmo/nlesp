./reset_test.sh

# init hbase tables
#./create_tables.sh

# run unit test
./Active/VicKitUnitTest

# run scenario 1 
./runCase1.sh

# wait until the scenario A finished.
sleep 3s

# run scenario 2
./runCase2.sh
