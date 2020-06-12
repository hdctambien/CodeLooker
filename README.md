# Code Looker Atter
> A GUI to quickly visually inspect student code and grade it pass/fail

This program offers a graphical interface to iterate through student code and quickly assign it a pass/fail grade.

## Features

This project makes it easy to:
* Visually iterate over student assignments
* Assign pass/fail grades to student code after visually inspecting it

## Usage

Double click on the `/dist/CodeLooker.jar` file.

Browse for the folder that contains your student lab folders.

You can scroll through student folders using the `A` and `D` keys as well as the left and right arrow keys.

You can PASS the currently selected assignment using the `W` or up arrow key.

You can FAIL the currently selected assignment using the `S` or down arrow key.

There are also buttons you can use.

You can set the default file to open in the `File` text field. Type a file name in that field that if that file exists in the student folder, it will be the default file that opens.

## Structure of Student Code

If your students wrote their code in the file Foo.java then CodeCheker expects the student files to be stored in the following structure:

```
/Path/To/Labs/LabName/StudentName1/Foo.java
                     /StudentName2/Foo.java
                     /StudentName2/Foo.java
```

Conveniently, this folder structure is exactly the same as how the [Turn CS In](https://github.com/hdctambien/turncsin) web application stores student assignments.

## Saving Grades

Grades are saved in the selected lab folder in a file called `grades.csv` as you go.

## Building the Project

This is a simple java program with no external dependencies. Just compile it!

```
javac *.java
```

You can create runnable jar files after you compile the code by creating a file called `Manifest` with the following content

```
Manifest-Version: 1.0
Main-Class: CodeLooker
```

Then run the following command

```
jar cvfm CodeLooker.jar Manifest *.class
```

## Licensing

This project is licensed under MIT license. A short and simple permissive license with conditions only requiring preservation of copyright and license notices. Licensed works, modifications, and larger works may be distributed under different terms and without source code.
