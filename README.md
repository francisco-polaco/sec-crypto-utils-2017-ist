# Java-Crypto-Utils

Well some families asked for some good functions to use Java Crypto in their project.
And in record time, we delivered.

## Compilation and Installation
The easy way to do this is by using maven and simply running the below command.
``` 
mvn install
```
Now you can edit your project's POM and add the following dependency:
``` 
<groupId>pt.ulisboa.tecnico.meic.sec</groupId>
<artifactId>crypto</artifactId>
<version>1.0-SNAPSHOT</version>
```
You are now ready to call some of the best methods out there :D

## Usage
You only need to instantiate an object of the class CryptoManager.
``` 
CryptoManager cryptoManager = new CryptoManager();
``` 
After instantiate, you can call all the methods shown below on the object.<br>
Also, some utility functions are available statically at CryptoUtilities class.

## Reference
### CryptoManager Class


### CryptoUtilities Class
Note that all methods below are static.

| Method     | What it does | Parameters | Returns|
| ------------- |---------------------|------|--------|
| getPrivateKeyFromKeystore | Retrieves the PrivateKey from the given KeyStore | (KeyStore keystore, String keyAlias, char[] keyPassword) | PrivateKey|
| getPublicKeyFromKeystore | Retrieves the PublicKey from the given KeyStore | (KeyStore keystore, String keyAlias, char[] keyPassword) | PublicKey|
| readKeystoreFile | Loads into memory a KeyStore that is written to a file | (String keyStoreFilePath, char[] keyStorePassword) | KeyStore|
| readAESKey | Loads into memory an AES key that is written to a file | (String keyPath) | Key |
| writeAESKey | Writes into disk an AES key | (String keyPath, Key symmetricKey) | void |


## Acknowledgments
I thank my pet Nicky that didn't allow me to have a full hour of work on this library.