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
#         {'TesticalPlugin': [1.0, 0.1]},
#         {'Dufus': [2.0, 3.0, 5.0]}
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
