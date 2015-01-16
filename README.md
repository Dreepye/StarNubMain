StarNubMain
===========
Some code still needs refactoring and cleanup + documentation. This project is a highly efficient set of modules for running Starbound
servers and additionally are the foot work for remote clients such as downloadable clients and android apps. Central (Global) banning. Ect Ect.

Project Start Date: 
- April 2014 - Invested time approximately 30-40 hours weekly to learning and coding

Documents can be found here:
- [StarNub](http://starnub.org)
- [StarNub Repo](http://repo.starnub.org)
- [StarNub Main Documents](http://docs.starnub.org/main/)
- [StarNub Plugins Documents](http://docs.starnub.org/main/)
- [Twitch Stream](http://www.twitch.tv/Underbalanced/)


Freenode IRC:
- '#starnub'
- '##starbound-dev'

Teamspeak 3
- ts3.free-universe.com

Pizza Fund - Paypal
admin@free-universe.com

Code contributions are welcome. I have yet to type out specific guidelines. You will probably want to chat with me.


Features:
===========
###Event Driven 
	- Packet Events (Handled by player network Threads)
	- Internal Events (StarNub/Plugins) (Handled by Task Manager)
	

###Task Manager (Short lived Runnables/Threads) API

###Multi-threaded networking
	- Socket attack protections
	- Optimized memory management
	- Cached Objects

###Pluggable API
    - Supports Java and Python
    - Commands and Plugins
    - Plugins have default configuration methods for generation and verification, Multiple resource extractor and loader
	- See Java and Python (Uptime Command and Motd Plugin) http://pastebin.com/mqzsjLJb

###Database API - Out of box support 
	- Commons Connector (Plugins) or Plugins can make their own either or both
	- SQLite
	- MySQL

###Cache API (Wrapper and Objects)
	- Many built in Cache types
	- Easy to extend

###Player Session Manager
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

###Starbound Manager
	- Process Manager
		-Process Stream Manager
	- Process crash checking
	- Server Responsiveness

###Tokens
	- Color Tokens I.E Blue {b} Can be used anywhere but must use Colors.Validate("token"); or Colors.shortcutReplacement(stringcotainingtokens);
	- Custom Tokens I.E {player-count} would return player count if using StringTokens.replaceTokens(stringcotainingtokens);
		- Can add new tokens by extending StringToken.class 

###Starbound Data (Emulated) API
	- Packets
	- Data Types

###Utility Library API
	- Bytes, Cache, Compression, Concurrency, Connectivity,  Crypto,  Directories, Events, Exceptions, File, Generics, Numbers, Os, Strings, Time, UUID
