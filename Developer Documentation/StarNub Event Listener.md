# Creating a StarNub Event Listener
Required Args:
    - Class/Data Type: (String, Priority, String, StarNubEventHandler)
    - Reference: (Plugin/CommandName, Priority, Event Key, StarNubEventHandler)


### Java:
Imports:
~~~~~~~~~~~~~{.java}
import org.starnub.starnubserver.events.starnub.StarNubEventHandler;
import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
~~~~~~~~~~~~~

Code:
~~~~~~~~~~~~~{.java}
StarNubEventSubscription starboundUptimeEvent = new StarNubEventSubscription("MyPlugin", Priority.LOW, "Starbound_Uptime", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                starboundUptime = (long) objectEvent.getEVENT_DATA();
            }
        });
~~~~~~~~~~~~~

### Python
Imports:
~~~~~~~~~~~~~{.py}
from org.starnub.starnubserver.events.starnub import StarNubEventSubscription from org.starnub.starnubserver.events.starnub import StarNubEventHandler
~~~~~~~~~~~~~
Code:
~~~~~~~~~~~~~{.py}
class StarboundUptimeHandler(StarNubEventHandler):
    def onEvent(self, objectEvent):
        starboundUptime = objectEvent.getEVENT_KEY

def __init__(self):
    StarNubEventSubscription('Essentials', Priority.LOW, 'Starbound_Uptime', StarNubUptimeHandler())
~~~~~~~~~~~~~