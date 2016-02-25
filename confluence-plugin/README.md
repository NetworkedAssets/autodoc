# <a href="http://condoc.networkedassets.com/"><img src="https://github.com/networkedassets/doc/src/main/resources/images/pluginLogo.png" height="48" title="Documentation from Code"/></a>Documentation from Code Plugin <a href="http://www.networkedassets.com/"><img style="float: right;" src="http://www.networkedassets.com/wordpress/wp-content/uploads/2013/03/NA_logo_header.png" height="48"></a>

Plugin for Confluence - Team Collaboration Software from Atlassian created by [NetworkedAssets](http://www.networkedassets.com/). For more detailed information, please have a look onto [Documentation from Code Plugin](http://condoc.networkedassets.com/).

## Installation

Download following **resources**:
- [The Transformer java project](https://github.com/networkedassets/transformer)
- Stash and/or Bitbucket Stash PostReceive Hook Plugin
- [Streams Third Party Provider Plugin](https://maven.atlassian.com/content/repositories/atlassian-public/com/atlassian/streams/streams-thirdparty-plugin/5.4.1/)

-------

We prepared the [video guide](https://www.youtube.com/watch?v=lwHNeXfJMUI) to help you with installation process.
However, if you don't wan't to watch our video for some reasons, you can follow step-by-step guide below.

-------
### Confluence plugin installation
1. Login to confluence as administrator, click the cog icon in upper-right corner and select Add-ons.
2. Click Upload add-on and select the streams-thirdparty-plugin .jar file that you've previously downloaded. Click Upload. 
⋅⋅⋅(If uploading takes too long time and seems not to finish at all, refresh the page and upload the file again.)
3. Success pop-up window will appear.
4. Repeat stepps 2 and 3 for the confluence-plugin .jar file.
### Bitbucket/Stash PostReceive Hook Plugin installation
1. Open Bitbucket/Stash, go to Administration and choose Manage Add-ons.
2. Upload Stash/Bitbucket plugin as a .jar file.
3. Get your Transformer up and running following [our guide](https://github.com/networkedassets/transformer/README.md).

