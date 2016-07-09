# http://kiwenlau.blogspot.ch/2015/05/steps-to-compile-64-bit-hadoop-230.html
# http://www.csrdu.org/nauman/2014/01/23/geting-started-with-hadoop-2-2-0-building/
# http://cleverowl.uk/2015/06/30/compiling-2-7-0-on-64-bit-linux/

#? rm -r ~/.m2

sudo apt-get install -y  libprotobuf-dev protobuf-compiler maven cmake build-essential pkg-config libssl-dev zlib1g-dev llvm automake autoconf make
# llvm-gcc instead of llvm?


( # Build protobuf
wget https://protobuf.googlecode.com/files/protobuf-2.5.0.tar.gz
tar xvzf protobuf-2.5.0.tar.gz
cd protobuf-2.5.0
./configure --prefix=/usr
make
sudo make install
protoc --version


# TODO When using JDK 1.9: change 1.5 to 1.6 in
#    protobuf-2.5.0/java/pom.xml
# in the following part
#
#       <artifactId>maven-compiler-plugin</artifactId>
#       <configuration>
#         <source>1.5</source>
#         <target>1.5</target>
#       </configuration>
#     </plugin>

cd java
mvn install
mvn package
)

HADOOP_RELEASE=2.7.2
wget http://www-eu.apache.org/dist/hadoop/common/hadoop-$HADOOP_RELEASE/hadoop-$HADOOP_RELEASE-src.tar.gz 
tar xvzf hadoop-$HADOOP_RELEASE-src.tar.gz 
cd hadoop-$HADOOP_RELEASE-src

mvn package -Pdist,native -DskipTests -Dtar

#TODO cp hadoop-dist/target/hadoop-2.7.2 $DEST_DIR
