# dropbox
To compile program go to dropbox folder and type:
mvn clean install

To run server go to dropbox/server/target and type:
java -jar server-1.0-SNAPSHOT-jar-with-dependencies.jar --path path_to_discs

path_to_discs is a directory containing folders imitating servers, should be at least one, names does not matter

To run client go to dropbox/client/target and type:
java -jar client-1.0-SNAPSHOT-jar-with-dependencies.jar --path path_to_client_folder --user user_name

path_to_client_folder is a path with client files to upload on server
