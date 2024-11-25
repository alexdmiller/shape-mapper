# Troubleshooting

## Library does not appear in the Contribution Manager

1. Please check that the value you have for your library name in the code is the same as the `libName` in
   the `build.gradle.kts` file. 
2. One possible issue is the location of your `sketchbook` folder. Processing stores installed libraries in 
   the `sketchbook` folder. Where your `sketchbook` folder is located depends on your operating system. You 
   can see where Gradle thinks your sketchbook folder is in the task run output. Check that the folder exists, 
   and contains the subfolder `libraries`. If not, you can check the sketchbook location in your Processing 
   application preferences.  Click on `File` > `Preferences`, and note the sketchbook folder location. If this 
   location is different from what's in the Gradle build file, you will need to input the value manually. Also, 
   please submit an issue to this repository.


## Dependency is not accessible in code

1. If a dependency is not accessible in your code, check for errors in the build window in 
   Intellij (hammer icon), or run `Tasks` > `build` > `build`, and look for an error message.
