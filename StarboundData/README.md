#Starbound Data Types Library
This library is use to read Starbound Data and Packet types from Netty.io's ByteBuf class.

Authors: Underbalanced
Contributes:

NOTE: This code has been refined and is ready for public consumption

Requirements
============
- Java 8 (32 or 64 bit version)

Current Data Types
========
- Colors:
	- GameColors - All the colors in the game with look up methods
	- Color - Holds details about a color by NAME, hex and shortcut

- Tiles:
	- TileDamage - Contains damage type, amount and harvest level

- Variants:
	- Variant - This data type can be composed of the following (String, Double, Boolean, Byte, Variant Array, Map)
    - Variant Length Quantity (VLQ) - This varies in length up to 10 bytes and provides a Integer representing the value (See References)

- Vectors:
	- Vec2F - Floating Point (x,y) - 2 Points making up a precise coordinate
	- Vec2I - Integer (x,y) - 2 Points making up a coordinate

- Vector ArrayList:
	- Vec2IArray - Holds up to 100 Vec2I's. Anything sent over 100 (10 Radius) will be discarded

Current Packet Types
========
- ChatSent
- ChatReceive
- ClientConnectionResponse
- ClientDisconnectRequest
- ConnectionResponse
- Heartbeat
- ServerDisconnect
- ProtocolVersion
- UniverseTimeUpdate
- DamageTileGroup
- PassThrough

Misc
========
- Packets - List all packets with methods to manipulate them
- Packet - Abstract Object that all packets must inherite
- StarboundBufferRead - Static methods to read from a Netty.io ByteBuf
- StarboundBufferWriter - Static methods to write to a Netty.io ByteBuf
	
Installation
============
- Coming soon maven "pom.xml"...
- Coming soon adding a library wiki...

References
============
- [Netty Network Library](Netty.io)
- [Variant Length Quantity Wiki](http://en.wikipedia.org/wiki/Variable-length_quantity).
- [Variant Length Quantity Rosetta Code](http://rosettacode.org/wiki/Variable-length_quantity).
