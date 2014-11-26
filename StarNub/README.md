OUTDATED - Update coming soon


-Incomplete as of 19 August 2014


StarNub was created to enhance Starbound

Wiki - https://github.com/StarNub/StarNub/wiki
API - http://docs.starnub.org/StarNubApi/

Authors: Underbalanced

Contributors:

NOTE: This code is not final and will be modified for efficiency and clean up.

Requirements
============
- Java 8 (32 or 64 bit version)
- Any Windows or Linux OS

Current Features
========
Base Features:
   - Easy Install
   - StarNub Configuration:
		- Step by step setup, or extract a utilities.file and edit
		- Configuration self heals data types
		- Most configuration items can be modified while the starbounddata.packets.starbounddata.packets.server is running
   - Starbound Configuration:
		- Auto generate configuration if it does not exist in directory
		*- Auto write configuration variables when changed in StarNub configuration
   - Highly configurable logging to screen and utilities.file
		- Log rotation every new day and if specified utilities.file size of your choice is met a new utilities.file will be created
		- Can filter, Debug, Events, Commands, Chat, Information, Warning (Error and Fatal can not be turned off)
   - SQLite or MySQL with support for other databases (Player data storage)
		- Data tracked:
			- Player Sessions (Characters, start and end time, attached account)
			- Characters (Cleaned NAME & uuid)
			- Characters and the IPs seen for each one
			- Accounts, account settings, starbounddata.packets.chat rooms, achievements are just some
			- Groups, group ladders and group permissions
   - Highly scalable:
		- Network and Event system well scale to hardware
   *- Built in DOS and DDOS detection attempts for sockets and starbounddata.packets.connection spamming
		- Auto clean up of left open sockets
		*- Auto internal ip banning on DOS or DDOS
		*- Connection rate limiter in case of ddos detected
   - Unique account system:
		- Characters that join a starbounddata.packets.starbounddata.packets.server can create an account or attach a character to an account this enables them to keep personal settings, groups, permissions and centralize all the information about them from each character into one spot
   - 3 Tier permissions system {base}.{sub}.{permission}. 
		- You can use permissions to allow command usage, block breaking size, starbounddata.packets.chat speed are just a few uses of permissions
		- Accounts and Groups can have permissions. If a group inherited another group, the permissions are thus inherited as well and the player assumse each of the permissions
   - Chat:
		- Spam Protections:
			- Configurable: 
				- Global starbounddata.packets.chat rate limit (Prevents spamming, is configurable to the milliseconds)
					*- Permission per user for fine grain rate limiting
			- Auto lower caseing of mostly capitalized sentences
			*- Block repeat messages or messages that are almost the same
			*- Word filter
			- Spam reply message
		- Server NAME and starbounddata.packets.chat - color options
		- Global NAME and starbounddata.packets.chat - color option
		*- Player prefix and suffix selectable by the player based on groups and achievements they have
		- Customizable tage start and end example..."[", "]".
		- Chat Rooms: 
			- Universe starbounddata.packets.chat is a starbounddata.packets.chat room that can be left
			- Chat Rooms subscriptions are saved so when logging in you rejoin any rooms from before
			- Chat rooms that are private can be password protected, and the starbounddata.packets.chat room will appear in the list butthe inhabitants are secret
			- Players can set a default starbounddata.packets.chat room so when they use starbounddata.packets.chat it talks to that room only, they would have to use /starbounddata.packets.chat universe hey! in order to talk back to universe if they set the starbounddata.packets.chat room to another default
		- Players can be muted
		- Players can be ignored by other players both players will not see each others starbounddata.packets.chat or be able to whisper each other
		- Allow colored nick names or starbounddata.packets.chat by players
		*- Tokens for information an colors instead of ^blue; or ^#0000CC; you could use {bl} for online {online}
		- Illegal names and nick names are automatically changed when a player joins and sent to the starbounddata.packets.starbounddata.packets.server
		- PVP messages are no longer starbounddata.packets.global but local
		- Players can be whispered by any identifier, Starbound id(Changed per login), StarnubID, character NAME colored or not, nick NAME, ip, uuid
			- Example /w 2 hey = Starbound id, /w 2s hey statnubd up, /w underbalanced hey, /w 127.0.0.1 hey who is this?
		
   - Commands: 
		- Optional: Command delivery to plugin success or failure notification
		- Parser:
			- Arguments:
				- If the command only has 2 arguments then no "-" are needed.
					- Example /w Underbalanced hey how are you (Argument 1 = Underbalanced, argument 2 = hi how are you)
				- If the command has more then 2 arguments then "-" are used to indicate a arguments
					- Example /tban -1d2h -Underbalanced -Using excessive profanity (arguments 1 = 1d2h, argument 2 = Underbalanced, aruement 3 = Using exsessive profanity)
				- Auto verify enoughargumentss are present based on the command settings
			- Checks if players can use the command or not based on the command settings
			- Checks if everyone can use the command or if the player has permissions
		- Commands are used by /{commandname or alias} {command}
			- Example /starnub online or /sn online - would return whos online
			- Commands can be mapped to shortcuts using the shortcut.yml in StarNub directory
				- Example: Config utilities.file would be starnub: ['online', 'who'], starboundmanager: ['ban', 'kick']
					- ^-this would map /starnub online or /starnub who to /online or /who and /starboundmanager ban and /starboundmanager kick to /ban and /kick
			- Only one unique command can be mapped, so if both starnub and starboundmanager both have 'ban' only one can be shortcut's
		- Players can be command blocked
					
	- Whitelisted starbounddata.packets.starbounddata.packets.server option (uuid, ip or starnub id)
	
	- Restrictions: 
		- Offline banning by uuid, ip, character NAME, starnub id
		- Online banning by uuid, ip, character NAME, starboundmanager id*preferd, starnub id* prefered
		- Can ban session uuid, ip or ban all ips ever seen with character and every uuid seen with an ip
		- Players can be permanently muted, command blocked or banned
		- Players can also be temporarily muted command blocked or banned, these are automatically removed after the time expired

	*- Achievements:
		- Can be created by utilities.file but will need a plugin to track and give
		
	- Groups:
		- Have tags, colors, permissions, inheritances
		- Can simle load or update from utilities.file, but data is stored permanently in database
		- Multiple can be assigned to player
		
	- Plugins
		- Easy StarNub API for creating plugins
		- Some plugins can be updated while starbounddata.packets.starbounddata.packets.server is running
		- If two versions of the same plugin are in the plugins director, only the newest well be loaded
		- Plugins can use StarNubs configuration class to enable self updating, healing and cleaning configuration
		- Built in event system that allows event creation and subscription
	
   - Future support	for multi lingual starbounddata.packets.starbounddata.packets.server tool messages and plugins support
   - Future additional player, plugin and starbounddata.packets.starbounddata.packets.server stats
   
Optional Essentials Plugin Features:
	- Server Monitor (Checks every 20 seconds)
		- Checks the Starbound Server process every 15 seconds. (Restarts on Server crashes)
		- Checks the Starbound Server for a response via (TCP)(Restarts on Server lockups)
		- Auto Re-starter (Configurable)

	
Planned Features
================

>>>>>>> dev_unstable_nightly
	
Installation
============
Moved to Wiki.
