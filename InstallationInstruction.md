This is instruction how to deploy the application on your localhost

# Introduction #

This application is developed as an extension of <i>Signavio Core Component</i> (<a href='http://code.google.com/p/signavio-core-components/'><a href='http://code.google.com/p/signavio-core-components/'>http://code.google.com/p/signavio-core-components/</a></a>). We create a new query plugin enabling a process designer to gain and reuse a knowledge of other existing business processes which are similar to the designing process.

# Details #

Since our application is based on <i>Signavio Core Component</i>, the preliminary installation is referred to the installation of Signavio Core Platform. You can find it at (<a href='http://code.google.com/p/signavio-core-components/wiki/InstallationInstructions'><a href='http://code.google.com/p/signavio-core-components/wiki/InstallationInstructions'>http://code.google.com/p/signavio-core-components/wiki/InstallationInstructions</a></a>.

However, what you need to do after you successfully deployed an application is to:

1. create your own database (here we use MySQL). <br />
2. Modify database setting in business-process-query/platform/src/com/signavio/warehouse/query/gateway/util/Configuration.java <br />
3. create 4 tables by running SQL scripts which are in folder business-process-query/platform/SQL scrips/ <br />

Here we go! that's it. Now you can enjoy using our business process query.