StarNubMain
===========
Some code still needs refactoring and cleanup + documentation. This project is a highly efficient set of modules for running Starbound
servers and additionally are the foot work for remote clients such as downloadable clients and android apps. Central (Global) banning. Ect Ect.

Project Start Date: April 2014 - Invested time approximately 30-40 hours weekly to learning and coding

Documents can be found here:

[StarNub Main Documents](http://docs.starnub.org/main/)
[StarNub Plugins Documents](http://docs.starnub.org/main/)

[Twitch Stream](http://www.twitch.tv/Underbalanced/)

[StarNub](http://starnub.org)

[StarNub Repo](http://repo.starnub.org)

Freenode IRC:

'#starnub'

Pizza Fund - Paypal

admin@free-universe.com

Teamspeak 3

ts3.free-universe.com

Code contributions are welcome. I have yet to type out specific guidelines. You will probably want to chat with me.


Features:
===========
Event Driven 
	- Packet Events (Handled by player network Threads)
	- Internal Events (StarNub/Plugins) (Handled by Task Manager)

Task Manager (Short lived Runnables/Threads) API

Multi-threaded networking
	- Socket attack protections
	- Optimized memory management
	- Cached Objects

Plugin API 
	- Command API (Multiple Commands and Arguments, Auto permissions generator,
	- Default configuration generation and verification, Multiple resource extractor and loader
	- Room for Lua or Python plugins (Would needed coding but is very possible)

Database API - Out of box support 
 	- Commons Connector (Plugins) or Plugins can make their own either or both	
	- SQLite
	- MySQL

Cache API (Wrapper and Objects)
	- Many built in Cache types
	- Easy to extend

Player Session Manager
	- No logging in with the same character anymore
	- No UUID stealing
	- Session History
	- Permissions (Account or without)(Group and Account)
	- Accounts
		- Auto Login and Multiple Characters per account
		- Account Settings

	- Character History tracking (IP's)
	- Groups
		- Group Inheritances
		- Group Ladders
	- Chat Tags
	- OP's, Whitelist, Banning

Starbound Manager
	- Process Manager
		-Process Stream Manager
	- Process crash checking
	- Server Responsiveness
	
Starbound Data (Emulated) API
	- Packets
	- Data Types

Utility Library API
	- Bytes, Cache, Compression, Concurrency, Connectivity,  Crypto,  Directories, Events, Exceptions, File, Generics, Numbers, Os, Strings, Time, UUID
