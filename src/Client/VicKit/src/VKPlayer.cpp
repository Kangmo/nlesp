#include <VicKit/VKPlayer.h>
#include "VKInternal.h"

TxString VKPlayerDidChangeNotificationName;


void VKPlayer::loadPlayers(const TxStringArray & identifiers, LoadPlayersHandler * handler)
{
    VK_ASSERT(handler);
}

void VKPlayer::loadPhoto(VKPhotoSize size, LoadPhotoHandler * handler)
{
    VK_ASSERT(handler);
}
