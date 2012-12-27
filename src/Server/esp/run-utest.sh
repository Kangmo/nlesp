java -cp $(echo ./libs/*.jar | tr ' ' ':'):$(echo ./libs-junit/*.jar | tr ' ' ':'):./bin org.junit.runner.JUnitCore com.thxsoft.vds_thrift_unit.VdsThriftTests
#java -cp $(echo ./libs/*.jar | tr ' ' ':'):$(echo ./libs-junit/*.jar | tr ' ' ':'):./bin com.thxsoft.vds_thrift_unit.SingleJUnitTestRunner com.thxsoft.vds_thrift_unit.VdsThriftTests#testLoadFollowerUIDs

