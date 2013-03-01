GETTING STARTED
1. Run rw.asfki.UpdateAsfkiFilesJob
It will run parser with default configuration

CONFIGURATION
You can change parameters also with just adding vm arguments in run 
configurations like this -D[param.key]=[param.value]
Example: -Ddb2lExtention=.db2l (Output files will have .db2l extention)

Note: Vm params have highest priority for this program.

DEPENDENCY
Parser uses log4j 1.2