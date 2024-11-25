# Getting Started
We're excited that you're interested in contributing to the Processing ecosystem! Building a library lets you expand Processing’s capabilities and it's a great way to give back by sharing your custom tools with the community.

This guide will help you set up your development environment and start building your library using the provided template.


## Setting up the environment
Follow these steps to create your own repository on GitHub and install the necessary tools.

1. **Create a new Github repository** using the [processing-library-template](https://github.com/processing/processing-library-template)
   as a base.
   (See: [How to create a repository from a template.](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template))
   Choose a name that reflects the library you're building. Make sure to read about Processing's [naming rules for libraries](https://github.com/benfry/processing4/wiki/Library-Basics#library-naming-rules). You can always rename the repository later.
2. **Clone your repository** to your local computer.
   ([How to clone a repository](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository))
3. **Install [Processing](https://processing.org/download)**, if it's not already installed on your computer.
4. **Install an integrated development editor (IDE)** such as [Intellij](https://www.jetbrains.com/idea/download), 
   or [Visual Studio Code](https://code.visualstudio.com/).
   Intellij comes fully featured, and has a free community version. For Visual Studio Code, you'll need to install additional extensions, such as Gradle for Java and Java language support.

## Testing the Library Template
Now that your environment is set up, follow these steps to get familiar with the template and make sure everything works correctly:

1. **Open up your new repository in your chosen IDE.**
2. **Run the Gradle task `deployToProcessingSketchbook`:** 
   - In the Gradle menu (the elephant icon) in your IDE, navigate to `Tasks` > `processing` > 
   `deployToProcessingSketchbook`, and double click on `deployToProcessingSketchbook`. 
   - This will build the library, create the release artifacts, and copy them to the folder 
   where Processing libraries are stored.
3. **Check if the library appears in the Contribution Manager:** 
   - Open Processing, and click on `Sketch` > `Import Library ...` > `Manage Libraries ...`. 
   This opens the Contribution Manager. 
   - You should see an entry named "A Template Example Library" with a check mark next to it. 
   This is the sample library you just installed using the template. 
   - If it does not appear, please check the [troubleshooting guide](troubleshooting.md).
4. **Customize the library information**:
   - Open the file `release.properties` and edit the `name`, `authors`, and `sentence` fields to match your library. 
   - Rerun the `deployToProcessingSketchbook` Gradle task to update the library information.
   - Open the Contribution Manager again to see your changes. The name, description, and author(s) should be updated.

## Next Steps
Great, now that you're familiar with the library template, you can explore additional guides:

1. [The development process](develop)
2. [Releasing your library](release)
3. [Troubleshooting](troubleshooting.md)
