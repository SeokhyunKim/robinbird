Robinbird
==========

## Goal: making a versatile source architecture analysis tool
Currently, it's just in very early stage.
But, now it can generate class diagram scripts automatically for PlantUML (plantuml.com).
So, robinbird and PlantUML can make class diagram like below almost automatically even for a complex source codes.
![Class diagram sample](/sample.png)

## Prerequistes
* Install PlantUML.
* PlantUML is not required to run robinbird, but it is required to generate diagram based on robinbird-created script.

## Simpe Usage
* Generate PlantUML script
  * robinbird -r root_path_of_source > sample.txt
  * PlantUML script will be printed out. So, just redirect stdout to a text file.
* Then, use PlantUML to convert the script to a diagram
  * plantuml sample.txt
  * If generated diagram is not fitted into a file, try svg format
    * plantuml -tsvg sample.txt
    * svg is scalable vector format
    
## Detailed Usage Options
* Will be updated.

 
