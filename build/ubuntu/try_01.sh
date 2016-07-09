git clone git://git.apache.org/hadoop.git


cat hadoop/BUILDING.txt

# remove Open JDK...
sudo apt-get purge openjdk-\*

# ... and get Oracle's JDK
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java9-installer
#  Remove again with
#  sudo apt-get purge oracle-java9-installer

sudo apt-get install docker.io
#? systemctl start docker.service
sudo usermod -aG docker $USER
# Login again to reflect new membership in group docker.
su - $USER
#
# TODO is bios virtualization enabled. Seems to be necessary for docker
# TODO is
#   sudo service docker restart
# necessary in Ubuntu?


cd hadoop/
./start-build-env.sh

sudo chown -R $USER /home/$USER/.m2

mvn compile
