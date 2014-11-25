#Utilities Library
This library is house common code used for generic functionality
Authors: Underbalanced
Contributes:

NOTE: This code has been refined and is ready for public consumption

Requirements
============
- Java 8 (32 or 64 bit version)

Current Utility Functions
============
- Bytes:
	- BytesInteger - Converts Integers to and from byte[]'s

- Cache:
	- CacheWrapper - Holds Cache Objects that extend TimeCache from this library
	- BooleanCache - Holds the System Time and a boolean value
	- StringCache - Holds the System Time and a String value
	- TimeCache - Simple utilities.cache that holds the System Time from when it was constructed

- Date and Times:
	- DateAndTimes - Simple Date formatter and time calculator

- Operating System:
	- OperatingSystem - contains the operating system type (windows or linux)
	- BitVersion - is an abstract class that belongs to Operating System
		- LinuxBitVersion - contains methods to obtain linux bit versions

- Threading:
	- NamedThreadFactory - This creates a named thread factory that names Threads as such (Name + " - " + increment)
	- ThreadSleep - Provides methods for you to sleep a thread in seconds or milliseconds

- YAML:  **Note**: "!!sets", are unable to be checked in a YAML parser and look like key, value mappings but, are not actually (k,v).
	- YAMLWrapper - This wraps a Map called Data that represents the YAML File from disk, and has methods to add, remove, check, get values and even collections methods
		- YAMLFile - This holds the YAMLDumper as well as the details about the YAML file. Files can be loaded from disk, but must have a default as well. Default can be in (Jar or a Map provided at construction)
			- YAMLDumper - This holds the YAMLAutoDumper as well as the boolean value for AUTO_DUMP_ON_MODIFICATION which saves the file on data changes
				-YAMLAutoDump - This uses a TaskManager (Utilities Class) or a ScheduledThreadPoolExecutor (Java Class) to auto save file data at a interval

- Exceptions:
	- CacheWrapperOperationException - Thrown by CacheWrapper class
	- CollectionDoesNotExistException - Thrown by YAMLWrapper class

Misc
============


To Do:
============
- Add Observable and Supplier Systems so that external programs can receive specific details and use them, such as one time purges, What was purged?

	
Installation
============
- Coming soon maven "pom.xml"...
- Coming soon adding a library wiki...

References
============
- YAML:
	- [YAML Ain't Markup Language](Netty.io)
	- [Snake YAML for Java](https://code.google.com/p/snakeyaml/)
	- [Online YAML Parser - Verify Your YAML!](http://yaml-online-parser.appspot.com/)

- Joda Time:
	- [Joda Time Library](http://www.joda.org/joda-time/)
	- [Joda Time Format Reference](http://www.joda.org/joda-time/key_format.html)