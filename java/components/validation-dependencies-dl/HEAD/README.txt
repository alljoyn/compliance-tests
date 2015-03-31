This project can be used instead of the validation-dependencies to install needed AllJoyn dependencies in to the local Maven repository.

This project will attempt to download and extract the various components from their release locations.

Note that some of the downloads are rather large and will take time depending on the available bandwidth.

Check that sufficient storage space is available before building this project. The total download size could exceed 1 GB (depending on the the number of components and their sizes) and additional space is needed while extracting their contents. 

Files are downloaded to the Java temporary folder. As an example, the following can be used to override the location of the temporary folder:
  mvn install -Djava.io.tmpdir=/local/mnt/workspace/tmp

If you get an error downloading because your JRE does not trust the root certificate for the allseenalliance.org sites then you can use the cacerts file in this current directory as follows:
  mvn install -Djava.io.tmpdir=/local/mnt/workspace/tmp -Djavax.net.ssl.trustStore=cacerts -Djavax.net.ssl.trustStorePassword=changeit
