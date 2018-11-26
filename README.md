Robinbird
==========

## Currently, major refactoring is ongoing. Just using master branch because this is not popular at all. ;D Nevertheless, if you want to run this, please contact me. During the refactoring, I'm not guranteeing this is working.


## Goal: making a versatile source architecture analysis tool
Currently, it's just in very early stage.
But, now it can generate class diagram scripts automatically for PlantUML (plantuml.com).
So, robinbird and PlantUML can make class diagram like below almost automatically even for a complex source codes.
![Class diagram sample](/sample.png)

## Prerequistes
* Install PlantUML command line tool.
  * Mac OS
    ```
    brew install plantuml
    ```
  * Other OSes
    * will be updated later
* PlantUML is not required to run robinbird,
but it is required to generate diagram based on robinbird-created script.

## How to Compile and Install
* Clone robinbird
```
git clone https://github.com/SeokhyunKim/robinbird.git
```
* robinbird is using gradle wrapper
```
./gradlew build
```
* Distribution file is generated under build/distributions. Copy to any directory you want
* set path
```
export PATH=$PATH:YOUR_INSTALL_DIRECTORY/robinbird/bin
```
* That's it!

## Simpe Usage
* Generate PlantUML script
  * robinbird -r root_path_of_source > sample.txt
  * PlantUML script will be printed out. So, just redirect stdout to a text file.
* Then, use PlantUML to convert the script to a diagram
  * plantuml sample.txt
    * sample.png will be created which has your class diagram.
  * If generated diagram is not fitted into a file, try svg format
    * plantuml -tsvg sample.txt
    * svg is scalable vector format
* That's pretty much! Other options are for tweaking your class diagram.
* Other features like sequence diagram and EXCITING abstracted architecture diagram will be coming.
  * what is abstracted architecture diagram? That's what we're investigating. It's kind of research, so still vague now.
    
## Detailed Usage Options
* Try 'robinbird help' on command line.

 
