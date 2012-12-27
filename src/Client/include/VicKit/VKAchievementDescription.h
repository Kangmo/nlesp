#ifndef __O_VK_ACHIEVEMENT_DESCRIPTION_H__
#define __O_VK_ACHIEVEMENT_DESCRIPTION_H__ (1)

#include <VicKit/Basement.h>
#include <VicKit/VKDefines.h>


class VKAchievementDescription
{
public :
	typedef std::vector<VKAchievementDescription> VKAchievementDescriptionArray ;
private :
	TxString identifier_;
	TxString groupIdentifier_;
	TxString title_;
	TxString achievedDescription_;
	TxString unachievedDescription_;
	int      maximumPoints_;
	bool     hidden_;
	bool     replayable_;
	TxImage  image_;
	static   TxImage incompleteAchievementImage_;
	static   TxImage placeholderCompletedAchievementImage_;

public :
	const TxString & identifier() const { return identifier_; };
    // The group identifier for the achievement, if one exists.
	const TxString & groupIdentifier() const { return groupIdentifier_; };
	// The title of the achievement.
	const TxString & title() const { return  title_; };
	// The description for an unachieved achievement.
	const TxString & achievedDescription() const { return  achievedDescription_; };
	// The description for an achieved achievement.
	const TxString & unachievedDescription() const { return  unachievedDescription_; };
	// Maximum points available for completing this achievement.
	const int maximumPoints() const { return  maximumPoints_; };
	// Whether or not the achievement should be listed or displayed if not yet unhidden by the game.
	const bool isHidden() const { return hidden_; };
	// Whether or not the achievement will be reported by the game when the user earns it again. This allows the achievement to be used for challenges when the recipient has previously earned it.
	const bool isReplayable() const { return replayable_; };
	// Image for completed achievement. Not valid until loadImage: has completed.
	const TxImage & image() const { return  image_; };

	// The default image for any incomplete achievement
	static const TxImage & incompleteAchievementImage() { return incompleteAchievementImage_; };

	// A placeholder image to be used for any completed achievement until the description image has loaded.
	static const TxImage & placeholderCompletedAchievementImage() { return placeholderCompletedAchievementImage_; };


	// Asynchronously load all achievement descriptions
	class LoadAchievementDescriptionsHandler {
    public :
		virtual ~LoadAchievementDescriptionsHandler() {};
        virtual void onLoadAchievementDescriptions( const VKAchievementDescriptionArray & descriptions, VKError * error ) = 0;
    };
	static void loadAchievementDescriptions(const TxString & email, const TxString & password, LoadAchievementDescriptionsHandler * handler);


	// Asynchronously load the image. Error will be nil on success.
	class LoadImageHandler {
    public :
		virtual ~LoadImageHandler() {};
        virtual void onLoadImage( const TxImage & image, VKError * error ) = 0;
    };
	void loadImage(LoadImageHandler * handler);
};

#endif /*__O_VK_ACHIEVEMENT_DESCRIPTION_H__*/
