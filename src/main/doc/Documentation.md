# PrivAS

## Overview

## Client

## Reference Panel Provider

## Third Party Server

### Configuration
The Third Party Server must provide at least 2 scripts :
* one to get a public RSA Key for a Session
* one to launch a series of Association Tests

#### GetPublicRSAKey

**Example For Simple Unix base System:**

```bash
#!/bin/bash

if [ $# -lt 1 ]
then
        echo -e "usage :\t$0 SessionID";
        exit 1;
fi

installDir=/path/to/PrivAS/WorkingDirectory
jar=/path/to/PrivAS.TPS.VERSION.jar

java -jar $jar --keygen $installDir/sessions $1
```

#### LaunchAssociationTest 

**Example For Simple Unix base System:**

```bash
#!/bin/bash

if [ $# -lt 1 ]
then
        echo -e "usage :\t$0 SessionID";
        exit 1;
fi

session=$1
installDir=/path/to/PrivAS/WorkingDirectory
jar=/path/to/PrivAS.TPS.VERSION.jar
core=24
seed="random" #random seed for Production
#seed="123456789" #fixed seed for Debuging
d=`date +"%Y-%m-%d"`;
log=$installDir/log/$d.log
err=$installDir/log/$d.err

java -jar $jar "$session" "$installDir" "$core" "$seed" &>> $log

```