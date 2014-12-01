chatsystem-corba
================

Centralized server provides chat room services to clients.

##Build

Open a terminal on server machine:

    cd git/chatsystem-corba/
    javac *.java classes/*.java classes/ChatServerPackage/*.java
    idlj -fall -td classes/ ChatSystem.idl
    orbd -ORBInitialPort 1050 -ORBInitialHost localhost &
    java -classpath classes/ Server -ORBInitialPort 1050
    
On client machine:

    java -classpath .:classes/ Client -ORBInitialPort 1050
    
##Architecture

Implements `CORBA` (Common Object Request Broker Architecture) `RMI` (Remote Method Invocation) architecture consisting of a centralized server which provides chat room services to clients through the use of an `IDL` (Interface Definition Language) interface.

The server instantiates a `CORBA` object, creates a remote object reference to it and binds this reference to the name service.  Potential clients look up the name service to resolve the reference to the `CORBA` object, once this is complete the client may invoke any remote methods specified by the `CORBA` object’s `IDL` interface.

The remote methods declared in the `IDL` file specify the expected types of the input and output parameters for each method, this mechanism provides the interoperability between `CORBA` applications wrote in different languages.
