
#!/bin/bash

#####################################################
#
# Used to provision the Vagrant VM
#
#####################################################

wget http://dl.bintray.com/sbt/debian/sbt-0.13.5.deb
sudo dpkg -i sbt-0.13.5.deb
sudo apt-get update

sudo apt-get install -y  -o "Acquire::http::Timeout=900" openjdk-7-jdk

sudo apt-get install -y sbt
