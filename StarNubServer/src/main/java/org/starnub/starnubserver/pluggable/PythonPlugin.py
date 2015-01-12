# '''
# PLUGIN INFORMATION IS REQUIRED FOR PROPER PYTHON PLUGIN LOADING
#
# '''
#
# pluggable_info = {
#     'name': 'PythonPlugin', # String
#     'class': 'MyPlugin',
#     'version': 1.0, # Double
#     'author': 'Underbalanced',
#     'URL': 'http://www.starnub.org',
#     'has_configuration': False,
#     'load_first': ['TestValue'], # List of plugin names to load first but are not dependencies
#     'dependencies':[
#         'TesticalPlugin',
#         'Dufus'
#     ], # List of maps, Map is plugin name with a List of versions that can be used with this plugin
#     'additional_permissions':['test_plugin.can.chat', 'test_plugin.can.walk'], # These permissions are not loaded but for reference for the plugin user
#     'description': 'This is a test Python Plugin!' # Shot Description about your plugin
# }
# # THE DEFAULT CONFIGURATION SHOULD NOT BE EDITED A USER CONFIGURATION FILE WILL BE PRESENTED ON DISK FOR EDITING
# default_configuration = {}
#
#
# from org.starnub.starnubserver.pluggable.plugins import PyPlugin
#
# class MyPlugin(PyPlugin):
#     def __init__(self):
#         self.x = 6
#         self.y = 7
#
#
#     def onEnable(self):
#
#
#     def onDisble(self):
#
#
# pluggable_info = {
#     'name': 'PythonCommand', # String name that is used to call command
#     'class': 'Ban',
#     'command': 'mute',
#     'main_args':['info', 'add', 'addoffline', 'remove', 'example'],
#     'custom_split': {
#         'mute': 2,
#         }, # This would contain a map of command:int value, the value is how m
#     'can_use': 2, # 0 - Player, 1 - Remote Client, 2 - Both (Remote clients would be something like a phone or web app)
#     'version': 1.0, # Double
#     'author': 'Underbalanced',
#     'URL': 'http://www.starnub.org',
#     'description': 'This is a test Python Plugin!' # Shot Description about your plugin
# }
#
# from org.starnub.starnubserver.pluggable.command import PyCommand
#
# class Ban(PyCommand):
#     def __init__(self):
#
#
#
#     def onCommand(self, PlayerSession playerSession, String command, String[] main_args, int argsCount):
pluggable_info = {
    'name': 'StarNub',
    'class': 'Uptime',
    'command': 'uptime',
    'main_args':['starbound', 'starnub'],
    'custom_split': {},
    'can_use': 2,
    'version': 1.0,
    'author': 'Underbalanced',
    'URL': 'http://www.starnub.org',
    'description': 'This will will provide the uptime of StarNub or Starbound server(s)!'
}

from org.starnub.starnubserver.pluggable.command import Command

STARBOUND_UPTIME_EVENT;
STARNUB_UPTIME_EVENT;
starboundUptime = 0L;
starnubUptime = 0L;


class Uptime(Command):
    def __init__(self):
        this.STARBOUND_UPTIME_EVENT = new StarNubEventSubscription("Essentials", Priority.LOW, "Starbound_Uptime", new StarNubEventHandler() {
            public void onEvent(ObjectEvent objectEvent) {
                starboundUptime = (long) objectEvent.getEVENT_DATA();
            }
        });
        this.STARNUB_UPTIME_EVENT = new StarNubEventSubscription("Essentials", Priority.LOW, "StarNub_Uptime", new StarNubEventHandler() {
            public void onEvent(ObjectEvent objectEvent) {
                starnubUptime = (long) objectEvent.getEVENT_DATA();
            }
        });


    def onCommand(self, playersession, command, main_args, argscount):
        if argscount == 0
            playersession.sendBroadcastMessageToClient("Essentials", "You did not supply enough arguments. /uptime {starbound} or /uptime starnub}");
        else
            arg = args[0]
            uptime = 0
            name = ''
            if arg.equals('starbound')
                uptime = starboundUptime
                name = 'Starbound Server'
            else arg.equals('starnub')
            uptime = starnubUptime
            name = 'StarNub Server'
        formattedtime = DateAndTimes.getPeriodFormattedFromMilliseconds(uptime, False);
        servername = StarNub.getConfiguration().getNestedValue("starnub_info", "server_name");
        playersession.sendBroadcastMessageToClient(servername, "The current uptime of the " + name + " is " + formattedtime + ".");
