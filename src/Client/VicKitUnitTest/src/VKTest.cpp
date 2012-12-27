#include "ChatScenario1.h"
#include "ChatScenario2.h"
#include "LocalPlayerTest.h"

#include "gtest/gtest.h"

int main(int argc, char **argv) {

	if (argc <= 1) {
		std::cout << "Running basic unit test.\n";

		VicKitSystem::initialize("LocalPlayerTest");

		::testing::InitGoogleTest(&argc, argv);

		int ret = RUN_ALL_TESTS();

		VicKitSystem::destroy();

		return ret;
	}

	if (strcmp(argv[1], "1A")==0) {
		std::cout << "Running scenario 1A.\n";
		ChatScenario1A scenarioA1;

		scenarioA1.run();
	} else if (strcmp(argv[1], "1B")==0) {
		std::cout << "Running scenario 1B.\n";

		ChatScenario1B scenario1B;
		scenario1B.run();
	} else if (strcmp(argv[1], "2A")==0) {
		std::cout << "Running scenario 2A.\n";

		ChatScenario2A scenario2A;
		scenario2A.run();
	} else if (strcmp(argv[1], "2B")==0) {
		std::cout << "Running scenario 2B.\n";

		ChatScenario2B scenario2B;
		scenario2B.run();
	}
	return 0;
}



